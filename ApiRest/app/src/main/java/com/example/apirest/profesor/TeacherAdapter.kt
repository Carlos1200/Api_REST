package com.example.apirest.profesor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.R

class TeacherAdapter(private val teachers: List<Teacher>) : RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    private var onItemClick: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.tvNombre)
        val apellidoTextView: TextView = view.findViewById(R.id.tvApellido)
        val edadTextView: TextView = view.findViewById(R.id.tvEdad)
        val asignaturaTextView: TextView = view.findViewById(R.id.tvAsignatura)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.teacher_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val teacher = teachers[position]
        holder.nombreTextView.text = teacher.nombre
        holder.apellidoTextView.text = teacher.apellido
        holder.edadTextView.text = teacher.edad.toString()
        holder.asignaturaTextView.text = teacher.asignatura

        // Agrega el escuchador de clics a la vista del elemento de la lista
        holder.itemView.setOnClickListener {
            onItemClick?.onItemClick(teacher)
        }
    }

    override fun getItemCount(): Int {
        return teachers.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClick = listener
    }

    interface OnItemClickListener {
        fun onItemClick(teacher: Teacher)
    }
}