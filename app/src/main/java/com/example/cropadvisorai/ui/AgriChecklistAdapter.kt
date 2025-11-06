package com.example.cropadvisorai.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cropadvisorai.R

class AgriChecklistAdapter(
    val items: MutableList<AgriChecklistItem>,
    private val onCheck: (AgriChecklistItem, Int) -> Unit,
    private val onLongPress: (AgriChecklistItem, Int) -> Unit
) : RecyclerView.Adapter<AgriChecklistAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_agri_checklist, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.desc.text = item.description
        holder.checkBox.isChecked = item.isChecked

        // clicking checkbox completes the task (remove)
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = false // always start unchecked in UI to prompt user to intentionally check
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // remove item and notify
                onCheck(item, position)
            }
        }

        holder.itemView.setOnLongClickListener {
            onLongPress(item, position)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun removeAt(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun insertAt(position: Int, item: AgriChecklistItem) {
        val pos = position.coerceIn(0, items.size)
        items.add(pos, item)
        notifyItemInserted(pos)
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val desc: TextView = view.findViewById(R.id.itemDesc)
        val checkBox: CheckBox = view.findViewById(R.id.itemCheckBox)
    }
}
