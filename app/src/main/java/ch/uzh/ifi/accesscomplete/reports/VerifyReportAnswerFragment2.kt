package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginLeft
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.map.MainFragment
import ch.uzh.ifi.accesscomplete.quests.AbstractBottomSheetFragment
import ch.uzh.ifi.accesscomplete.quests.note_discussion.AttachPhotoFragment
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2
import ch.uzh.ifi.accesscomplete.reports.API.VerifyingQuestEntity
import ch.uzh.ifi.accesscomplete.reports.database.MapMarker
import ch.uzh.ifi.accesscomplete.view.image_select.setImage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import de.westnordost.osmapi.map.data.LatLon
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*
import kotlinx.android.synthetic.main.quest_generic_list.*
import java.util.*

/**
 * BottomSheetFragment that provides a programmatically created View based on the Tags
 * available in the provided Quest
 */
class VerifyReportAnswerFragment2: AbstractBottomSheetFragment() {

    val TAG = "VerifyReportAnswerFragment2"
    lateinit var bundle: Bundle
    lateinit var quest: UzhQuest2
    lateinit var finalView: View
    var tagCounter = 0
    var counter = 0 //This one is for providing the View ID's

    interface Listener {
        fun onReportVerified(verification: VerifyingQuestEntity, imgList: List<String>)
    }
    private val listener: Listener? get() = parentFragment as? Listener
        ?: activity as? Listener

    private val attachPhotoFragment: AttachPhotoFragment
        get() = childFragmentManager.findFragmentById(R.id.frame_fragment_attach_photo) as AttachPhotoFragment

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
        val linearLayout = LinearLayout(ctx)
        val rlp5 : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = rlp5
        bundle = this.arguments ?: Bundle()
        val mf: MainFragment = parentFragment as MainFragment
        quest = mf.markerViewModel.allMapMarkers.value!!.find{ it.mid == bundle.getString("questMID")  } ?: mf.markerViewModel.allMapMarkers.value!![0]

        val rlp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        //rlp.setMargins(16,16,16,16)
        val imgView = ImageView(ctx)
        imgView.id = counter++
        imgView.visibility = View.GONE
        imgView.layoutParams = rlp
        //imgView.requestLayout()
        linearLayout.addView(imgView)
        //mutMap[counter++] = imgView.id
        if(!quest.imageURL?.filterNot { it.length < 5 }.isNullOrEmpty()){
            Log.d(TAG, "Image is visible")
            Picasso.get()
                .load(quest.imageURL!!.last())
                .error(R.drawable.ic_quest_road_construction)
                .into(imgView)
            imgView.visibility = View.VISIBLE
            //TODO Show picture
            //imgView.setImageResource(R.drawable.ic_achievement_surveyor)
        }
        //TODO Create Elements based on tags
        Log.d(TAG, quest.tags?.tags.toString())
        if(!quest.tags?.tags.isNullOrEmpty()){
            for (tag in quest.tags!!.tags){
                tagCounter++
                val keyText = TextView(ctx)
                keyText.text = tag.k.replace("_"," ").capitalize(Locale.ROOT)
                keyText.id = counter++
                //mutMap[counter++] = keyText.id
                val rlp3 : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                //rlp3.addRule(LinearLayout.BELOW, counter - 1)
                //rlp3.setMargins(16,16,0,0)
                keyText.layoutParams = rlp3
                keyText.requestLayout()
                linearLayout.addView(keyText)

                val valueText = TextView(ctx)
                valueText.text = tag.v
                valueText.id = counter++ //View.generateViewId()
                //mutMap[counter++] = valueText.id
                val rlp4 : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                //rlp4.addRule(LinearLayout.BELOW, counter - 1)
                //rlp4.setMargins(16,16,0,0)
                valueText.layoutParams = rlp4
                valueText.requestLayout()
                linearLayout.addView(valueText)

                val radioGroup = RadioGroup(ctx)
                val llp6: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                radioGroup.layoutParams = llp6
                radioGroup.id = counter++
                radioGroup.orientation = RadioGroup.HORIZONTAL
                val correct = RadioButton(ctx)
                correct.id = counter++
                correct.text = "Correct"
                radioGroup.addView(correct)
                val wrong = RadioButton(ctx)
                wrong.id = counter++
                wrong.text = "Wrong"
                radioGroup.addView(wrong)
                val unsure = RadioButton(ctx)
                unsure.id = counter++
                unsure.text = "Unsure"
                radioGroup.addView(unsure)
                linearLayout.addView(radioGroup)

            }
        }

        val noteText = EditText(ctx)
        noteText.hint = " Comment about any values amiss, add your own or what you think is important to mention."
        val llp69 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        noteText.layoutParams = llp69
        noteText.id = counter //Remember no increase in value here
        linearLayout.addView(noteText)

        val photoFragment = FrameLayout(ctx)
        photoFragment.id = R.id.frame_fragment_attach_photo
        val llp420 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        photoFragment.layoutParams = llp420
        linearLayout.addView(photoFragment)
        val content = view.findViewById<ViewGroup>(R.id.content)
        content.removeAllViews()
        content.addView(linearLayout)
        content.requestLayout()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.commit { add<AttachPhotoFragment>(R.id.frame_fragment_attach_photo) }
        }

        val titleString = "Are these Values correct for ${quest.subtitle}?"
        titleLabel.text = titleString
        cancelButton.setOnClickListener { activity?.onBackPressed() }
        doneButton.setOnClickListener { onClickOk() }
        finalView = view
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
        var cunt = 1
        var fullDescription = "" //Will be set for verification.description, containing user comments and their answers to the values
        if (counter >= 1){
            while(cunt < counter -1){
                fullDescription += finalView.findViewById<TextView>(cunt).text.toString() + ": "   //Now that I think of it, I couldve done the view finding with TAGS instead of a simple counter for id :3
                fullDescription += finalView.findViewById<TextView>(cunt + 1).text.toString() + " marked "     //Probably wouldve been easier to understand than working with a counter
                val tempRG = finalView.findViewById<RadioGroup>(cunt+2)
                fullDescription += finalView.findViewById<RadioButton>(tempRG.checkedRadioButtonId).text.toString() + " - "
                cunt += 6

            }


        }
        fullDescription += "User comment: " + finalView.findViewById<EditText>(counter).text.toString()
        //attachPhotoFragment.imagePaths
        Log.d(TAG, "Description created: $fullDescription")
        val verif = VerifyingQuestEntity("",quest.mid,fullDescription, "")
        listener?.onReportVerified(verif, attachPhotoFragment.imagePaths)


    }

}
