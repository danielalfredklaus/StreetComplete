package ch.uzh.ifi.accesscomplete.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ch.uzh.ifi.accesscomplete.ApplicationConstants
import ch.uzh.ifi.accesscomplete.BuildConfig
import ch.uzh.ifi.accesscomplete.R

/** Shows the about screen */
class AboutFragment : PreferenceFragmentCompat() {

    interface Listener {
        fun onClickedChangelog()
        fun onClickedCredits()
        fun onClickedPrivacyStatement()
    }
    private val listener: Listener? get() = parentFragment as? Listener ?: activity as? Listener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.about)

        findPreference<Preference>("version")?.summary = getString(R.string.about_summary_current_version, "v" + BuildConfig.VERSION_NAME)
        findPreference<Preference>("version")?.setOnPreferenceClickListener {
            listener?.onClickedChangelog()
            true
        }

        findPreference<Preference>("license")?.setOnPreferenceClickListener {
            openUrl("https://www.gnu.org/licenses/gpl-3.0.html")
        }

        findPreference<Preference>("authors")?.setOnPreferenceClickListener {
            listener?.onClickedCredits()
            true
        }

        findPreference<Preference>("privacy")?.setOnPreferenceClickListener {
            listener?.onClickedPrivacyStatement()
            true
        }

        findPreference<Preference>("repository")?.setOnPreferenceClickListener {
            openUrl("https://github.com/svstoll/StreetComplete/")
        }

        findPreference<Preference>("report_error")?.setOnPreferenceClickListener {
            openUrl("https://github.com/svstoll/StreetComplete/issues/")
        }

        findPreference<Preference>("email_feedback")?.setOnPreferenceClickListener {
            sendFeedbackEmail()
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.setTitle(R.string.action_about2)
    }

    private fun openUrl(url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        return tryStartActivity(intent)
    }

    private fun sendFeedbackEmail(): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sven.stoll@uzh.ch"))
        intent.putExtra(Intent.EXTRA_SUBJECT, ApplicationConstants.USER_AGENT + " Feedback")
        return tryStartActivity(intent)
    }

    private fun tryStartActivity(intent: Intent): Boolean {
        return try {
            startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
