package com.example.apirest.profesor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apirest.MainActivity
import com.example.apirest.R
import com.example.apirest.alumno.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TeacherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherAdapter
    private lateinit var api: TeacherApi

    // Obtener las credenciales de autenticación
    val auth_username = "admin"
    val auth_password = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        val fab_agregar: FloatingActionButton = findViewById<FloatingActionButton>(R.id.fab_agregar_teacher)

        recyclerView = findViewById(R.id.recyclerView_teacher)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Crea un cliente OkHttpClient con un interceptor que agrega las credenciales de autenticación
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", Credentials.basic(auth_username, auth_password))
                    .build()
                chain.proceed(request)
            }
            .build()

        // Crea una instancia de Retrofit con el cliente OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.5:80/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Crea una instancia del servicio que utiliza la autenticación HTTP básica
        api = retrofit.create(TeacherApi::class.java)

        cargarDatos(api)

        // Cuando el usuario quiere agregar un nuevo registro
        fab_agregar.setOnClickListener(View.OnClickListener {
            val i = Intent(getBaseContext(), CrearTeacherActivity::class.java)
            i.putExtra("auth_username", auth_username)
            i.putExtra("auth_password", auth_password)
            startActivity(i)
        })
    }

    override fun onResume() {
        super.onResume()
        cargarDatos(api)
    }

    private fun cargarDatos(api: TeacherApi) {
        val call = api.obtenerTeachers()
        call.enqueue(object : Callback<List<Teacher>> {
            override fun onResponse(call: Call<List<Teacher>>, response: Response<List<Teacher>>) {
                if (response.isSuccessful) {
                    val teachers = response.body()
                    if (teachers != null) {
                        adapter = TeacherAdapter(teachers)
                        recyclerView.adapter = adapter

                        // Establecemos el escuchador de clics en el adaptador
                        adapter.setOnItemClickListener(object : TeacherAdapter.OnItemClickListener {
                            override fun onItemClick(teacher: Teacher) {
                                val opciones = arrayOf("Modificar Maestro", "Eliminar Maestro")

                                AlertDialog.Builder(this@TeacherActivity)
                                    .setTitle(teacher.nombre)
                                    .setItems(opciones) { dialog, index ->
                                        when (index) {
                                            0 -> Modificar(teacher)
                                            1 -> eliminarAlumno(teacher, api)
                                        }
                                    }
                                    .setNegativeButton("Cancelar", null)
                                    .show()
                            }
                        })
                    }
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al obtener los maestros: $error")
                    Toast.makeText(
                        this@TeacherActivity,
                        "Error al obtener los maestros 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Teacher>>, t: Throwable) {
                Log.e("API", "Error al obtener los maestros: ${t.message}")
                Toast.makeText(
                    this@TeacherActivity,
                    "Error al obtener los maestros 2",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    private fun Modificar(teacher: Teacher) {
        // Creamos un intent para ir a la actividad de actualización de alumnos
        val i = Intent(getBaseContext(), ActualizarTeacherActivity::class.java)
        // Pasamos el ID del alumno seleccionado a la actividad de actualización
        i.putExtra("profesor_id", teacher.id)
        i.putExtra("nombre", teacher.nombre)
        i.putExtra("apellido", teacher.apellido)
        i.putExtra("edad", teacher.edad)
        i.putExtra("asignatura", teacher.asignatura)
        // Iniciamos la actividad de actualización de alumnos
        startActivity(i)
    }

    private fun eliminarAlumno(teacher: Teacher, api: TeacherApi) {
        val teacherTMP = Teacher(teacher.id,"", "", -987,"")
        Log.e("API", "id : $teacher")
        val llamada = api.eliminarTeacher( teacherTMP)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TeacherActivity, "Profesor eliminado", Toast.LENGTH_SHORT).show()
                    cargarDatos(api)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al eliminar alumno : $error")
                    Toast.makeText(this@TeacherActivity, "Error al eliminar profesor 1", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar alumno : $t")
                Toast.makeText(this@TeacherActivity, "Error al eliminar profesor 2", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_alumno, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_alumnos -> {
                val intent = Intent(this@TeacherActivity, AlumnoActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_logout->{
                logout(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun logout(item: MenuItem) {
        FirebaseAuth.getInstance().signOut().also {
            val intent = Intent(this@TeacherActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}