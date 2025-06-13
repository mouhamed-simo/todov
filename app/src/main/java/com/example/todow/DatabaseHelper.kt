import com.example.todow.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    internal val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    fun addTask(titre: String, date: String, time: String, isDone: Boolean = false, onComplete: (Boolean) -> Unit) {
        if (currentUserId == null) {
            onComplete(false)
            return
        }
        val taskData = hashMapOf(
            "titre" to titre,
            "date" to date,
            "time" to time,
            "is_done" to isDone,
            "userId" to currentUserId
        )
        db.collection("tasks").add(taskData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteTask(taskId: String, onComplete: (Boolean) -> Unit) {
        db.collection("tasks").document(taskId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateTask(taskId: String, titre: String, date: String, time: String, isDone: Boolean, onComplete: (Boolean) -> Unit) {
        val updatedData = mapOf(
            "titre" to titre,
            "date" to date,
            "time" to time,
            "is_done" to isDone
        )
        db.collection("tasks").document(taskId).update(updatedData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateTaskStatus(taskId: String, isDone: Boolean, onComplete: (Boolean) -> Unit) {
        db.collection("tasks").document(taskId).update("is_done", isDone)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getTasks(onComplete: (List<Task>) -> Unit) {
        if (currentUserId == null) {
            onComplete(emptyList())
            return
        }
        db.collection("tasks").whereEqualTo("userId", currentUserId).get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.map { doc ->
                    Task(
                        id = doc.id,
                        titre = doc.getString("titre") ?: "",
                        date = doc.getString("date") ?: "",
                        time = doc.getString("time") ?: "",
                        is_done = doc.getBoolean("is_done") ?: false
                    )
                }
                onComplete(tasks)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }


    // دالة التسجيل (Sign Up) باستعمال Firebase Auth
    fun signUp(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)  // التسجيل ناجح
                } else {
                    onComplete(false, task.exception?.message)  // تسجيل فشل مع رسالة الخطأ
                }
            }
    }

    // دالة تسجيل الدخول (Login)
    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)  // تسجيل الدخول ناجح
                } else {
                    onComplete(false, task.exception?.message)  // فشل تسجيل الدخول مع رسالة الخطأ
                }
            }
    }

    // دالة تسجيل الخروج (Logout)
    fun logout() {
        auth.signOut()
    }
}

