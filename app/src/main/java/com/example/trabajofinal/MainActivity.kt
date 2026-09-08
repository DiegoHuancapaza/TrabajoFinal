package com.example.trabajofinal
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val etEmail = findViewById<TextInputEditText>(R.id.editTextTextEmailAddress)
        val etPassword = findViewById<TextInputEditText>(R.id.editTextTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        // Acción del Botón Iniciar Sesión
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Validar campos vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Comprobar credenciales en la base de datos SQLite
            val isValidUser = dbHelper.checkUser(email, password)

            if (isValidUser) {
                Toast.makeText(this, "¡Bienvenido! Sesión iniciada con éxito", Toast.LENGTH_SHORT).show()
                // Aquí podrías dirigir al usuario a la pantalla principal de tu app
                // startActivity(Intent(this, HomeActivity::class.java))
                // finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        // Navegar a la pantalla de Registro
        tvGoToRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}