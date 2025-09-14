package example.com.colegioapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import example.com.colegioapp.adapter.StudentAdapter
import example.com.colegioapp.model.Student

class StudentListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var studentsRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddStudent: Button
    private lateinit var btnBack: Button
    private lateinit var studentList: MutableList<Student>
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        Log.d("DEBUG", "StudentListActivity onCreate called")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        studentsRef = database.getReference("students")

        // RecyclerView
        recyclerView = findViewById(R.id.rvStudents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Botones
        btnAddStudent = findViewById(R.id.btnAddStudent)
        btnBack = findViewById(R.id.btnBack)

        // Lista y adapter
        studentList = mutableListOf()
        adapter = StudentAdapter(studentList) { student ->
            val intent = Intent(this, EditStudentActivity::class.java)
            intent.putExtra("STUDENT_ID", student.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Botón Agregar estudiante
        btnAddStudent.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
        }

        // Botón Atrás
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cierra esta actividad
        }

        // Cargar estudiantes
        loadStudents()
    }

    private fun loadStudents() {
        studentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    student?.let {
                        it.id = studentSnapshot.key ?: ""
                        studentList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                Log.d("DEBUG", "Loaded ${studentList.size} students")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DEBUG", "Error loading students: ${error.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d("DEBUG", "StudentListActivity onResume called")
        loadStudents()
    }

    override fun onStart() {
        super.onStart()
        loadStudents()
    }

    override fun onStop() {
        super.onStop()
        // Opcional: detener listeners si es necesario
    }
}
