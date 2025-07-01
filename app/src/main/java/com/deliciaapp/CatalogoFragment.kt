package com.deliciaapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
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
    private lateinit var etBuscar: EditText
    private lateinit var etPrecioMin: EditText
    private lateinit var etPrecioMax: EditText
    private lateinit var spCategoria: Spinner

    private val listaProductos = mutableListOf<Producto>()
    private val productosOriginales = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalogo, container, false)

        // Referencias
        recyclerView = view.findViewById(R.id.recyclerCatalogo)
        etBuscar = view.findViewById(R.id.etBuscar)
        etPrecioMin = view.findViewById(R.id.etPrecioMin)
        etPrecioMax = view.findViewById(R.id.etPrecioMax)
        spCategoria = view.findViewById(R.id.spCategoria)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductoAdapter(listaProductos)
        recyclerView.adapter = adapter

        // Spinner categorías (puedes adaptar según tus categorías reales)
        val categorias = listOf("Todas", "Pan", "Pasteles", "Bebidas", "Otros")
        spCategoria.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categorias)

        cargarProductosDesdeFirebase()

        // Listeners para filtros
        etBuscar.addTextChangedListener(filtroTextWatcher)
        etPrecioMin.addTextChangedListener(filtroTextWatcher)
        etPrecioMax.addTextChangedListener(filtroTextWatcher)

        spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        return view
    }

    private fun cargarProductosDesdeFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                productosOriginales.clear()
                for (document in result) {
                    val producto = document.toObject(Producto::class.java)
                    producto.id = document.id
                    productosOriginales.add(producto)
                }
                aplicarFiltros()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
    }

    private val filtroTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = aplicarFiltros()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun aplicarFiltros() {
        val textoBusqueda = etBuscar.text.toString().lowercase()
        val precioMin = etPrecioMin.text.toString().toDoubleOrNull()
        val precioMax = etPrecioMax.text.toString().toDoubleOrNull()
        val categoriaSeleccionada = spCategoria.selectedItem.toString()

        val filtrados = productosOriginales.filter { producto ->
            val coincideBusqueda = producto.nombre.lowercase().contains(textoBusqueda)
            val coincideCategoria = categoriaSeleccionada == "Todas" || producto.categoria.equals(categoriaSeleccionada, ignoreCase = true)
            val dentroRangoMin = precioMin == null || producto.precio >= precioMin
            val dentroRangoMax = precioMax == null || producto.precio <= precioMax

            coincideBusqueda && coincideCategoria && dentroRangoMin && dentroRangoMax
        }

        listaProductos.clear()
        listaProductos.addAll(filtrados)
        adapter.notifyDataSetChanged()
    }

}
