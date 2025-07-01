package com.deliciaapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream

class SesionFragment : Fragment() {

    private lateinit var imgPerfil: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvApellidos: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvEmail: TextView

    private val PICK_IMAGE = 101
    private val TAKE_PHOTO = 102
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var currentPhotoUri: Uri

    private lateinit var btnCerrarSesion: Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sesion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imgPerfil = view.findViewById(R.id.imgPerfil)
        tvNombre = view.findViewById(R.id.tvNombre)
        tvApellidos = view.findViewById(R.id.tvApellidos)
        tvTelefono = view.findViewById(R.id.tvTelefono)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        sharedPrefs = requireContext().getSharedPreferences("perfil", Context.MODE_PRIVATE)

        cargarDatosUsuario()
        cargarFotoPerfil()

        imgPerfil.setOnClickListener {
            mostrarOpcionesFoto()
        }

        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

    }

    private fun cargarDatosUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("clientes").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    tvNombre.text = doc.getString("nombres") ?: ""
                    tvApellidos.text = doc.getString("apellidos") ?: ""
                    tvTelefono.text = doc.getString("telefono") ?: ""
                    tvEmail.text = FirebaseAuth.getInstance().currentUser?.email ?: ""
                }
            }
    }

    private fun cargarFotoPerfil() {
        val uriPath = sharedPrefs.getString("fotoLocal", null)
        if (uriPath != null) {
            val file = File(uriPath)
            if (file.exists()) {
                imgPerfil.setImageURI(Uri.fromFile(file))
            } else {
                imgPerfil.setImageResource(R.drawable.ic_perfil_placeholder)
            }
        } else {
            imgPerfil.setImageResource(R.drawable.ic_perfil_placeholder)
        }
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("Elegir de galería", "Tomar una foto", "Eliminar foto")
        AlertDialog.Builder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> elegirDesdeGaleria()
                    1 -> tomarFoto()
                    2 -> eliminarFoto()
                }
            }
            .show()
    }

    private fun elegirDesdeGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun tomarFoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(requireContext().filesDir, "perfil.jpg")
        currentPhotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    private fun eliminarFoto() {
        imgPerfil.setImageResource(R.drawable.ic_perfil_placeholder)
        sharedPrefs.edit().remove("fotoLocal").apply()
        File(requireContext().filesDir, "perfil.jpg").delete()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            PICK_IMAGE -> {
                val imageUri = data?.data ?: return
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                guardarFoto(bitmap)
            }
            TAKE_PHOTO -> {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, currentPhotoUri)
                guardarFoto(bitmap)
            }
        }
    }

    private fun guardarFoto(bitmap: Bitmap) {
        val file = File(requireContext().filesDir, "perfil.jpg")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        sharedPrefs.edit().putString("fotoLocal", file.absolutePath).apply()
        imgPerfil.setImageBitmap(bitmap)
    }
}
