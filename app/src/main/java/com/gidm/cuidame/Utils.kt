package com.gidm.cuidame

import android.content.Context
import android.widget.Toast

object Utils {
    fun comprobarUsuario(nombre: String, correo: String, contrasenia1: String,
                         contrasenia2: String, context: Context) : Boolean{

        var correcto = true

        if(nombre == "" || correo == "" || contrasenia1 == "" || contrasenia2 == ""){
            Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
            correcto = false
        }

        if(contrasenia1.length != contrasenia2.length || contrasenia1.length < 8){
            Toast.makeText(context, "Las contraseñas deben ser iguales y tener como mínimo 8 caracteres",
            Toast.LENGTH_LONG).show()
            correcto = false
        }

        return correcto
    }
}