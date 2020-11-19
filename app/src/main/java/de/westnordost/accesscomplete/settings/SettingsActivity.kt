package de.westnordost.accesscomplete.settings

import android.os.Bundle
import de.westnordost.accesscomplete.FragmentContainerActivity
import de.westnordost.accesscomplete.settings.questselection.QuestSelectionFragment

class SettingsActivity : FragmentContainerActivity(), SettingsFragment.Listener
{
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (savedInstanceState == null) {
            mainFragment = SettingsFragment()
        }
    }

    override fun onClickedQuestSelection() {
        pushMainFragment(QuestSelectionFragment())
    }
}
