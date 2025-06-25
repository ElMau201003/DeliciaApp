package com.deliciaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deliciaapp.adapter.ProductoAdapter
import com.deliciaapp.model.Producto
import com.deliciaapp.CarritoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Carga inicial
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatalogoFragment())
            .commit()

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_catalogo -> CatalogoFragment()
                R.id.nav_carrito -> CarritoFragment()
                R.id.nav_sesion -> SesionFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .commit()
                true
            } ?: false
        }

        // 👇 Llamada correcta: dentro del onCreate
        //subirProductosAFirestore()
    }

    /*private fun subirProductosAFirestore() {
        val db = FirebaseFirestore.getInstance()

        val productos = listOf(
            Producto("Pan francés", "Recién horneado todos los días", 2.00),
            Producto("Empanadas de Pollo", "Rellenas con ingredientes frescos", 5.00),
            Producto("Pastel de Chocolate", "Puro sabor en cada rebanada", 20.00),
            Producto("Pie de manzana", "Dulce sabor a manzana", 10.00),
            Producto("Bizcocho de vainilla", "Un sabor que gusta", 12.00)
        )

        for (producto in productos) {
            db.collection("productos")
                .add(producto)
                .addOnSuccessListener {
                    println("✅ Producto '${producto.nombre}' subido con éxito")
                }
                .addOnFailureListener { e ->
                    println("❌ Error al subir el producto: ${e.message}")
                }
        }
    }*/
}