package example.com.colegioapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import example.com.colegioapp.model.Student

class AddStudentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etAge)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val age = etAge.text.toString().trim().toIntOrNull() ?: 0
            val address = etAddress.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || age <= 0 || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val student = Student(
                name = name,
                age = age,
                address = address,
                phone = phone
            )

            val studentsRef = database.getReference("students")
            val studentId = studentsRef.push().key

            if (studentId != null) {
                studentsRef.child(studentId).setValue(student)
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.student_added, Toast.LENGTH_SHORT).show()
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
}