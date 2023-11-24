package com.volpe.fateczap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.volpe.fateczap.R
import com.volpe.fateczap.databinding.FragmentContatosBinding
import com.volpe.fateczap.databinding.FragmentConversasBinding
import com.volpe.fateczap.models.Conversa
import com.volpe.fateczap.models.Usuario
import com.volpe.fateczap.utils.Constantes
import com.volpe.fateczap.utils.exibirMensagem

class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration

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
        adicionarListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }

    private fun adicionarListenerConversas() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        if (idUsuarioRemetente != null){
            eventoSnapshot = firestore
                .collection(Constantes.CONVERSAS)
                .document( idUsuarioRemetente)
                .collection(Constantes.ULTIMAS_CONVERSAS)
                .addSnapshotListener { querySnapshot, error ->
                    if (erro != null){
                        activity?.exibirMensagem("Erro ao recuperar conversas")
                    }

                    val listaConversas = mutableListOf<Conversa>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->
                        val conversa = documentSnapshot.toObject(Conversa::class.java)
                    }
                }
        }
    }
}