package ch.uzh.ifi.accesscomplete.quests.width

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.measurement.ARCoreMeasurementActivity
import ch.uzh.ifi.accesscomplete.util.checkIfTalkBackIsActive
import kotlinx.android.synthetic.main.quest_width.*
import java.lang.NumberFormatException
import kotlin.math.roundToInt

//Just a modified copy of Svens AddWidthForm.kt
class AddWidthModular : Fragment() {

    val contentLayoutResId = R.layout.quest_width
    private var measureButton : Button? = null
    var manualInputField : EditText? = null
    lateinit var instructionsText : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.quest_width, container, false)

        measureButton = view.findViewById(R.id.measureButton)
        manualInputField = view.findViewById(R.id.manualInputField)
        instructionsText = view.findViewById(R.id.quest_width_measurement_instructions)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInputFields()
        //checkIsFormComplete()
        ARCoreMeasurementActivity.checkIsSupportedDevice(requireActivity()) { disableARCoreMeasurementIfNotSupported() }
    }

    private fun initInputFields() {
        // The ARCore measurement is not accessible for screen reader users, hence the option will
        // be removed if TalkBack is active.
        if (checkIfTalkBackIsActive(requireContext())) {
            measureButton?.visibility = View.GONE
        } else {
            measureButton?.setOnClickListener {
                val intent = Intent(activity?.application, ARCoreMeasurementActivity::class.java)
                intent.putExtra(ARCoreMeasurementActivity.EXTRA_ADDITIONAL_INSTRUCTIONS_ID, R.string.quest_width_measurement_instructions)
                intent.putExtra(ARCoreMeasurementActivity.EXTRA_ADDITIONAL_INSTRUCTIONS_IMAGE_ID, R.drawable.example_width)
                startActivityForResult(intent, ARCoreMeasurementActivity.REQUEST_CODE_MEASURE_DISTANCE)
            }
        }
        /*
        manualInputField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // NOP
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // NOP
            }

            override fun afterTextChanged(s: Editable?) {
                checkIsFormComplete()
            }
        }) */
    }

    private fun disableARCoreMeasurementIfNotSupported() {
        measureButton?.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ARCoreMeasurementActivity.REQUEST_CODE_MEASURE_DISTANCE) {
            if (resultCode == Activity.RESULT_OK) {
                val distance = data!!.getFloatExtra((ARCoreMeasurementActivity.RESULT_ATTRIBUTE_DISTANCE), -1f)
                if (distance >= 0) {
                    manualInputField?.setText((distance * 100).roundToInt().toString())
                }
            }
        }
    }

    fun isFormComplete(): Boolean = manualInputField!!.text.isNotEmpty()
    fun resetInputs() {
        manualInputField?.text = null
    }

    private fun parseWidthInMeters(): Float {
        var widthValueInCm = 0
        val input = manualInputField?.text.toString()
        try {
            widthValueInCm = Integer.parseInt(input)
        } catch (e: NumberFormatException) {
            manualInputField?.text = null
        }

        return widthValueInCm / 100f
    }



}
