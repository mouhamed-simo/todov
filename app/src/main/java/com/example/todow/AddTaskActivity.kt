package com.example.todow

import FirestoreHelper
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todow.databinding.ActivityAddTaskBinding
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var firestoreHelper: FirestoreHelper
    private var isEditMode = false
    private var taskId: String? = null
    private var isDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestoreHelper = FirestoreHelper()

        // إذا كان تعديل مهمة
        if (intent.hasExtra("task_id")) {
            isEditMode = true
            taskId = intent.getStringExtra("task_id")
            val titre = intent.getStringExtra("task_titre") ?: ""
            val date = intent.getStringExtra("task_date") ?: ""
            val time = intent.getStringExtra("task_time") ?: ""

            binding.editTitre.setText(titre)
            binding.editDate.setText(date)
            binding.editTime.setText(time)
        }

        // اختيار التاريخ
        binding.editDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.editDate.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        // اختيار الوقت
        binding.editTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.editTime.setText(selectedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        // عند الضغط على زر الحفظ
        binding.btnPushTask.setOnClickListener {
            val titre = binding.editTitre.text.toString()
            val date = binding.editDate.text.toString()
            val time = binding.editTime.text.toString()

            if (titre.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                if (isEditMode && taskId != null) {
                    firestoreHelper.updateTask(taskId!!, titre, date, time, isDone) { success ->
                        if (success) {
                            Toast.makeText(this, "تم تعديل المهمة بنجاح", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "فشل في تعديل المهمة", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    firestoreHelper.addTask(titre, date, time) { success ->
                        if (success) {
                            Toast.makeText(this, "تمت إضافة المهمة بنجاح", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "فشل في إضافة المهمة", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
            }
        }

        // عند الضغط على زر العودة
        binding.back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
