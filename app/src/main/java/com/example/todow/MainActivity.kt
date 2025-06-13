package com.example.todow

import FirestoreHelper
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todow.Fragment.CompletedTasks
import com.example.todow.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: todoAdapter
    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var auth: FirebaseAuth
    private var tasksList = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تغيير لون status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        auth = FirebaseAuth.getInstance()

        // التأكد من تسجيل الدخول
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        firestoreHelper = FirestoreHelper()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.DrawerLayout

        adapter = todoAdapter(this, tasksList, firestoreHelper)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.Close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.menu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.completedTasks -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CompletedTasks())
                        .addToBackStack(null)
                        .commit()
                    binding.recyclerView.visibility = View.GONE
                    binding.noTask.visibility = View.GONE
                }
                R.id.addTasks2 -> {
                    startActivity(Intent(this, AddTaskActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("isFirstTime", true)
        if (isFirstTime) {
            startActivity(Intent(this, ExplainActivity::class.java))
            finish()
            return
        }

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // تحميل المهام من Firestore عند بداية التشغيل
        loadTasksFromFirestore()
    }

    // جلب المهام من Firestore وتحديث الواجهة
    private fun loadTasksFromFirestore() {
        firestoreHelper.getTasks { tasks ->
            runOnUiThread {
                tasksList.clear()
                tasksList.addAll(tasks)
                adapter.refreshData(tasksList)
                toggleNoTaskView(tasksList.isEmpty())
            }
        }
    }

    private fun toggleNoTaskView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.noTask.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.noTask.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        binding.toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            R.id.deleteAllTask -> {
                AlertDialog.Builder(this)
                    .setTitle("Delete All Tasks")
                    .setMessage("Are you sure you want to delete all tasks?")
                    .setPositiveButton("Yes") { _, _ ->
                        deleteAllTasksFromFirestore()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            R.id.logout -> {
                AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTasksFromFirestore() {
        // حذف كل المهام واحدة واحدة (Firestore لا يدعم حذف مجموعة كاملة دفعة واحدة)
        if (tasksList.isEmpty()) return

        var deleteCount = 0
        for (task in tasksList) {
            firestoreHelper.deleteTask(task.id) { success ->
                deleteCount++
                if (deleteCount == tasksList.size) {
                    // بعد حذف جميع المهام، تحديث الواجهة
                    loadTasksFromFirestore()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasksFromFirestore()
    }
}
