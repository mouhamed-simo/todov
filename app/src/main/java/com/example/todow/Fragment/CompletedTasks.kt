package com.example.todow.Fragment

import FirestoreHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todow.R
import com.example.todow.Task
import com.example.todow.todoAdapter

class CompletedTasks : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: todoAdapter
    private lateinit var firestoreHelper: FirestoreHelper
    private var completedTasks: MutableList<Task> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed_tasks, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewComplete)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        firestoreHelper = FirestoreHelper()

        // تحميل المهام المكتملة من Firestore
        firestoreHelper.getTasks { allTasks ->
            completedTasks.clear()
            completedTasks.addAll(allTasks.filter { it.is_done})
            adapter = todoAdapter(requireContext(), completedTasks, firestoreHelper)
            recyclerView.adapter = adapter
        }

        return view
    }
}
