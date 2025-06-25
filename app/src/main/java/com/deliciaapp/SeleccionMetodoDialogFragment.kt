package com.deliciaapp.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.deliciaapp.R

class SeleccionMetodoDialogFragment(
    private val onConfirmar: (metodo: String, direccion: String?, referencia: String?, contacto: String?) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_seleccion_metodo, null)

        val radioDelivery = view.findViewById<RadioButton>(R.id.radioDelivery)
        val radioRecojo = view.findViewById<RadioButton>(R.id.radioRecojo)
        val layoutFormulario = view.findViewById<LinearLayout>(R.id.layoutFormulario)

        val etDireccion = view.findViewById<EditText>(R.id.etDireccion)
        val etReferencia = view.findViewById<EditText>(R.id.etReferencia)
        val etContacto = view.findViewById<EditText>(R.id.etContacto)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupMetodo)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            layoutFormulario.visibility = if (checkedId == R.id.radioDelivery) View.VISIBLE else View.GONE
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("¿Cómo deseas recibir tu pedido?")
            .setView(view)
            .setPositiveButton("Confirmar") { _, _ ->
                val metodo = if (radioDelivery.isChecked) "Delivery" else "Recojo en tienda"
                val direccion = if (radioDelivery.isChecked) etDireccion.text.toString() else null
                val referencia = if (radioDelivery.isChecked) etReferencia.text.toString() else null
                val contacto = if (radioDelivery.isChecked) etContacto.text.toString() else null
                onConfirmar(metodo, direccion, referencia, contacto)
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }
}
