package com.gidm.cuidame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gidm.cuidame.adapter.MisPacientesAdapter
import com.gidm.cuidame.adapter.Paciente
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MisPacientesActivity : AppCompatActivity() {
    // Modo de visualización de la lista
    private lateinit var linearLayoutManager: LinearLayoutManager

    // El adaptador de la lista
    private lateinit var adapter: MisPacientesAdapter

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pacientes)

        // Elementos de la vista
        recyclerView = findViewById<RecyclerView>(R.id.pacientes)
    }

    override fun onStart() {
        super.onStart()
        actualizarLista()
    }

    fun actualizarLista(){
        // Se indica el modo de organización de la lista
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        val dbUsuario = FirebaseDatabase.getInstance().reference.child("Usuarios")

        val shared = getSharedPreferences("datos-sanitario", MODE_PRIVATE)
        // Obtenemos el id del usuario
        val id = shared.getString("id", "")

        // Se obtienen los sanitarios guardados
        dbUsuario.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val pacientes = ArrayList<Paciente>()
                val usuarioActual = snapshot.child(id!!)

                val pacientesUsuario = if(usuarioActual.child("pacientes").value != null){
                    usuarioActual.child("pacientes").value as HashMap<String, String>
                } else HashMap()

                for((_, value) in pacientesUsuario){
                    val sanitario = snapshot.child(value)
                    pacientes.add(Paciente(sanitario.child("nombre").value as String,
                        value))
                }

                adapter = MisPacientesAdapter(pacientes)
                recyclerView.adapter =adapter
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }
}