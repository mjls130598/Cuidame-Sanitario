package com.gidm.cuidame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gidm.cuidame.adapter.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatsActivity : AppCompatActivity() {

    // Modo de visualización de la lista
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val db = FirebaseDatabase.getInstance().reference
    private lateinit var idUsuario: String
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        val shared = getSharedPreferences("datos-sanitario", MODE_PRIVATE)
        idUsuario = shared.getString("id", "")!!

        recyclerView = findViewById<RecyclerView>(R.id.conversaciones)
    }

    override fun onStart() {
        super.onStart()

        // Se indica el modo de organización de la lista
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        // Obtenemos las conversaciones anteriores
        val chats = db.child("Usuarios")

        chats.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Se mira si se ha comenzado una conversación con él
                val chatsUsuario = if (snapshot.child(idUsuario).child("chats").value != null) {
                    snapshot.child(idUsuario).child("chats").value as HashMap<String, String>
                } else HashMap<String, String>()

                // Si hay alguna conversación, ...
                if(chatsUsuario.isNotEmpty()){

                    for ((key, _) in chatsUsuario){

                        val dbPaciente = snapshot.child(key)

                        // Recojo la información de los sanitarios
                        val pacientes = ArrayList<Paciente>()
                        pacientes.add(Paciente(dbPaciente.child("nombre").value as String, key))

                        val adapter = MisPacientesAdapter(pacientes)
                        recyclerView.adapter = adapter
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}