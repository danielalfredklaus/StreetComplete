package ch.uzh.ifi.accesscomplete.reports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.uzh.ifi.accesscomplete.R
import ch.uzh.ifi.accesscomplete.reports.API.UzhQuest2


class MyRecyclerViewAdapter internal constructor(data: List<UzhQuest2>): ListAdapter<UzhQuest2, MyRecyclerViewAdapter.QuestViewHolder>(QuestComparator()) {
    val mData: List<UzhQuest2> = data


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        return QuestViewHolder.create(parent)
    }

    // binds the data to the TextView in each row
     override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.subtitle)
    }

    class QuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemView: TextView = itemView.findViewById(R.id.verify_text_item)
        fun bind(text: String?) {
            //itemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): QuestViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.temp_recycler_item, parent, false)
                return QuestViewHolder(view)
            }
        }
    }

    class QuestComparator : DiffUtil.ItemCallback<UzhQuest2>() {
        override fun areItemsTheSame(oldItem: UzhQuest2, newItem: UzhQuest2): Boolean {
            return oldItem.mid === newItem.mid
        }

        override fun areContentsTheSame(oldItem: UzhQuest2, newItem: UzhQuest2): Boolean {
            return oldItem.mid == newItem.mid
        }
    }

}
