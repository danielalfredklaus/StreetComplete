package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.AbstractBottomSheetFragment
import ch.uzh.ifi.accesscomplete.quests.note_discussion.AttachPhotoFragment
import ch.uzh.ifi.accesscomplete.quests.width.AddWidthModular
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.dialog_barrier.*
import kotlinx.android.synthetic.main.dialog_barrier.view.*
import kotlinx.android.synthetic.main.dialog_barrier_visual.*
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.marker_create_note.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*


class BarrierMobilityFragment : AbstractBottomSheetFragment() {

    private val measureWidthForm: AddWidthModular
        get() = childFragmentManager.findFragmentById(R.id.barrier_mobility_AR_measurement) as AddWidthModular
    private val attachPhotoFragment: AttachPhotoFragment
        get() = childFragmentManager.findFragmentById(R.id.frame_fragment_attach_photo) as AttachPhotoFragment

    val layoutResId = R.layout.fragment_create_note
    private lateinit var imagePaths: List<String>
    private lateinit var barrierContent: View
    private var wheelchairQuestion: String = "not answered"
    private var changesMade: Boolean = false
    private val noteText get() = barrier_dialog_comment?.text?.toString().orEmpty().trim()
    private val widthInput get() = measureWidthForm.manualInputField?.text?.toString().orEmpty().trim()


    //var textArray = arrayOf("ya mom", "ya da")
    //var imageArray = arrayOf(R.drawable.ic_add_photo_black_24dp,R.drawable.quest_pin)

    interface Listener {
        fun onReportFinished(location: Location, stringList: ArrayList<String>)
    }
    private val listener: Listener? get() = parentFragment as? Listener
        ?: activity as? Listener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResId, container, false)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        val content = view.findViewById<ViewGroup>(R.id.content)
        content.removeAllViews()
        barrierContent = inflater.inflate(R.layout.dialog_barrier, content)

        val buttonPanel = view.findViewById<ViewGroup>(R.id.buttonPanel)
        buttonPanel.removeAllViews()
        val newPanel = inflater.inflate(R.layout.quest_buttonpanel_done_cancel, buttonPanel)

        var spinner = barrierContent.findViewById<Spinner>(R.id.barrier_mobility_spinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.mobility_barriers_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    ViewChangeOnItemSelected(parent.getItemAtPosition(position))

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            val wheelchairYes: RadioButton= view.findViewById(R.id.barrier_dialog_yes)
            val wheelchairNo: RadioButton= view.findViewById(R.id.barrier_dialog_no)
            val wheelchairUnsure: RadioButton= view.findViewById(R.id.barrier_dialog_unsure)

            //Sure you could do this in a better way...but is it worth the time?  Also Radiogroups don't work with Constraintlayouts
            wheelchairYes.setOnClickListener {
                    wheelchairYes.isChecked = true
                    wheelchairNo.isChecked = false
                    wheelchairUnsure.isChecked = false
                    wheelchairQuestion = "yes"
                    changesMade = true
            }
            wheelchairNo.setOnClickListener {
                wheelchairYes.isChecked = false
                wheelchairNo.isChecked = true
                wheelchairUnsure.isChecked = false
                wheelchairQuestion = "no"
                changesMade = true
            }
            wheelchairUnsure.setOnClickListener {
                wheelchairYes.isChecked = false
                wheelchairNo.isChecked = false
                wheelchairUnsure.isChecked = true
                wheelchairQuestion = "unsure"
                changesMade = true
            }

            return view
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.commit { add<AddWidthModular>(R.id.barrier_mobility_AR_measurement) }
            childFragmentManager.commit { add<AttachPhotoFragment>(R.id.frame_fragment_attach_photo) }
        }



        titleLabel.text = "TO DO Report permanent Barrier for mobility impaired"
        cancelButton.setOnClickListener { activity?.onBackPressed() }
        doneButton.setOnClickListener { onClickOk() }

    }

    var bundle : Bundle ? = null
    var location: Location? = null
    var mode: String? = ""

    override fun onStart() {
        super.onStart()
        bundle = this.arguments
        //If i dont clear out arguments then there will be an infinite cycle
        this.arguments = null
        if (bundle != null){
            mode = bundle?.getString("mode")
            location = bundle?.getParcelable<Location>("location")
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
        // I need barrier type, barrier width & barrier height, and the wheelchair info
    }

    val arList = arrayOf("Chain", "Block", "Hole","Cycle barrier", "Bollard")
    var barrierType = ""
    private fun ViewChangeOnItemSelected(itemAtPos: Any){
        barrierType = itemAtPos as String
        val arView = barrierContent.findViewById<FrameLayout>(R.id.barrier_mobility_AR_measurement)
        val chairQ = barrierContent.findViewById<ConstraintLayout>(R.id.barrier_mobility_constraintlayout_wheelchair)
        if(itemAtPos in arList) { arView.visibility = View.VISIBLE } else { arView.visibility = View.GONE }
        if (itemAtPos != "Turnstile"){ chairQ.visibility = View.GONE } else { chairQ.visibility = View.VISIBLE }
        when (itemAtPos) {
            "Choose a barrier"-> { changesMade = false }
            "Chain" -> { measureWidthForm.instructionsText.text = "Measure the height" }
            "Block", "Hole" -> { measureWidthForm.instructionsText.text = "Measure the width" }
            "Cycle barrier", "Bollard" -> { measureWidthForm.instructionsText.text = "Measure the width between barriers" }
            else -> {
            }
        }
    }

    private val TAG = "BarrierMobilityFragment"
    override fun onDiscard() {
        super.onDiscard()
        Log.i(TAG, "Fragment discarded")
        markerLayoutContainer?.visibility = View.INVISIBLE
        attachPhotoFragment?.deleteImages()
        Log.i(TAG, " "+ noteText.isNotEmpty() +" "+ ( barrierType in arList && widthInput.isNotEmpty() )+" "+ changesMade+" "+ attachPhotoFragment?.imagePaths?.isNotEmpty() )
    }

    override fun isRejectingClose() =
        noteText.isNotEmpty() || ( barrierType in arList && widthInput.isNotEmpty() )|| changesMade || attachPhotoFragment?.imagePaths?.isNotEmpty()

}
