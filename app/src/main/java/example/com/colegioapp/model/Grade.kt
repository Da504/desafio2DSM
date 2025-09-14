package example.com.colegioapp.model

data class Grade(
    var id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val gradeLevel: String = "",
    val subject: String = "",
    val finalGrade: Double = 0.0
)