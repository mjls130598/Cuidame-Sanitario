package com.gidm.cuidame

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Miramos si hay algún id en la memoria del dispositivo
        val shared = getSharedPreferences("datos-sanitario", MODE_PRIVATE) ?: return
        val id = shared.getString("id", null)

        // Sino lo hay, ...
        if(id == null){
            // Nos dirigimos a la actividad de iniciar sesión
            cambiarActividad(IniciarSesionActivity::class.java)
            finish()
        }

        // Obtenemos los distintos botones del menú
        val perfil = findViewById<LinearLayout>(R.id.perfil)
        val logout = findViewById<LinearLayout>(R.id.logout)

        // Si clickea sobre "Perfil"
        perfil.setOnClickListener {
            cambiarActividad(PerfilActivity::class.java)
        }

        // Si clickea sobre "Salir"
        logout.setOnClickListener{

            // Cierra la sesión en Firebase
            FirebaseAuth.getInstance().signOut()

            // Se elimina los datos de la memoria local
            with(shared.edit()){
                remove("id")
                commit()
            }

            cambiarActividad(IniciarSesionActivity::class.java)
            finish()
        }
    }

    private fun cambiarActividad(clase : Class<*>){
        val intent = Intent(this, clase)
        startActivity(intent)
    }
}