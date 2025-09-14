package example.com.colegioapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import example.com.colegioapp.R
import example.com.colegioapp.model.Grade

class GradeAdapter(
    private val grades: List<Grade>,
    private val onItemClick: (Grade) -> Unit
) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>() {

    class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvGradeLevel: TextView = itemView.findViewById(R.id.tvGradeLevel)
        val tvSubject: TextView = itemView.findViewById(R.id.tvSubject)
        val tvFinalGrade: TextView = itemView.findViewById(R.id.tvFinalGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grade, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val grade = grades[position]
        holder.tvStudentName.text = "Estudiante: ${grade.studentName}"
        holder.tvGradeLevel.text = "Grado: ${grade.gradeLevel}"
        holder.tvSubject.text = "Materia: ${grade.subject}"
        holder.tvFinalGrade.text = "Nota: ${grade.finalGrade}"

        holder.itemView.setOnClickListener {
            onItemClick(grade)
        }
    }

    override fun getItemCount(): Int = grades.size
}