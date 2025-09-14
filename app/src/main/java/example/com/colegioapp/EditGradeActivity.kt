package example.com.colegioapp

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import example.com.colegioapp.model.Grade
import example.com.colegioapp.model.Student

class EditGradeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var studentsRef: DatabaseReference
    private lateinit var gradesRef: DatabaseReference
    private lateinit var gradeId: String
    private lateinit var studentList: MutableList<Student>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_grade)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        studentsRef = database.getReference("students")
        gradesRef = database.getReference("grades")

        gradeId = intent.getStringExtra("GRADE_ID") ?: ""
        studentList = mutableListOf()

        val spStudent = findViewById<Spinner>(R.id.spStudent)
        val spGradeLevel = findViewById<Spinner>(R.id.spGradeLevel)
        val spSubject = findViewById<Spinner>(R.id.spSubject)
        val etFinalGrade = findViewById<EditText>(R.id.etFinalGrade)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        // Configurar spinners
        val gradeLevels = resources.getStringArray(R.array.grades_array)
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gradeLevels)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGradeLevel.adapter = gradeAdapter

        val subjects = resources.getStringArray(R.array.subjects_array)
        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSubject.adapter = subjectAdapter

        // Cargar estudiantes
        loadStudents(spStudent)

        // Cargar datos de la nota
        loadGradeData(spStudent, spGradeLevel, spSubject, etFinalGrade)

        btnUpdate.setOnClickListener {
            val selectedStudent = spStudent.selectedItem as? Student
            val gradeLevel = spGradeLevel.selectedItem.toString()
            val subject = spSubject.selectedItem.toString()
            val gradeText = etFinalGrade.text.toString().trim()

            // Convertir coma a punto y parsear Double
            val finalGrade = gradeText.replace(',', '.').toDoubleOrNull()

            // Validación de nota
            if (selectedStudent == null || gradeLevel.isEmpty() || subject.isEmpty() || finalGrade == null || finalGrade !in 0.0..10.0) {
                Toast.makeText(this, R.string.invalid_grade, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto Grade actualizado
            val grade = Grade(
                id = gradeId,
                studentId = selectedStudent.id,
                studentName = selectedStudent.name,
                gradeLevel = gradeLevel,
                subject = subject,
                finalGrade = finalGrade
            )

            gradesRef.child(gradeId).setValue(grade)
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.grade_updated, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
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

                // Adapter usando objetos Student
                val studentAdapter = object : ArrayAdapter<Student>(
                    this@EditGradeActivity,
                    android.R.layout.simple_spinner_item,
                    studentList
                ) {
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
                Toast.makeText(this@EditGradeActivity, "Error cargando estudiantes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadGradeData(
        spStudent: Spinner,
        spGradeLevel: Spinner,
        spSubject: Spinner,
        etFinalGrade: EditText
    ) {
        gradesRef.child(gradeId).get().addOnSuccessListener { snapshot ->
            val grade = snapshot.getValue(Grade::class.java)
            grade?.let {
                // Seleccionar estudiante
                for (i in studentList.indices) {
                    if (studentList[i].id == grade.studentId) {
                        spStudent.setSelection(i)
                        break
                    }
                }

                // Seleccionar nivel
                val gradeLevels = resources.getStringArray(R.array.grades_array)
                for (i in gradeLevels.indices) {
                    if (gradeLevels[i] == grade.gradeLevel) {
                        spGradeLevel.setSelection(i)
                        break
                    }
                }

                // Seleccionar materia
                val subjects = resources.getStringArray(R.array.subjects_array)
                for (i in subjects.indices) {
                    if (subjects[i] == grade.subject) {
                        spSubject.setSelection(i)
                        break
                    }
                }

                // Mostrar nota
                etFinalGrade.setText(grade.finalGrade.toString())
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                deleteGrade()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteGrade() {
        gradesRef.child(gradeId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, R.string.grade_deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
