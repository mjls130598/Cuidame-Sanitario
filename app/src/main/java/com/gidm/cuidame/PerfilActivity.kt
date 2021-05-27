package com.gidm.cuidame

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class PerfilActivity : AppCompatActivity() {

    lateinit var dialogFragment: AlertaDialogFragment
    private lateinit var datosUsuario: DatabaseReference
    private lateinit var shared : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Recogemos los elementos de la pantalla
        val editar = findViewById<Button>(R.id.editar)
        val borrar = findViewById<Button>(R.id.borrar)

        // Si se clickea sobre "Editar cuenta", ...
        editar.setOnClickListener{
            val intent = Intent(this, EditarActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Si se clickea sobre "Borrar cuenta", ...
        borrar.setOnClickListener{
            // Mostrar el diálogo de confirmación
            dialogFragment = AlertaDialogFragment.newInstance()
            dialogFragment!!.show(supportFragmentManager, "Alerta")
        }
    }

    override fun onStart() {

        val nombre = findViewById<TextView>(R.id.nombre)
        val email = findViewById<TextView>(R.id.email)
        shared = getSharedPreferences("datos-paciente", MODE_PRIVATE)

        // Obtenemos el id del usuario
        val id = shared.getString("id", null)
        val usuario = FirebaseAuth.getInstance().currentUser!!
        datosUsuario = FirebaseDatabase.getInstance().
        getReference("Usuarios").child(usuario.uid)

        if (id != null){
            // Accedemos a los datos del usuario
            val nombreUsuario = datosUsuario.child("nombre")

            // Los mostramos por pantalla
            if(usuario != null){
                nombreUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        nombre.text = dataSnapshot.getValue(String::class.java)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
                email.text = usuario.email
            }
        }

        super.onStart()
    }

    fun continuarCierre(continuar: Boolean) {
        if (continuar) {

            // Se borra la infomación del usuario en la BD
            datosUsuario.removeValue()

            // Se borra la autenticación
            FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener{
                // Si se ha borrado correctamente
                if(it.isSuccessful){
                    // Borramos el id de la memoria local
                    with(shared.edit()){
                        remove("id")
                        commit()
                    }

                    // Nos dirigimos a la actividad de iniciar sesión
                    val intent = Intent(this, IniciarSesionActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }

                else
                    Toast.makeText(this, "No se puede borrar, inténtelo más tarde", Toast.LENGTH_LONG).show()

            }
        }
        else dialogFragment!!.dismiss()
    }

    class AlertaDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(requireActivity())
                .setMessage("¿Está seguro?")
                .setCancelable(false)
                .setNegativeButton("No") { _, _ ->
                    (activity as PerfilActivity).continuarCierre(false)
                }
                .setPositiveButton("Sí") { _, _ ->
                    (activity as PerfilActivity).continuarCierre(true)
                }.create()
        }

        companion object {
            fun newInstance(): AlertaDialogFragment {
                return AlertaDialogFragment()
            }
        }
    }
}