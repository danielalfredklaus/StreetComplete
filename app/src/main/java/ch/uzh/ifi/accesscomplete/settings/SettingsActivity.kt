package ch.uzh.ifi.accesscomplete.settings

import android.os.Bundle
import ch.uzh.ifi.accesscomplete.FragmentContainerActivity
import ch.uzh.ifi.accesscomplete.settings.questselection.QuestSelectionFragment

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
