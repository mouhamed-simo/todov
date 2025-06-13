package com.example.todow.splach

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todow.R
import com.example.todow.Task

class CompleteTaskAdapter(
    private val context: Context,
    private var listCompleteTask: List<Task>
) : RecyclerView.Adapter<CompleteTaskAdapter.CompleteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false)
        return CompleteViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompleteViewHolder, position: Int) {
        val task = listCompleteTask[position]
        holder.tvTitle.text = task.titre
        holder.tvDateTime.text = "${task.date} - ${task.time}"
    }

    override fun getItemCount(): Int = listCompleteTask.size

    fun refreshData(newList: List<Task>) {
        listCompleteTask = newList
        notifyDataSetChanged()
    }

    class CompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvtitre)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvtime)
    }
}
