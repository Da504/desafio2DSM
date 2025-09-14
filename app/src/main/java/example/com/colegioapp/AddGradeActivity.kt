package example.com.colegioapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import example.com.colegioapp.model.Grade
import example.com.colegioapp.model.Student

class AddGradeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var studentsRef: DatabaseReference
    private lateinit var gradesRef: DatabaseReference
    private lateinit var studentList: MutableList<Student>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_grade)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        studentsRef = database.getReference("students")
        gradesRef = database.getReference("grades")

        studentList = mutableListOf()

        val spStudent = findViewById<Spinner>(R.id.spStudent)
        val spGradeLevel = findViewById<Spinner>(R.id.spGradeLevel)
        val spSubject = findViewById<Spinner>(R.id.spSubject)
        val etFinalGrade = findViewById<EditText>(R.id.etFinalGrade)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)


        val gradeLevels = resources.getStringArray(R.array.grades_array)
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gradeLevels)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGradeLevel.adapter = gradeAdapter

        val subjects = resources.getStringArray(R.array.subjects_array)
        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSubject.adapter = subjectAdapter


        loadStudents(spStudent)

        btnSave.setOnClickListener {
            val selectedStudent = spStudent.selectedItem as? Student
            val gradeLevel = spGradeLevel.selectedItem.toString()
            val subject = spSubject.selectedItem.toString()
            val gradeText = etFinalGrade.text.toString().trim()


            val finalGrade = gradeText.replace(',', '.').toDoubleOrNull()


            if (selectedStudent == null || gradeLevel.isEmpty() || subject.isEmpty() || finalGrade == null || finalGrade !in 0.0..10.0) {
                Toast.makeText(this, R.string.invalid_grade, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val grade = Grade(
                studentId = selectedStudent.id,
                studentName = selectedStudent.name,
                gradeLevel = gradeLevel,
                subject = subject,
                finalGrade = finalGrade
            )

            val gradeId = gradesRef.push().key
            if (gradeId != null) {
                gradesRef.child(gradeId).setValue(grade)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.grade_added, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadStudents(spStudent: Spinner) {
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


                val studentAdapter = object : ArrayAdapter<Student>(
                    this@AddGradeActivity,
                    android.R.layout.simple_spinner_item,
                    studentList
                ) {
                    override fun getItem(position: Int): Student? {
                        return studentList[position]
                    }

                    override fun getCount(): Int {
                        return studentList.size
                    }

                    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                        val view = super.getView(position, convertView, parent)
                        (view as TextView).text = studentList[position].name
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                        val view = super.getDropDownView(position, convertView, parent)
                        (view as TextView).text = studentList[position].name
                        return view
                    }
                }
                studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spStudent.adapter = studentAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddGradeActivity, "Error cargando estudiantes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
