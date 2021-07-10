package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.map.MainFragment
import ch.uzh.ifi.accesscomplete.quests.AbstractBottomSheetFragment
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.westnordost.osmapi.map.data.LatLon
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*

class VerifyReportAnswerFragment2: AbstractBottomSheetFragment() {

    lateinit var bundle: Bundle
    lateinit var quest: UzhQuest2
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quest_answer, container, false)
        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }
        val buttonPanel = view.findViewById<ViewGroup>(R.id.buttonPanel)
        buttonPanel.removeAllViews()
        inflater.inflate(R.layout.quest_buttonpanel_done_cancel, buttonPanel)

        val ctx = requireContext()
        val relativeLayout = RelativeLayout(ctx)
        val rlp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        bundle = this.arguments ?: Bundle()
        val mf: MainFragment = parentFragment as MainFragment
        quest = mf.markerViewModel.allMapMarkers.value!!.find{ it.mid == bundle.getString("questMID")  } ?: mf.markerViewModel.allMapMarkers.value!![0]

        if(!quest.imageURL.isNullOrEmpty()){
            val imgView = ImageView(ctx)
            imgView.id = View.generateViewId()
            //TODO Show picture
        }

        //TODO Create Elements based on tags

        val content = view.findViewById<ViewGroup>(R.id.content)
        content.removeAllViews()
        content.addView(relativeLayout, rlp)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleLabel.text = "Verify Report for ${quest.subtitle}"
        cancelButton.setOnClickListener { activity?.onBackPressed() }
        doneButton.setOnClickListener { onClickOk() }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


    override fun isRejectingClose(): Boolean {
        //return super.isRejectingClose()
        return false
    }

    fun onClickOk(){

    }

}
