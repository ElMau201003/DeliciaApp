package com.deliciaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deliciaapp.R
import com.deliciaapp.model.Producto

class CarritoAdapter(
    private val productos: MutableList<Producto>,
    private val onCantidadCambiada: () -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreCarrito)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioCarrito)
        val etCantidad: EditText = itemView.findViewById(R.id.etCantidad)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = productos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "S/ ${producto.precio * producto.cantidad}"
        holder.etCantidad.setText(producto.cantidad.toString())

        holder.etCantidad.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val nuevaCantidad = holder.etCantidad.text.toString().toIntOrNull() ?: 1
                producto.cantidad = nuevaCantidad.coerceAtLeast(1)

                holder.itemView.post {
                    notifyItemChanged(holder.bindingAdapterPosition)
                }

                onCantidadCambiada()
            }
        }


        holder.btnEliminar.setOnClickListener {
            productos.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productos.size)
            onCantidadCambiada()
        }
    }
}

