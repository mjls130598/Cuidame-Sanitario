package com.gidm.cuidame.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gidm.cuidame.ChatActivity
import com.gidm.cuidame.R
import kotlin.collections.ArrayList

class MisPacientesAdapter(private val pacientes: ArrayList<Paciente>):
    RecyclerView.Adapter<MisPacientesAdapter.PacienteHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PacienteHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_paciente, false)
        return PacienteHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: PacienteHolder, position: Int) {
        val itemSanitario = pacientes[position]
        holder.bindPaciente(itemSanitario)
    }

    override fun getItemCount(): Int = pacientes.size

    class PacienteHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener {

        private var paciente: Paciente? = null
        private var view: View = v

        init {
            view.setOnClickListener(this)
        }

        fun bindPaciente(paciente: Paciente) {
            this.paciente = paciente
            val nombre = view.findViewById<TextView>(R.id.nombrePaciente)
            nombre.text = paciente.nombre
        }

        override fun onClick(p0: View?) {
            // Dirige a la actividad donde se muestran la lista de mensajes
            // entre los dos usuario
            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("idPaciente", paciente!!.uid)
            view.context.startActivity(intent)
        }

    }
}