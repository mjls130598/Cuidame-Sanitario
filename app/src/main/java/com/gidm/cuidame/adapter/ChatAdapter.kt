package com.gidm.cuidame.adapter

import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gidm.cuidame.R
import kotlin.collections.ArrayList

class ChatAdapter(private val mensajes: ArrayList<Chat>, private val usuarioID: String):
    RecyclerView.Adapter<ChatAdapter.BaseViewHolder<*>>() {

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Chat, itemAnterior: Chat?)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val itemChat = mensajes[position]
        val itemChatAnterior = if (position != 0) mensajes[position - 1] else null
        holder.bind(itemChat, itemChatAnterior)
    }

    override fun getItemViewType(position: Int): Int {
        val mensaje = mensajes[position]
        return when (mensaje.emisor) {
            usuarioID -> ENVIADO
            else -> RECIBIDO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return when (viewType) {
            ENVIADO -> {
                val inflatedView = parent.inflate(R.layout.recyclerview_send, false)
                EnviadoViewHolder(inflatedView)
            }
            RECIBIDO -> {
                val inflatedView = parent.inflate(R.layout.recyclerview_recibido, false)
                RecibidoViewHolder(inflatedView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = mensajes.size

    inner class EnviadoViewHolder(itemView: View): BaseViewHolder<Chat>(itemView) {
        override fun bind(chat: Chat, chatAnterior: Chat?) {
            mostrarElementos(chat, chatAnterior, itemView)
        }

    }

    inner class RecibidoViewHolder(itemView: View): BaseViewHolder<Chat>(itemView){
        override fun bind(chat: Chat, chatAnterior: Chat?) {
            mostrarElementos(chat, chatAnterior, itemView)
        }

    }

    fun mostrarElementos(chat: Chat, chatAnterior: Chat?, view: View){
        val mensaje = view.findViewById<TextView>(R.id.mensaje)
        val hora = view.findViewById<TextView>(R.id.hora)
        val fecha = view.findViewById<TextView>(R.id.fecha)

        mensaje.text = chat.mensaje
        val minutos = if (chat.fecha.minutes < 10) "0${chat.fecha.minutes}"
        else chat.fecha.minutes.toString()
        hora.text = "${chat.fecha.hours} : $minutos"
        val fechaChat = "${chat.fecha.date} ${mes(chat.fecha.month)}"
        fecha.text = fechaChat

        // Mostrar la fecha si es el primer mensaje en ese día
        if(chatAnterior != null){
            val fechaAnterior = "${chatAnterior.fecha.date} ${mes(chatAnterior.fecha.month)}"

            if(fechaAnterior == fechaChat)
                fecha.visibility = GONE
        }
    }

    fun mes(mesInt: Int): String{
        return when (mesInt){
            0 -> "Enero"
            1 -> "Febrero"
            2 -> "Marzo"
            3 -> "Abril"
            4 -> "Mayo"
            5 -> "Junio"
            6 -> "Julio"
            7 -> "Agosto"
            8 -> "Septiembre"
            9 -> "Octubre"
            10 -> "Noviembre"
            11 -> "Diciembre"
            else -> ""
        }
    }

    companion object {
        private const val ENVIADO = 1
        private const val RECIBIDO = 2
    }
}