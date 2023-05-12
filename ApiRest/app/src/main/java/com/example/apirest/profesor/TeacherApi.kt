package com.example.apirest.profesor

import com.example.apirest.model.teacher
import retrofit2.Call
import retrofit2.http.*

interface TeacherApi {

    @GET("profesor.php")
    fun obtenerTeachers(): Call<List<Teacher>>

    @GET("profesor.php")
    fun obtenerTeacherPorId(@Path("id") id: Int): Call<Teacher>

    @POST("profesor.php")
    fun crearTeacher(@Body teacher: Teacher): Call<Teacher>

    @PUT("profesor.php")
    fun actualizarTeacher( @Body teacher: Teacher): Call<Teacher>

    @PUT("profesor.php")
    fun eliminarTeacher( @Body teacher: Teacher): Call<Void>
}