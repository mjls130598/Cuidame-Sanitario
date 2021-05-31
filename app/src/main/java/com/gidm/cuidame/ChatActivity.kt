package com.gidm.cuidame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gidm.cuidame.adapter.Chat
import com.gidm.cuidame.adapter.ChatAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    // Modo de visualización de la lista
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val db = FirebaseDatabase.getInstance().reference
    private lateinit var idPaciente: String
    private lateinit var idUsuario: String
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        idPaciente = intent.getStringExtra("idPaciente")!!

        // Obtenemos el id del usuario actual
        val shared = getSharedPreferences("datos-sanitario", MODE_PRIVATE)
        idUsuario = shared.getString("id", "")!!

        recyclerView = findViewById<RecyclerView>(R.id.recyclerChats)
        val enviar = findViewById<ImageView>(R.id.enviarMensaje)
        val mensajeInput = findViewById<TextView>(R.id.inputMensaje)

        enviar.setOnClickListener{

            val mensaje = mensajeInput.text.toString()

            if (mensaje != "") {
                // Obtenemos las conversaciones anteriores
                val chats = db.child("Usuarios").child(idUsuario).child("chats")

                chats.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        // Se mira si se ha comenzado una conversación con él
                        val chatsUsuario = if (snapshot.value != null) {
                            snapshot.value as HashMap<String, String>
                        } else HashMap<String, String>()

                        val campos = HashMap<String, String>()
                        campos["mensaje"] = mensaje
                        campos["emisor"] = idUsuario
                        campos["receptor"] = idPaciente
                        campos["fecha"] = Date().toString()

                        // Si es así, ...
                        if (chatsUsuario.containsKey(idPaciente)) {

                            // Guardamos el nuevo mensaje dentro de él
                            val idChat = chatsUsuario[idPaciente]!!
                            val nuevoMensaje = db.child("Chats").child(idChat).push()

                            nuevoMensaje.setValue(campos)
                        }
                        // Sino, ...
                        else{

                            // Se crea una nueva conversación entre ellos y se guarda el mensaje
                            val nuevaConversacion = db.child("Chats").push()
                            val nuevoChat = nuevaConversacion.push()
                            nuevoChat.setValue(campos)

                            val nuevaConversacionUsuario = db.child("Usuarios").
                            child(idUsuario).child("chats").child(idPaciente)

                            nuevaConversacionUsuario.setValue(nuevaConversacion.key)

                            val nuevaConversacionSanitario = db.child("Usuarios").
                            child(idPaciente).child("chats").child(idUsuario)

                            nuevaConversacionSanitario.setValue(nuevaConversacion.key)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {}

                })

                mensajeInput.setText("")
                actualizarLista()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        actualizarLista()
    }

    private fun actualizarLista(){

        // Se indica el modo de organización de la lista
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        // Obtenemos las conversaciones anteriores
        val chats = db.child("Usuarios").child(idUsuario).
        child("chats")

        chats.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                // Se mira si se ha comenzado una conversación con él
                val chatsUsuario = if (snapshot.value != null) {
                    snapshot.value as HashMap<String, String>
                } else HashMap<String, String>()

                // Si es así, ...
                if(chatsUsuario.containsKey(idPaciente)){
                    val idChat = chatsUsuario[idPaciente]!!
                    val chatsSanitario = db.child("Chats").child(idChat)

                    // Se obtienen los mensajes anteriores y se pinta por pantalla
                    chatsSanitario.addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chats = ArrayList<Chat>()

                            for(chat in snapshot.children){
                                chats.add(Chat(chat.key as String,
                                    chat.child("mensaje").value as String,
                                    chat.child("emisor").value as String,
                                    chat.child("receptor").value as String,
                                    Date(chat.child("fecha").value as String)
                                ))
                            }

                            val adapter = ChatAdapter(chats, idUsuario)
                            recyclerView.adapter = adapter
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}