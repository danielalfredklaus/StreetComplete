package ch.uzh.ifi.accesscomplete.reports

import android.content.res.Configuration
import android.graphics.Point
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.ktx.getLocationInWindow
import ch.uzh.ifi.accesscomplete.quests.AbstractBottomSheetFragment
import ch.uzh.ifi.accesscomplete.quests.note_discussion.AttachPhotoFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_quest_answer.*
import kotlinx.android.synthetic.main.marker_create_note.*
import kotlinx.android.synthetic.main.quest_buttonpanel_done_cancel.*

class ManualPositionFragment: AbstractBottomSheetFragment()  {

    interface Listener {
        fun openInBottomSheet(nextFragment: String, bundle: Bundle, position: Point)
    }
    private val listener: Listener? get() = parentFragment as? Listener
        ?: activity as? Listener

    val layoutResId = R.layout.fragment_create_note

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResId, container, false)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        val content = view.findViewById<ViewGroup>(R.id.content)
        content.removeAllViews()
        inflater.inflate(R.layout.dialog_position_manual, content)

        val buttonPanel = view.findViewById<ViewGroup>(R.id.buttonPanel)
        buttonPanel.removeAllViews()
        val newButtonPanel = inflater.inflate(R.layout.quest_buttonpanel_done_cancel, buttonPanel)
        val doneButton: Button = newButtonPanel.findViewById(R.id.doneButton)
        doneButton.text = "TO DO Use this location"
        val cancelButton: Button = newButtonPanel.findViewById(R.id.cancelButton)
        cancelButton.text = getString(R.string.back)

        val titleBubble = view.findViewById<LinearLayout>(R.id.speechBubbleTitleContainer)
        titleBubble.visibility = View.GONE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
        }

        cancelButton.setOnClickListener { activity?.onBackPressed() }
        doneButton.setOnClickListener { onClickOk() }


    }
    var bundle : Bundle ? = null
    var location: Location? = null
    var mode: String? = ""

    override fun onStart() {
        super.onStart()

    }


    private fun onClickOk(){

        bundle = this.arguments
        var nextFragment: String? = bundle?.getString("nextFragment")
        val newBundle = Bundle()
        val screenPos = createNoteMarker.getLocationInWindow()
        screenPos.offset(createNoteMarker.width / 2, createNoteMarker.height / 2)
        newBundle.putParcelable("pos",screenPos)
        newBundle.putString("mode", "manual")
        if(nextFragment != null) listener?.openInBottomSheet(nextFragment, newBundle, screenPos)
    }
}
