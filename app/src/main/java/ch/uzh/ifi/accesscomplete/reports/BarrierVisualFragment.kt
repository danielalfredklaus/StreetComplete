package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AbstractBottomSheetFragment
import ch.uzh.ifi.accesscomplete.quests.note_discussion.AttachPhotoFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*

class BarrierVisualFragment: AbstractBottomSheetFragment()  {

    private val attachPhotoFragment: AttachPhotoFragment
        get() = childFragmentManager.findFragmentById(R.id.frame_fragment_attach_photo) as AttachPhotoFragment
    
    val layoutResId = R.layout.fragment_create_note


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResId, container, false)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        val content = view.findViewById<ViewGroup>(R.id.content)
        content.removeAllViews()
        inflater.inflate(R.layout.dialog_barrier_visual, content)

        val buttonPanel = view.findViewById<ViewGroup>(R.id.buttonPanel)
        buttonPanel.removeAllViews()
        inflater.inflate(R.layout.quest_buttonpanel_done_cancel, buttonPanel)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.commit { add<AttachPhotoFragment>(R.id.frame_fragment_attach_photo) }
        }

        titleLabel.text = "TO DO Report permanent Barrier for visually impaired"
        cancelButton.setOnClickListener { activity?.onBackPressed() }
        doneButton.setOnClickListener { onClickOk() }

        bundle = this.arguments
        //If i dont clear out arguments then there will be an infinite cycle
        this.arguments = null
        if (bundle != null){
            mode = bundle?.getString("mode")
            location = bundle?.getParcelable<Location>("location")
            if(mode == "photo") {
                var fm = parentFragmentManager
                fm.beginTransaction().hide(this).commit()
            }
        }
    }
    var bundle : Bundle ? = null
    var location: Location? = null
    var mode: String? = ""

    override fun onStart() {
        super.onStart()
        if (bundle != null){
            if(mode == "photo") {
                attachPhotoFragment.takePhoto()
                mode = "wasPhoto"
                //var fm = parentFragmentManager
                //fm.beginTransaction().show(this).commit()
            }

        }

    }

    private fun onClickOk(){
        val toast = Toast.makeText(context, "Location is ${location?.latitude},${location?.longitude}", Toast.LENGTH_LONG)
        toast.show()
    }
}
