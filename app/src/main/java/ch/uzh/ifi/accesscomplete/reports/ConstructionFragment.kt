package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AbstractBottomSheetFragment
import ch.uzh.ifi.accesscomplete.quests.incline.AddInclineForm
import ch.uzh.ifi.accesscomplete.quests.incline.AddInclineFormModular
import ch.uzh.ifi.accesscomplete.quests.note_discussion.AttachPhotoFragment
import ch.uzh.ifi.accesscomplete.quests.width.AddWidthModular
import ch.uzh.ifi.accesscomplete.util.TextChangedWatcher
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.dialog_construction.*
import kotlinx.android.synthetic.main.form_leave_note.*
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*
import kotlinx.android.synthetic.main.quest_incline.*

class ConstructionFragment: AbstractBottomSheetFragment() {

    private val attachPhotoFragment: AttachPhotoFragment
        get() = childFragmentManager.findFragmentById(R.id.frame_fragment_attach_photo) as AttachPhotoFragment
    private val measureWidthForm: AddWidthModular
        get() = childFragmentManager.findFragmentById(R.id.construction_dialog_passage_width) as AddWidthModular
    private val measureWidthForm2: AddWidthModular
        get() = childFragmentManager.findFragmentById(R.id.construction_dialog_bypass_width) as AddWidthModular
    private val measureSlopeForm: AddInclineFormModular
        get() = childFragmentManager.findFragmentById(R.id.construction_dialog_passage_slope) as AddInclineFormModular
    private val measureSlopeForm2: AddInclineFormModular
        get() = childFragmentManager.findFragmentById(R.id.construction_dialog_bypass_slope) as AddInclineFormModular

    private val commentText: String? = null
    val layoutResId = R.layout.fragment_create_note
    var newContent: View? = null
    private var manualInputField: EditText? = null
    private var manualInputField2: EditText? = null
    private var slopeValue: Int = 0
    private var slopeValue2: Int = 0
    private lateinit var imagePaths: List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResId, container, false)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        val content = view.findViewById<ViewGroup>(R.id.content)
        content.removeAllViews()
        newContent = inflater.inflate(R.layout.dialog_construction, content)

        val buttonPanel = view.findViewById<ViewGroup>(R.id.buttonPanel)
        buttonPanel.removeAllViews()
        inflater.inflate(R.layout.quest_buttonpanel_done_cancel, buttonPanel)

        return view
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)

         if (savedInstanceState == null) {
             childFragmentManager.commit { add<AttachPhotoFragment>(R.id.frame_fragment_attach_photo) }
             childFragmentManager.commit { add<AddWidthModular>(R.id.construction_dialog_passage_width) }
             childFragmentManager.commit { add<AddWidthModular>(R.id.construction_dialog_bypass_width) }
             childFragmentManager.commit { add<AddInclineFormModular>(R.id.construction_dialog_passage_slope) }
             childFragmentManager.commit { add<AddInclineFormModular>(R.id.construction_dialog_bypass_slope) }
         }

         //noteInput.addTextChangedListener(TextChangedWatcher { updateDoneButtonEnablement() })

         //titleLabel.text = getString(R.string.map_btn_create_note)
         titleLabel.text = "TO DO Report Construction site"
         cancelButton.setOnClickListener { activity?.onBackPressed() }
         doneButton.setOnClickListener { onClickOk() }

         construction_dialog_linearlayout_passage_yes.visibility = View.GONE
         construction_dialog_linearlayout_bypass_yes.visibility = View.GONE
         construction_dialog_linearlayout_passage_yes.setBackgroundColor(Color.parseColor("#b7f5b5"))
         construction_dialog_linearlayout_bypass_yes.setBackgroundColor(Color.parseColor("#b7f5b5"))

         construction_dialog_passage_yes.setOnClickListener(){
             construction_dialog_passage_yes.isChecked = true
             construction_dialog_passage_no.isChecked = false
             construction_dialog_linearlayout_passage_yes.visibility = View.VISIBLE
         }
         construction_dialog_passage_no.setOnClickListener(){
             construction_dialog_passage_yes.isChecked = false
             construction_dialog_passage_no.isChecked = true
             construction_dialog_linearlayout_passage_yes.visibility = View.GONE
         }

         construction_dialog_bypass_yes.setOnClickListener(){
             construction_dialog_bypass_yes.isChecked = true
             construction_dialog_bypass_no.isChecked = false
             construction_dialog_linearlayout_bypass_yes.visibility = View.VISIBLE

         }
         construction_dialog_bypass_no.setOnClickListener(){
             construction_dialog_bypass_yes.isChecked = false
             construction_dialog_bypass_no.isChecked = true
             construction_dialog_linearlayout_bypass_yes.visibility = View.GONE
         }



         //updateDoneButtonEnablement()
     }

    private fun onClickOk() {
        //onComposedNote(noteText, attachPhotoFragment?.imagePaths)
        //TO DO
        //var widthValue = manualInputField?.text.toString()
        imagePaths = attachPhotoFragment.imagePaths
        //From the width form, i get the textfield which contains text which I need as Int :)
        var widthValue = measureWidthForm.manualInputField!!.text.toString()
        var widthValue2 = measureWidthForm2.manualInputField!!.text.toString()
        measureSlopeForm.onClickOk()
        measureSlopeForm2.onClickOk()
        val answer = if(measureSlopeForm.inclineView.locked) measureSlopeForm.answer else ""
        val answer2 = if(measureSlopeForm2.inclineView.locked) measureSlopeForm.answer else ""
        val toast = Toast.makeText(context, "Clicked OK, width measured was ( $widthValue & $widthValue2 ), slope measured was ( $answer & $answer2 ), and image paths are $imagePaths", Toast.LENGTH_LONG)
        toast.show()
    }

    private fun updateDoneButtonEnablement() {
        //doneButton.isEnabled = noteText.isNotEmpty()
        //
    }

    //What both classes need: Title text, Location, Camera,Comment
}
