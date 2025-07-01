package com.deliciaapp.model

data class Pedido(
    val productos: List<Producto> = listOf(),
    val total: Double = 0.0,
    val fecha: Long = System.currentTimeMillis(),
    val metodo: String = "",
    val direccion: String? = null,
    val referencia: String? = null,
    val contacto: String? = null,
    val usuarioId: String = "" // <--- nuevo campo para identificar al usuario
)
