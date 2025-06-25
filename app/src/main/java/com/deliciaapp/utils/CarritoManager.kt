package com.deliciaapp.utils

import com.deliciaapp.model.Producto

object CarritoManager {

    private val carrito = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        val existente = carrito.find { it.id == producto.id }
        if (existente != null) {
            existente.cantidad += 1
        } else {
            val nuevo = producto.copy(cantidad = 1)
            carrito.add(nuevo)
        }
    }

    fun obtenerProductos(): List<Producto> {
        return carrito
    }

    fun eliminarProducto(producto: Producto) {
        carrito.removeIf { it.id == producto.id }
    }

    fun editarCantidad(producto: Producto, nuevaCantidad: Int) {
        val existente = carrito.find { it.id == producto.id }
        if (existente != null) {
            if (nuevaCantidad <= 0) {
                eliminarProducto(producto)
            } else {
                existente.cantidad = nuevaCantidad
            }
        }
    }

    fun vaciarCarrito() {
        carrito.clear()
    }

    fun obtenerTotal(): Double {
        return carrito.sumOf { it.precio * it.cantidad }
    }
}
