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
                R.id.nav_pedidos -> PedidosFragment()
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

    }
}