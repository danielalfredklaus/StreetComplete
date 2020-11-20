/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.settings

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.*
import ch.uzh.ifi.accesscomplete.ktx.toBcp47LanguageTag
import ch.uzh.ifi.accesscomplete.ktx.toast
import kotlinx.android.synthetic.main.fragment_oauth.*
import kotlinx.coroutines.*
import oauth.signpost.OAuthConsumer
import oauth.signpost.OAuthProvider
import oauth.signpost.exception.OAuthCommunicationException
import oauth.signpost.exception.OAuthExpectationFailedException
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/** Fragment that manages the OAuth 1 authentication process in a webview*/
class OAuthFragment : Fragment(R.layout.fragment_oauth),
    BackPressedListener,
    HasTitle,
    CoroutineScope by CoroutineScope(Dispatchers.Main)
{
    @Inject internal lateinit var consumerProvider: Provider<OAuthConsumer>
    @Inject internal lateinit var provider: OAuthProvider
    @Inject @field:Named("OAuthCallbackScheme") internal lateinit var callbackScheme: String
    @Inject @field:Named("OAuthCallbackHost") internal lateinit var callbackHost: String

    interface Listener {
        fun onOAuthSuccess(consumer: OAuthConsumer)
        fun onOAuthFailed(e: Exception?)
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener
    private val callbackUrl get() = "$callbackScheme://$callbackHost"
    private val webViewClient: OAuthWebViewClient = OAuthWebViewClient()

    override val title: String get() = getString(R.string.user_login)

    private lateinit var consumer: OAuthConsumer
    private var authorizeUrl: String? = null
    private var oAuthVerifier: String? = null

    init {
        Injector.applicationComponent.inject(this)
    }

    /* --------------------------------------- Lifecycle --------------------------------------- */

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)
        if (inState != null) {
            consumer = inState.getSerializable(CONSUMER) as OAuthConsumer
            authorizeUrl = inState.getString(AUTHORIZE_URL)
            oAuthVerifier = inState.getString(OAUTH_VERIFIER)
        } else {
            consumer = consumerProvider.get()
            authorizeUrl = null
            oAuthVerifier = null
        }
        launch { continueAuthentication() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.userAgentString = ApplicationConstants.USER_AGENT
        webView.settings.javaScriptEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.setSupportZoom(false)
        webView.webViewClient = webViewClient
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onBackPressed(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CONSUMER, consumer)
        outState.putString(AUTHORIZE_URL, authorizeUrl)
        outState.putString(OAUTH_VERIFIER, oAuthVerifier)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    /* ------------------------------------------------------------------------------------------ */

    private suspend fun continueAuthentication() {
        try {
            if (authorizeUrl == null) {
                progressView?.visibility = View.VISIBLE
                authorizeUrl = withContext(Dispatchers.IO) {
                    provider.retrieveRequestToken(consumer, callbackUrl)
                }
                progressView?.visibility = View.INVISIBLE
            }
            if (authorizeUrl != null && oAuthVerifier == null) {
                webView.visibility = View.VISIBLE
                webView.loadUrl(
                    authorizeUrl,
                    mutableMapOf("Accept-Language" to Locale.getDefault().toBcp47LanguageTag())
                )
                oAuthVerifier = webViewClient.awaitOAuthCallback()
                webView.visibility = View.INVISIBLE
            }
            if (oAuthVerifier != null) {
                progressView?.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    provider.retrieveAccessToken(consumer, oAuthVerifier)
                }
                listener?.onOAuthSuccess(consumer)
                progressView?.visibility = View.INVISIBLE
            }
        }
        catch (e: Exception) {
            activity?.toast(R.string.oauth_communication_error, Toast.LENGTH_LONG)
            Log.e(TAG, "Error during authorization", e)
            listener?.onOAuthFailed(e)
        }
    }

    /* ---------------------------------------------------------------------------------------- */

    companion object {
        const val TAG = "OAuthDialogFragment"

        private const val CONSUMER = "consumer"
        private const val AUTHORIZE_URL = "authorize_url"
        private const val OAUTH_VERIFIER = "oauth_verifier"
    }

    private inner class OAuthWebViewClient : WebViewClient() {

        private var continutation: Continuation<String>? = null
        suspend fun awaitOAuthCallback(): String = suspendCoroutine {continutation = it }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val uri = url?.toUri() ?: return false
            if (!uri.isHierarchical) return false
            if (uri.scheme != callbackScheme || uri.host != callbackHost) return false
            val verifier = uri.getQueryParameter(OAUTH_VERIFIER)
            if (verifier != null) {
                continutation?.resume(verifier)
            } else {
                continutation?.resumeWithException(
                    OAuthExpectationFailedException("oauth_verifier parameter not set by provider")
                )
            }
            return true
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, url: String?) {
            continutation?.resumeWithException(
                OAuthCommunicationException("Error for URL $url","$description")
            )
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            progressView?.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            progressView?.visibility = View.INVISIBLE
        }
    }
}
