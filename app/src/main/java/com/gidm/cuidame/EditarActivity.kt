package com.gidm.cuidame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_usuario)

        // Obtenemos los distintos elementos de la pantalla
        val nombreInput = findViewById<EditText>(R.id.inputNombre)
        val emailInput = findViewById<EditText>(R.id.inputNuevoEmail)
        val contraseniaInput = findViewById<EditText>(R.id.inputNewContrasenia)
        val contraseniaRepetidaInput = findViewById<EditText>(R.id.inputNewContrasenia2)
        val guardar = findViewById<Button>(R.id.nuevoUsuario)

        // Recogemos el id del usuario activo
        val shared = getSharedPreferences("datos-sanitario", MODE_PRIVATE)
        val id = shared.getString("id", null)

        // Creamos las instancias a las distintas BBDD
        val auth = FirebaseAuth.getInstance()
        val dbUsuario = FirebaseDatabase.getInstance().reference.child("Usuarios").child(id!!)

        // Recogemos los valores guardados en las BBDD y los pintamos
        var nombreAntiguo = ""
        dbUsuario.child("nombre").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nombreAntiguo = dataSnapshot.getValue(String::class.java)!!
                nombreInput.setText(nombreAntiguo)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        val emailAntiguo = auth.currentUser!!.email
        emailInput.setText(emailAntiguo)

        // Si pulsa sobre "Guardar",...
        guardar.setOnClickListener{

            // Recogemos los valores de los campos
            val nombreNuevo = nombreInput.text.toString()
            val emailNuevo = emailInput.text.toString()
            val contrasenia = contraseniaInput.text.toString()
            val contraseniaRepetida = contraseniaRepetidaInput.text.toString()

            // Si todo lo introducido es correcto, ...
            if(Utils.comprobarUsuario(nombreNuevo, emailNuevo, contrasenia, contraseniaRepetida, this)){

                // Modificamos el perfil del usuario
                auth.currentUser!!.updateEmail(emailNuevo).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FIREBASE", "Email actualizado")
                    }
                }
                auth.currentUser!!.updatePassword(contrasenia).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FIREBASE", "Contraseña actualizada")
                    }
                }
                dbUsuario.child("nombre").setValue(nombreNuevo)

                // Nos dirigimos al perfil del usuario
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}