package example.com.colegioapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val btnStudents = findViewById<Button>(R.id.btnStudents)
        val btnGrades = findViewById<Button>(R.id.btnGrades)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnStudents.setOnClickListener {
            startActivity(Intent(this, StudentListActivity::class.java))
        }

        btnGrades.setOnClickListener {
            startActivity(Intent(this, GradeListActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}