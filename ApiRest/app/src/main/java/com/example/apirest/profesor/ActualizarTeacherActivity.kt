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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import okhttp3.Credentials
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActualizarTeacherActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var asignaturaEditText: EditText
    private lateinit var actualizarButton: Button


    // Obtener las credenciales de autenticación
    val auth_username = "admin"
    val auth_password = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_teacher)

        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        edadEditText = findViewById(R.id.edadEditText)
        actualizarButton = findViewById(R.id.actualizarButton)
        asignaturaEditText = findViewById(R.id.asignaturaEditText)

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

        // Obtener el ID del alumno de la actividad anterior
        val teacherId = intent.getIntExtra("profesor_id", -1)
        Log.e("API", "teacherId : $teacherId")

        val nombre = intent.getStringExtra("nombre").toString()
        val apellido = intent.getStringExtra("apellido").toString()
        val edad = intent.getIntExtra("edad", 1)
        val asignatura = intent.getStringExtra("asignatura").toString()

        nombreEditText.setText(nombre)
        apellidoEditText.setText(apellido)
        edadEditText.setText(edad.toString())
        asignaturaEditText.setText(asignatura)

        val teacher = Teacher(teacherId,nombre, apellido, edad, asignatura)

        actualizarButton.setOnClickListener {
            if (teacher != null) {
                // Crear un nuevo objeto Alumno con los datos actualizados
                val teacherActualizado = Teacher(
                    teacherId,
                    nombreEditText.text.toString(),
                    apellidoEditText.text.toString(),
                    edadEditText.text.toString().toInt(),
                    asignaturaEditText.text.toString()
                )
                //Log.e("API", "alumnoActualizado : $alumnoActualizado")

                val jsonTeacherActualizado = Gson().toJson(teacherActualizado)
                Log.d("API", "JSON enviado: $jsonTeacherActualizado")

                val gson = GsonBuilder()
                    .setLenient() // Agrega esta línea para permitir JSON malformado
                    .create()

                // Realizar una solicitud PUT para actualizar el objeto Alumno
                api.actualizarTeacher(teacherActualizado).enqueue(object : Callback<Teacher> {
                    override fun onResponse(call: Call<Teacher>, response: Response<Teacher>) {
                        if (response.isSuccessful && response.body() != null) {
                            // Si la solicitud es exitosa, mostrar un mensaje de éxito en un Toast
                            Toast.makeText(this@ActualizarTeacherActivity, "Profesor actualizado correctamente", Toast.LENGTH_SHORT).show()
                            val i = Intent(getBaseContext(), TeacherActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            // Si la respuesta del servidor no es exitosa, manejar el error
                            try {
                                val errorJson = response.errorBody()?.string()
                                val errorObj = JSONObject(errorJson)
                                val errorMessage = errorObj.getString("message")
                                Toast.makeText(this@ActualizarTeacherActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Si no se puede parsear la respuesta del servidor, mostrar un mensaje de error genérico
                                Toast.makeText(this@ActualizarTeacherActivity, "Error al actualizar el teacher", Toast.LENGTH_SHORT).show()
                                Log.e("API", "Error al parsear el JSON: ${e.message}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Teacher>, t: Throwable) {
                        // Si la solicitud falla, mostrar un mensaje de error en un Toast
                        Log.e("API", "onFailure : $t")
                        Toast.makeText(this@ActualizarTeacherActivity, "Error al actualizar el teacher", Toast.LENGTH_SHORT).show()

                        // Si la respuesta JSON está malformada, manejar el error
                        try {
                            val gson = GsonBuilder().setLenient().create()
                            val error = t.message ?: ""
                            val alumno = gson.fromJson(error, Teacher::class.java)
                            // trabajar con el objeto Alumno si se puede parsear
                        } catch (e: JsonSyntaxException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        } catch (e: IllegalStateException) {
                            Log.e("API", "Error al parsear el JSON: ${e.message}")
                        }
                    }
                })
            }
        }
    }
}