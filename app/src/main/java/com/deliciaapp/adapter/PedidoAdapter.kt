package com.deliciaapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deliciaapp.R
import com.deliciaapp.model.Pedido
import java.text.SimpleDateFormat
import java.util.*

class PedidoAdapter(private val pedidos: List<Pedido>, private val context: Context) :
    RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.bind(pedido)
        holder.btnDetalles.setOnClickListener {
            mostrarDialogoDetalles(pedido)
        }
    }

    override fun getItemCount() = pedidos.size

    private fun mostrarDialogoDetalles(pedido: Pedido) {
        val mensaje = buildString {
            append("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(pedido.fecha))}\n")
            append("Total: S/ %.2f\n".format(pedido.total))
            append("Método: ${pedido.metodo}\n\n")
            append("Productos:\n")
            pedido.productos.forEach {
                append("- ${it.nombre} x${it.cantidad} (S/ %.2f)\n".format(it.precio * it.cantidad))
            }
            pedido.direccion?.let { append("\nDirección: $it") }
            pedido.referencia?.let { append("\nReferencia: $it") }
            pedido.contacto?.let { append("\nContacto: $it") }
        }

        AlertDialog.Builder(context)
            .setTitle("Detalle del Pedido")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvProductos: TextView = itemView.findViewById(R.id.tvProductos)
        val tvMetodo: TextView = itemView.findViewById(R.id.tvMetodo)
        val btnDetalles: Button = itemView.findViewById(R.id.btnVerDetalles)

        fun bind(pedido: Pedido) {
            val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaFormateada = formato.format(Date(pedido.fecha))
            tvFecha.text = "Fecha: $fechaFormateada"
            tvTotal.text = "Total: S/ %.2f".format(pedido.total)
            tvMetodo.text = "Método: ${pedido.metodo}"

            val resumen = pedido.productos.joinToString("\n") {
                "- ${it.nombre} x${it.cantidad}"
            }
            tvProductos.text = resumen
        }
    }
}
