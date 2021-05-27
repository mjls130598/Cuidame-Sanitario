package com.gidm.cuidame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrarseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_usuario)

        // Creamos instancia con la base de datos encargada de las autorizaciones
        val auth = FirebaseAuth.getInstance()

        // Creamos instancia con la base de datos
        val db = FirebaseDatabase.getInstance()

        // Obtenemos los elementos de la vista
        val nombreInput = findViewById<EditText>(R.id.inputNombre)
        val correoInput = findViewById<EditText>(R.id.inputNuevoEmail)
        val contraseniaInput = findViewById<EditText>(R.id.inputNewContrasenia)
        val contraseniaRepInput = findViewById<EditText>(R.id.inputNewContrasenia2)
        val guardar = findViewById<Button>(R.id.nuevoUsuario)

        // Si se pulsa sobre "Guardar", ...
        guardar.setOnClickListener{

            // Recoge los datos de los elementos de la pantalla
            val nombre = nombreInput.text.toString()
            val correo = correoInput.text.toString()
            val contrasenia = contraseniaInput.text.toString()
            val contraseniaRep =  contraseniaRepInput.text.toString()

            // Si los datos introducidos son correctos, ...
            if(Utils.comprobarUsuario(nombre, correo, contrasenia, contraseniaRep, this)){

                // Creamos la autenticación del nuevo usuario en la BD
                auth.createUserWithEmailAndPassword(correo, contrasenia).addOnCompleteListener{

                     // Si se ha relizado correctamente, ...
                    if (it.isSuccessful){

                        // Obtenemos el ID del usuario
                        val usuarioID = auth.currentUser!!.uid

                        // Guardamos dentro de "Usuarios" en la BD
                        val referencia = db.getReference("Usuarios").child(usuarioID)

                        // El ID del usuario creado y su nombre
                        val campos = HashMap<String, String>()
                        campos["nombre"] = nombre

                        // Se guarda los datos anteriores
                        referencia.setValue(campos).addOnCompleteListener{ it2 ->

                            // Si todo ha salido correctamente, ...
                            if (it2.isSuccessful){

                                // Guarda el ID del usuario en su memoria local
                                val shared = getSharedPreferences("datos-paciente", MODE_PRIVATE)
                                    ?: return@addOnCompleteListener

                                with(shared.edit()){
                                    putString("id", usuarioID)
                                    commit()
                                }

                                // Nos dirigimos al menú principal
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                    else{
                        Toast.makeText(this, "Ese correo ya ha sido utilizado", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}