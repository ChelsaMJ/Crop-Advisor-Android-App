package com.example.cropadvisorai.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cropadvisorai.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

data class AgriChecklistItem(val id: Long, val title: String, val description: String, var isChecked: Boolean = false)

class AgriChecklistFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: AgriChecklistAdapter
    private val items = mutableListOf<AgriChecklistItem>()
    private var idCounter = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agri_checklist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.checklistRecyclerView)
        fab = view.findViewById(R.id.fab_add_agri)

        // initial sample items
        items.addAll(createInitialAgriChecklist())

        adapter = AgriChecklistAdapter(items.toMutableList(),
            onCheck = { item, position ->
                // remove checked item
                adapter.removeAt(position)
                Snackbar.make(requireView(), "Completed: ${item.title}", Snackbar.LENGTH_SHORT).show()
            },
            onLongPress = { item, pos ->
                // simple undo example (optional)
                Snackbar.make(requireView(), "${item.title}", Snackbar.LENGTH_SHORT)
                    .setAction("Undo") {
                        adapter.insertAt(pos, item)
                    }.show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fab.setOnClickListener { showAddItemDialog() }
    }

    private fun createInitialAgriChecklist(): List<AgriChecklistItem> {
        return listOf(
            newItem("Verify Soil pH with Lab Test", "Collect samples from 5 locations and send to lab"),
            newItem("Schedule weekly rainfall checks", "Check local station or rainfall app"),
            newItem("Room DB: write DAOs for crop records", "Add entities and test migrations"),
            newItem("Test coroutines for async fetching", "Create sample repo and fetch remote data"),
            newItem("Test Fused Location on device", "Request permission and sample coordinates")
        )
    }

    private fun newItem(title: String, desc: String): AgriChecklistItem {
        idCounter += 1
        return AgriChecklistItem(idCounter, title, desc, false)
    }

    private fun showAddItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_agri_item, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.editTitle)
        val descInput = dialogView.findViewById<EditText>(R.id.editDescription)

        AlertDialog.Builder(requireContext())
            .setTitle("Add reminder")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val title = titleInput.text.toString().trim()
                val desc = descInput.text.toString().trim()
                if (title.isNotEmpty()) {
                    val item = newItem(title, desc)
                    adapter.insertAt(0, item)
                    recyclerView.scrollToPosition(0)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .show()
    }
}
