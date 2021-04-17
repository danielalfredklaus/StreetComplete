package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import ch.uzh.ifi.accesscomplete.R

class SpinnerAdapter(private val ctx: Context, resource: Int, private val contentArray: Array<String>,
                     private val imageArray: Array<Int>) : ArrayAdapter<String?>(ctx, R.layout.barrier_mobility_spinner_layout, R.id.barrier_mobility_spinner_text_view, contentArray) {

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }


    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.barrier_mobility_spinner_layout, parent, false)
        //val imageView = row.findViewById<ImageView>(R.id.barrier_mobility_spinner_image_view)
        //imageView.setImageResource(imageArray[position])
        val textView = row.findViewById<TextView>(R.id.barrier_mobility_spinner_text_view)
        textView.text = contentArray[position]
        return row
    }
}
