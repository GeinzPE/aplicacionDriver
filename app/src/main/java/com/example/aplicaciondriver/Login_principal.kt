package com.geinzDriver.aplicaciondriver

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.geinzDriver.aplicaciondriver.MainActivity
import com.geinzDriver.aplicaciondriver.R
import com.geinzDriver.aplicaciondriver.databinding.ActivityLoginPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login_principal : AppCompatActivity() {
    private lateinit var binding:ActivityLoginPrincipalBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginPrincipalBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth=FirebaseAuth.getInstance()

        checkSession()

        binding.entrar.setOnClickListener {
            val email = binding.emailED.text.toString()
            val password = binding.paswordED.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Log.e("Login", "Email o contraseña vacíos")
            }
        }


        binding.mantenerSeccion.setOnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("checkBoxState", isChecked)
            editor.apply()
        }

    }
    private fun login(mail: String, contra: String) {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithEmailAndPassword(mail, contra)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Autenticación exitosa, ahora verifica en Firestore
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        val db = FirebaseFirestore.getInstance()
                        val docRef = db.collection("Trabajadores_Usuarios_Drivers")
                            .document("drivers")
                            .collection("drivers")
                            .document(userId) // O usar otro campo para identificar al usuario

                        docRef.get().addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Usuario existe en Firestore, continuar con la navegación
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Opcional: finaliza la actividad de inicio de sesión para que el usuario no pueda volver atrás
                            } else {
                                // Usuario no encontrado en Firestore
                                Log.e("Login", "El usuario no está registrado en Firestore")
                                // Aquí podrías mostrar un mensaje al usuario
                            }
                        }.addOnFailureListener { exception ->
                            // Error al obtener datos de Firestore
                            Log.e("Login", "Error al verificar el usuario en Firestore", exception)
                            // Aquí podrías mostrar un mensaje al usuario
                        }
                    } else {
                        // El usuario autenticado no tiene un ID válido
                        Log.e("Login", "Error: el ID del usuario no es válido")
                    }
                } else {
                    // Error de autenticación
                    Log.e("Login", "Error al iniciar sesión", task.exception)
                    // Aquí podrías mostrar un mensaje al usuario
                }
            }
    }

    private fun checkSession() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val checkBoxState = sharedPreferences.getBoolean("checkBoxState", false)

        if (checkBoxState) {
            // Si el CheckBox estaba marcado, verificar si ya hay un usuario autenticado
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Usuario ya está autenticado, redirigir a la MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}