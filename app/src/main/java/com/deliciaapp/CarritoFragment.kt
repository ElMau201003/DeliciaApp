package com.deliciaapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deliciaapp.adapter.CarritoAdapter
import com.deliciaapp.dialog.SeleccionMetodoDialogFragment
import com.deliciaapp.model.Pedido
import com.deliciaapp.model.Producto
import com.deliciaapp.utils.CarritoManager
import com.google.firebase.firestore.FirebaseFirestore

class CarritoFragment : Fragment() {

    private lateinit var recyclerCarrito: RecyclerView
    private lateinit var btnVaciarCarrito: Button
    private lateinit var btnGenerarPedido: Button
    private lateinit var tvTotal: TextView

    private lateinit var carritoAdapter: CarritoAdapter
    private var listaCarrito: MutableList<Producto> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_carrito, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerCarrito = view.findViewById(R.id.recyclerCarrito)
        btnVaciarCarrito = view.findViewById(R.id.btnVaciarCarrito)
        btnGenerarPedido = view.findViewById(R.id.btnGenerarPedido)
        tvTotal = view.findViewById(R.id.tvTotal)

        listaCarrito = CarritoManager.obtenerProductos().toMutableList()

        carritoAdapter = CarritoAdapter(listaCarrito) {
            actualizarTotal()
        }

        recyclerCarrito.layoutManager = LinearLayoutManager(requireContext())
        recyclerCarrito.adapter = carritoAdapter

        actualizarTotal()

        btnVaciarCarrito.setOnClickListener {
            CarritoManager.vaciarCarrito()
            listaCarrito.clear()
            carritoAdapter.notifyDataSetChanged()
            actualizarTotal()
        }

        btnGenerarPedido.setOnClickListener {
            if (listaCarrito.isEmpty()) {
                Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SeleccionMetodoDialogFragment { metodo, direccion, referencia, contacto ->
                guardarPedido(metodo, direccion, referencia, contacto)
            }.show(parentFragmentManager, "SeleccionMetodoDialog")
        }
    }

    private fun actualizarTotal() {
        val total = listaCarrito.sumOf { it.precio * it.cantidad }
        tvTotal.text = "Total: S/ %.2f".format(total)
    }

    private fun guardarPedido(
        metodo: String,
        direccion: String?,
        referencia: String?,
        contacto: String?
    ) {
        val productos = CarritoManager.obtenerProductos()
        val total = productos.sumOf { it.precio * it.cantidad }
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val pedido = Pedido(
            productos = productos,
            total = total,
            metodo = metodo,
            direccion = direccion,
            referencia = referencia,
            contacto = contacto,
            usuarioId = uid // <--- se guarda el ID del usuario aquí
        )

        FirebaseFirestore.getInstance().collection("pedidos")
            .add(pedido)
            .addOnSuccessListener {
                Toast.makeText(context, "Pedido enviado con éxito", Toast.LENGTH_SHORT).show()
                CarritoManager.vaciarCarrito()
                listaCarrito.clear()
                carritoAdapter.notifyDataSetChanged()
                actualizarTotal()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al enviar pedido", Toast.LENGTH_SHORT).show()
            }
    }

}
