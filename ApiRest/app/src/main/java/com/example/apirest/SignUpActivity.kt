package com.example.apirest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.apirest.alumno.AlumnoActivity
import com.google.firebase.auth.FirebaseAuth

private lateinit var e_mailInput: EditText
private lateinit var password_Input: EditText
private lateinit var repeatPasswordInput: EditText
private lateinit var buttonRegister: Button
private lateinit var auth: FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        e_mailInput = findViewById(R.id.SignupEmail)
        password_Input = findViewById(R.id.signupPassword)
        repeatPasswordInput = findViewById(R.id.signupRepeatPassword)
        buttonRegister = findViewById(R.id.signupButton)

        buttonRegister.setOnClickListener {
            var email = e_mailInput.text.toString()
            var password = password_Input.text.toString()
            var repeatPassword = repeatPasswordInput.text.toString()
            if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != repeatPassword) {
                Log.i("tag", password)
                Log.i("tag", repeatPassword)
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // create user with email and password in firebase and upload the rest of the data to firestore
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("tag", "createUserWithEmail:success")
                        val intent = Intent(this, AlumnoActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("tag", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}