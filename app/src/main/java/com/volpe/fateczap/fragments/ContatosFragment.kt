package com.volpe.fateczap.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.volpe.fateczap.databinding.FragmentContatosBinding
import com.volpe.fateczap.models.Usuario

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
        firestore
            .collection("usuarios")
            .addSnapshotListener{ querySnapshot, erro ->

                val documentos = querySnapshot?.documents

                documentos?.forEach { documentSnapshot ->

                    val usuario = documentSnapshot.toObject( Usuario::class.java)

                    if ( usuario != null ){
                        Log.i("fragmento_contato", "nome :  ${usuario.nome}")
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}