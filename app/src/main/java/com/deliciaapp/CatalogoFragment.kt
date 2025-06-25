package com.deliciaapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deliciaapp.adapter.ProductoAdapter
import com.deliciaapp.model.Producto
import com.google.firebase.firestore.FirebaseFirestore

class CatalogoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private val listaProductos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalogo, container, false)
        recyclerView = view.findViewById(R.id.recyclerCatalogo)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductoAdapter(listaProductos)
        recyclerView.adapter = adapter

        cargarProductosDesdeFirebase()
        return view
    }

    private fun cargarProductosDesdeFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                listaProductos.clear()
                for (document in result) {
                    val producto = document.toObject(Producto::class.java)
                    producto.id = document.id
                    listaProductos.add(producto)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
    }
}
