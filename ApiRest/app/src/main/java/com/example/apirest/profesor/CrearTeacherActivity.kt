package com.example.apirest.profesor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.apirest.R
import com.example.apirest.alumno.Alumno
import com.example.apirest.alumno.AlumnoActivity
import com.example.apirest.alumno.AlumnoApi
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CrearTeacherActivity : AppCompatActivity() {
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var asignaturaEditText: EditText
    private lateinit var crearButton: Button

    var auth_username = ""
    var auth_password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_teacher)

        val datos: Bundle? = intent.getExtras()
        if (datos != null) {
            auth_username = datos.getString("auth_username").toString()
            auth_password = datos.getString("auth_password").toString()
        }

        nombreEditText = findViewById(R.id.editTextNombre)
        apellidoEditText = findViewById(R.id.editTextApellido)
        edadEditText = findViewById(R.id.editTextEdad)
        asignaturaEditText = findViewById(R.id.editTextAsignatura)
        crearButton = findViewById(R.id.btnGuardar)

        crearButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val edad = edadEditText.text.toString().toInt()
            val asignatura = asignaturaEditText.text.toString()

            val teacher = Teacher(0,nombre, apellido, edad,asignatura)
            Log.e("API", "auth_username: $auth_username")
            Log.e("API", "auth_password: $auth_password")

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
                .baseUrl("http://192.168.100.5/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            // Crea una instancia del servicio que utiliza la autenticación HTTP básica
            val api = retrofit.create(TeacherApi::class.java)

            api.crearTeacher(teacher).enqueue(object : Callback<Teacher> {
                override fun onResponse(call: Call<Teacher>, response: Response<Teacher>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CrearTeacherActivity, "Profesor creado exitosamente", Toast.LENGTH_SHORT).show()
                        val i = Intent(getBaseContext(), TeacherActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("API", "Error crear alumno: $error")
                        Toast.makeText(this@CrearTeacherActivity, "Error al crear el profesor", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<Teacher>, t: Throwable) {
                    Toast.makeText(this@CrearTeacherActivity, "Error al crear el profesor", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}