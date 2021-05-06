package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.os.Bundle
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
import kotlinx.android.synthetic.main.dialog_barrier.view.*
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*


class BarrierMobilityFragment : AbstractBottomSheetFragment() {

    private val measureWidthForm: AddWidthModular
        get() = childFragmentManager.findFragmentById(R.id.barrier_mobility_AR_measurement) as AddWidthModular
    private val attachPhotoFragment: AttachPhotoFragment
        get() = childFragmentManager.findFragmentById(R.id.frame_fragment_attach_photo) as AttachPhotoFragment

    val layoutResId = R.layout.fragment_create_note
    private lateinit var imagePaths: List<String>
    private lateinit var barrierContent: View
    //var textArray = arrayOf("ya mom", "ya da")
    //var imageArray = arrayOf(R.drawable.ic_add_photo_black_24dp,R.drawable.quest_pin)



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
        inflater.inflate(R.layout.quest_buttonpanel_done_cancel, buttonPanel)


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


    private fun onClickOk(){

    }

    private fun ViewChangeOnItemSelected(itemAtPos: Any){
        when (itemAtPos) {
            "chain" -> {barrierContent.findViewById<FrameLayout>(R.id.barrier_mobility_AR_measurement).visibility = View.VISIBLE
                measureWidthForm.instructionsText.text = "Measure the height"
            }
            "block", "hole" -> { barrierContent.findViewById<FrameLayout>(R.id.barrier_mobility_AR_measurement).visibility = View.VISIBLE
                measureWidthForm.instructionsText.text = "Measure the width" }
            "cycle barrier", "bollard" -> { barrierContent.findViewById<FrameLayout>(R.id.barrier_mobility_AR_measurement).visibility = View.VISIBLE
                measureWidthForm.instructionsText.text = "Measure the width between barriers"}
            "turnstile" -> { barrierContent.findViewById<ConstraintLayout>(R.id.barrier_mobility_constraintlayout_wheelchair).visibility = View.VISIBLE
            barrierContent.findViewById<FrameLayout>(R.id.barrier_mobility_AR_measurement).visibility = View.GONE }
            else -> {

            }
        }
        if (itemAtPos != "turnstile"){
            barrierContent.findViewById<ConstraintLayout>(R.id.barrier_mobility_constraintlayout_wheelchair).visibility = View.GONE
        }

    }

}
