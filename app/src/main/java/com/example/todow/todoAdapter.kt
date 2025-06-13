package com.example.todow

import FirestoreHelper
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class todoAdapter(
    val context: Context,
    var listTasks: List<Task>,
    private val firestoreHelper: FirestoreHelper
) : RecyclerView.Adapter<todoAdapter.ViewAdapter>() {

    inner class ViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvtitle = itemView.findViewById<TextView>(R.id.tvtitre)
        val tvdate = itemView.findViewById<TextView>(R.id.tvdate)
        val tvtime = itemView.findViewById<TextView>(R.id.tvtime)
        val imageCheck = itemView.findViewById<ImageButton>(R.id.imageCheck)
        val imageDelete = itemView.findViewById<ImageButton>(R.id.btnDelete)
        val imageModifier = itemView.findViewById<ImageButton>(R.id.btnModifier)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAdapter {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false)
        return ViewAdapter(view)
    }

    override fun onBindViewHolder(holder: ViewAdapter, position: Int) {
        val task = listTasks[position]
        holder.tvtitle.text = task.titre
        holder.tvtime.text = "${task.date} , ${task.time}"

        val imageResource = if (task.is_done) {
            R.drawable.checkbox
        } else {
            R.drawable.checkboxvide
        }
        holder.imageCheck.setImageResource(imageResource)
        holder.tvtitle.paintFlags = if (task.is_done) {
            holder.tvtitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvtitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.imageCheck.setOnClickListener {
            val newStatus = !task.is_done
            firestoreHelper.updateTaskStatus(task.id, newStatus) { success ->
                if (success) {
                    task.is_done = newStatus
                    notifyItemChanged(position)
                } else {
                    Toast.makeText(context, "فشل في تحديث المهمة", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.imageDelete.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.alert_delete, null)
            alert.setView(view)
            val dialog = alert.create()

            view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btn_delete).setOnClickListener {
                firestoreHelper.deleteTask(task.id) { success ->
                    if (success) {
                        listTasks = listTasks.filter { it.id != task.id }
                        notifyDataSetChanged()
                        Toast.makeText(context, "تم حذف المهمة بنجاح", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "فشل في حذف المهمة", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

        holder.imageModifier.setOnClickListener {
            val intent = Intent(context, AddTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
                putExtra("task_titre", task.titre)
                putExtra("task_date", task.date)
                putExtra("task_time", task.time)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listTasks.size

    fun refreshData(newTasks: List<Task>) {
        listTasks = newTasks
        notifyDataSetChanged()
    }
}
