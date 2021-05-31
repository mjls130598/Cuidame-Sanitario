package com.gidm.cuidame

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object Utils {
    fun comprobarUsuario(nombre: String, correo: String, contrasenia1: String,
                         contrasenia2: String, especialidad: String, context: Context) : Boolean{

        var correcto = true

        if(nombre == "" || correo == "" || contrasenia1 == "" || contrasenia2 == "" || especialidad == ""){
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

    fun borrarPaciente(idPaciente: String, idSanitario: String){

        val db = FirebaseDatabase.getInstance().reference

        // Los chats con él
        val idConversacion = db.child("Usuarios").child(idSanitario).
        child("chats").child(idPaciente)

        idConversacion.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val id = snapshot.value.toString()
                db.child("Chats").child(id).removeValue()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        idConversacion.removeValue()
        db.child("Usuarios").child(idPaciente).child("chats").child(idSanitario).removeValue()

        val dbUsuarios = db.child("Usuarios")
        dbUsuarios.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Dentro del paciente
                val sanitariosSnapshot = snapshot.child(idPaciente).
                child("sanitarios").value

                val sanitarios = if (sanitariosSnapshot != null) {
                    sanitariosSnapshot as HashMap<String, String>
                } else HashMap()

                val idUsuarioSanitario = sanitarios.filterValues{it == idSanitario}.keys

                if(idUsuarioSanitario.isNotEmpty())
                    dbUsuarios.child(idPaciente).child("sanitarios").
                    child(idUsuarioSanitario.first()).removeValue()

                // Dentro de la cuenta del sanitario
                val pacientesSnapshot = snapshot.child(idSanitario).
                child("pacientes").value

                val pacientes = if (pacientesSnapshot != null) {
                    pacientesSnapshot as HashMap<String, String>
                } else HashMap()

                val idSanitarioPaciente = pacientes.filterValues{it == idPaciente}.keys
                if(idSanitarioPaciente.isNotEmpty())
                    dbUsuarios.child(idSanitario).child("pacientes").
                    child(idSanitarioPaciente.first()).removeValue()

            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}