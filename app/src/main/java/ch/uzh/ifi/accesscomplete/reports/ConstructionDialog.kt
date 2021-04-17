package ch.uzh.ifi.accesscomplete.reports

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.*
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.quests.note_discussion.AttachPhotoFragment
import kotlinx.android.synthetic.main.dialog_construction.*
import kotlinx.android.synthetic.main.dialog_construction.view.*
import kotlinx.android.synthetic.main.dialog_report_button.view.*
import ch.uzh.ifi.accesscomplete.ktx.getLocationInWindow
import kotlinx.android.synthetic.main.fragment_main.*


class ConstructionDialog (context: Context) : AlertDialog(context, R.style.Theme_Bubble_Dialog){

    init {
        var chairAnswer = ""
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_construction, null)
        view.construction_dialog_passage_yes.setOnClickListener(){
            view.construction_dialog_passage_yes.isChecked = true
            view.construction_dialog_passage_no.isChecked = false
            chairAnswer = "yes"
        }
        view.construction_dialog_passage_no.setOnClickListener(){
            view.construction_dialog_passage_yes.isChecked = false
            view.construction_dialog_passage_no.isChecked = true
            chairAnswer = "no"
        }
        /*
        view.construction_dialog_ok.setOnClickListener(){
            val constructionDate = view.construction_dialog_date_input.text
            val comment = view.construction_dialog_comment.text
            val definiteAnswer = chairAnswer
            val location = view?.getLocationInWindow()
            val toast = Toast.makeText(context, "$constructionDate $comment $definiteAnswer $location", Toast.LENGTH_SHORT)
            toast.setMargin(50f, 50f)
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show()
            this.dismiss()
        }
        view.construction_dialog_cancel.setOnClickListener(){
            this.dismiss()
        } */
        setView(view)

    }

}
