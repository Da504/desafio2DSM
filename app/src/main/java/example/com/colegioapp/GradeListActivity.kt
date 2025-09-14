package example.com.colegioapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import example.com.colegioapp.adapter.GradeAdapter
import example.com.colegioapp.model.Grade

class GradeListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var gradesRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddGrade: Button
    private lateinit var btnBack: Button
    private lateinit var gradeList: MutableList<Grade>
    private lateinit var adapter: GradeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade_list)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        gradesRef = database.getReference("grades")

        recyclerView = findViewById(R.id.rvGrades)
        btnAddGrade = findViewById(R.id.btnAddGrade)
        btnBack = findViewById(R.id.btnBack)

        recyclerView.layoutManager = LinearLayoutManager(this)

        gradeList = mutableListOf()
        adapter = GradeAdapter(gradeList) { grade ->
            val intent = Intent(this, EditGradeActivity::class.java)
            intent.putExtra("GRADE_ID", grade.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Botón Agregar nota
        btnAddGrade.setOnClickListener {
            startActivity(Intent(this, AddGradeActivity::class.java))
        }

        // Botón Atrás
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadGrades()
    }

    private fun loadGrades() {
        gradesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gradeList.clear()
                for (gradeSnapshot in snapshot.children) {
                    val grade = gradeSnapshot.getValue(Grade::class.java)
                    grade?.let {
                        it.id = gradeSnapshot.key ?: ""
                        gradeList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error si es necesario
            }
        })
    }
}
