package de.westnordost.accesscomplete.controls

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnPreDraw
import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.about.AboutActivity
import de.westnordost.accesscomplete.settings.SettingsActivity
import de.westnordost.accesscomplete.user.UserActivity
import kotlinx.android.synthetic.main.dialog_main_menu.view.*

/** Shows a dialog containing the main menu items */
class MainMenuDialog(
    context: Context,
    onClickDownload: () -> Unit
) : AlertDialog(context, R.style.Theme_Bubble_Dialog) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_main_menu, null)

        view.profileButton.setOnClickListener {
            val intent = Intent(context, UserActivity::class.java)
            context.startActivity(intent)
            dismiss()
        }
        view.settingsButton.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
            dismiss()
        }
        view.aboutButton.setOnClickListener {
            val intent = Intent(context, AboutActivity::class.java)
            context.startActivity(intent)
            dismiss()
        }
        view.downloadButton.setOnClickListener {
            onClickDownload()
            dismiss()
        }

        view.doOnPreDraw {
            view.bigMenuItemsContainer.columnCount = view.width / view.profileButton.width
        }

        setView(view)
    }
}
