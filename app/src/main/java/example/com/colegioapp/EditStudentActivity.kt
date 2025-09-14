package example.com.colegioapp

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import example.com.colegioapp.model.Student

class EditStudentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var studentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        studentId = intent.getStringExtra("STUDENT_ID") ?: ""

        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etAge)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        loadStudentData(etName, etAge, etAddress, etPhone)

        btnUpdate.setOnClickListener {
            val name = etName.text.toString().trim()
            val age = etAge.text.toString().trim().toIntOrNull() ?: 0
            val address = etAddress.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || age <= 0 || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val student = Student(
                id = studentId,
                name = name,
                age = age,
                address = address,
                phone = phone
            )

            val studentsRef = database.getReference("students")
            studentsRef.child(studentId).setValue(student)
                .addOnSuccessListener {
                    Toast.makeText(this, R.string.student_updated, Toast.LENGTH_SHORT).show()
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

    private fun loadStudentData(
        etName: EditText,
        etAge: EditText,
        etAddress: EditText,
        etPhone: EditText
    ) {
        val studentsRef = database.getReference("students").child(studentId)
        studentsRef.get().addOnSuccessListener { snapshot ->
            val student = snapshot.getValue(Student::class.java)
            student?.let {
                etName.setText(it.name)
                etAge.setText(it.age.toString())
                etAddress.setText(it.address)
                etPhone.setText(it.phone)
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setPositiveButton(R.string.yes) { _: DialogInterface, _: Int ->
                deleteStudent()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteStudent() {
        val studentsRef = database.getReference("students").child(studentId)
        studentsRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, R.string.student_deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}