package com.gidm.cuidame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IniciarSesionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        // Creamos instancia con la base de datos encargada de las autorizaciones
        val auth = FirebaseAuth.getInstance()

        // Elementos de la pantalla
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputContrasenia = findViewById<EditText>(R.id.inputContraseña)
        val iniciarSesion = findViewById<Button>(R.id.iniciar_sesion)
        val registrarse = findViewById<Button>(R.id.registrarse)

        // Si le da al botón "Iniciar sesión", ...
        iniciarSesion.setOnClickListener{

            // Recoge los datos de los campos de la pantalla
            val email = inputEmail.text.toString()
            val contrasenia = inputContrasenia.text.toString()

            // Inicia sesión dentro de la autenticación
            auth.signInWithEmailAndPassword(email, contrasenia).addOnCompleteListener {

                // Si se ha iniciado sesión correctamente, ...
                if(it.isSuccessful){

                    // Obtenemos el ID del usuario
                    val usuarioID = auth.currentUser!!.uid

                    // Comprobamos que es un sanitario el que ha iniciado sesión
                    val especialidadUsuario = FirebaseDatabase.getInstance().getReference("Usuarios").
                        child(usuarioID).child("especialidad")

                    especialidadUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            val especialidad = dataSnapshot.getValue(String::class.java)

                            // Si no tiene guardada una especialidad, ...
                            if(especialidad == null) {
                                // Cierra la sesión en Firebase
                                FirebaseAuth.getInstance().signOut()

                                Toast.makeText(
                                    this@IniciarSesionActivity,
                                    "No eres un sanitario", Toast.LENGTH_LONG
                                ).show()
                            }

                            else {
                                runOnUiThread {
                                    // Guarda el ID del usuario en su memoria local
                                    val shared =
                                        getSharedPreferences("datos-sanitario", MODE_PRIVATE)

                                    with(shared.edit()) {
                                        putString("id", usuarioID)
                                        commit()
                                    }

                                    // Nos dirigimos al menú principal
                                    val intent = Intent(
                                        this@IniciarSesionActivity,
                                        MainActivity::class.java
                                    )
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }

                else {
                    Toast.makeText(this, "Email y/o contraseña incorrectos", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Si le da al botón "Registrarse", ...
        registrarse.setOnClickListener{

            // Se va a la Activity RegistrarseActivity
            val intent = Intent(this, RegistrarseActivity::class.java)
            startActivity(intent)
        }
    }
}