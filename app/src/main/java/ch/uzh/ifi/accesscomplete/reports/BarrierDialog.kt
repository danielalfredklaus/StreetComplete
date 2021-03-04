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
import kotlinx.android.synthetic.main.dialog_barrier.*
import kotlinx.android.synthetic.main.dialog_barrier.view.*
import kotlinx.android.synthetic.main.dialog_report_button.view.*
import ch.uzh.ifi.accesscomplete.ktx.getLocationInWindow
import kotlinx.android.synthetic.main.fragment_main.*


class BarrierDialog (context: Context) : AlertDialog(context, R.style.Theme_Bubble_Dialog){

    init {
        var chairAnswer = ""
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_barrier, null)
        view.barrier_dialog_yes.setOnClickListener(){
            view.barrier_dialog_yes.isChecked = true
            view.barrier_dialog_no.isChecked = false
            view.barrier_dialog_unsure.isChecked = false
            chairAnswer = "yes"
        }
        view.barrier_dialog_no.setOnClickListener(){
            view.barrier_dialog_yes.isChecked = false
            view.barrier_dialog_no.isChecked = true
            view.barrier_dialog_unsure.isChecked = false
            chairAnswer = "no"
        }
        view.barrier_dialog_unsure.setOnClickListener(){
            view.barrier_dialog_yes.isChecked = false
            view.barrier_dialog_no.isChecked = false
            view.barrier_dialog_unsure.isChecked = true
            chairAnswer = "unsure"
        }
        view.barrier_dialog_ok.setOnClickListener(){
            val barriertype = view.barrier_dialog_barriertype_input.text
            val comment = view.barrier_dialog_comment.text
            val definiteAnswer = chairAnswer
            val location = view?.getLocationInWindow()
            val toast = Toast.makeText(context, "$barriertype $comment $definiteAnswer $location", Toast.LENGTH_SHORT)
            toast.setMargin(50f, 50f)
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show()
            this.dismiss()
        }
        view.barrier_dialog_cancel.setOnClickListener(){
            this.dismiss()
        }
        setView(view)

    }

}
