package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import ch.uzh.ifi.accesscomplete.R
import kotlinx.android.synthetic.main.dialog_report_button.view.*


public class ReportDialog(context: Context) : AlertDialog(context, R.style.Theme_Bubble_Dialog){
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_report_button, null)
        view.layout_barrier.setOnClickListener{
            context?.let { BarrierDialog(it).show() }
            this.dismiss()
        }
        view.layout_construction.setOnClickListener{
            context?.let { ConstructionDialog(it).show() }
            this.dismiss()
        }
        view.layout_other_issue.setOnClickListener{
            toastyBoi()
            this.dismiss()
        }
        setView(view)
    }

    private fun toastyBoi(){
        val toast = Toast.makeText(context, "Clicked a button", Toast.LENGTH_SHORT)
        toast.setMargin(50f, 50f)
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show()
    }
}
