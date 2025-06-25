package com.deliciaapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nombresField = findViewById<EditText>(R.id.editTextNombres)
        val apellidosField = findViewById<EditText>(R.id.editTextApellidos)
        val telefonoField = findViewById<EditText>(R.id.editTextTelefono)
        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val passwordField = findViewById<EditText>(R.id.editTextPassword)
        val confirmField = findViewById<EditText>(R.id.editTextConfirmPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        val loginText = findViewById<TextView>(R.id.textLogin)

        registerButton.setOnClickListener {
            val nombres = nombresField.text.toString()
            val apellidos = apellidosField.text.toString()
            val telefono = telefonoField.text.toString()
            val email = emailField.text.toString()
            val pass = passwordField.text.toString()
            val confirm = confirmField.text.toString()

            if (nombres.isBlank() || apellidos.isBlank() || telefono.isBlank() || email.isBlank() || pass.isBlank() || confirm.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val cliente = hashMapOf(
                        "nombres" to nombres,
                        "apellidos" to apellidos,
                        "telefono" to telefono,
                        "email" to email
                    )

                    db.collection("clientes").document(uid).set(cliente)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro completo", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar datos del cliente", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

