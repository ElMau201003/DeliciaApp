package com.deliciaapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deliciaapp.adapter.PedidoAdapter
import com.deliciaapp.model.Pedido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PedidosFragment : Fragment() {

    private lateinit var recyclerPedidos: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private val listaPedidos = mutableListOf<Pedido>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_pedidos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerPedidos = view.findViewById(R.id.recyclerPedidos)
        recyclerPedidos.layoutManager = LinearLayoutManager(requireContext())

        pedidoAdapter = PedidoAdapter(listaPedidos, requireContext())
        recyclerPedidos.adapter = pedidoAdapter

        cargarPedidosDelUsuario()
    }

    private fun cargarPedidosDelUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("pedidos")
            .whereEqualTo("usuarioId", uid)
            .get()
            .addOnSuccessListener { result ->
                listaPedidos.clear()
                for (document in result) {
                    val pedido = document.toObject(Pedido::class.java)
                    listaPedidos.add(pedido)
                }
                pedidoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
            }
    }
}
