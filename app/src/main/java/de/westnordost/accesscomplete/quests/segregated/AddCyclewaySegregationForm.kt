package de.westnordost.accesscomplete.quests.segregated

import android.os.Bundle
import android.view.View

import de.westnordost.accesscomplete.R
import de.westnordost.accesscomplete.quests.AImageListQuestAnswerFragment
import de.westnordost.accesscomplete.view.image_select.Item

class AddCyclewaySegregationForm : AImageListQuestAnswerFragment<Boolean, Boolean>() {

    override val items get() = listOf(
        Item(true, if (countryInfo.isLeftHandTraffic) R.drawable.ic_path_segregated_l else R.drawable.ic_path_segregated, R.string.quest_segregated_separated),
        Item(false, R.drawable.ic_path_segregated_no, R.string.quest_segregated_mixed)
    )

    override val itemsPerRow = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageSelector.cellLayoutId  = R.layout.cell_labeled_icon_select_right
    }

    override fun onClickOk(selectedItems: List<Boolean>) {
        applyAnswer(selectedItems.single())
    }
}
