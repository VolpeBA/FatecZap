package com.volpe.fateczap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.volpe.fateczap.databinding.ActivityPerfilBinding
import com.volpe.fateczap.utils.exibirMensagem

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate( layoutInflater )
    }

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){ uri->
        if ( uri != null){
            binding.imagePerfil.setImageURI( uri )
            uploadImagemStorage( uri )
        }else{
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false
    private var idUsuario: String? = null

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuario()
    }

    private fun recuperarDadosIniciaisUsuario() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if ( idUsuario != null ){
            firestore
                .collection("usuarios")
                .document( idUsuario )
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    var dadosUsuario = documentSnapshot.data

                    if ( dadosUsuario != null ){

                        val nome = dadosUsuario["nome"] as String
                        val foto = dadosUsuario["foto"] as String

                        binding.editNomePerfil.setText( nome )
                        if ( foto.isNotEmpty() ){
                            Picasso.get()
                                .load( foto )
                                .into( binding.imagePerfil )
                        }
                    }
                }
        }

    }

    private fun uploadImagemStorage(uri: Uri) {
        val idUsuario = firebaseAuth.currentUser?.uid
        if ( idUsuario != null){
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile( uri )
                .addOnSuccessListener {task ->
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { url ->
                            val dados = mapOf(
                                "foto" to url.toString()
                            )
                            atualizarDadosPerfil( idUsuario, dados )
                        }
                }.addOnFailureListener{
                    exibirMensagem("Falha ao enviar a imagem, tente novamente")
                }
        }
    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {
        firestore
            .collection("usuarios" )
            .document( idUsuario )
            .update( dados )
            .addOnSuccessListener {
                //exibirMensagem("Sucesso ao atualizar o perfil")

            }
            .addOnFailureListener {
                exibirMensagem("Erro ao atualizar perfil, tente novamente")
            }
    }


    private fun inicializarEventosClique() {
        binding.fabSelecionar.setOnClickListener{
            if ( temPermissaoGaleria ){
                gerenciadorGaleria.launch("image/*")
            }else{
                exibirMensagem("Não tem permissão para acessar galeria")
                solicitarPermissoes()
            }
        }

        binding.btnAtualizarPerfil.setOnClickListener{
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if ( nomeUsuario.isNotEmpty()){
                val idUsuario = firebaseAuth.currentUser?.uid
                // Sempre que utilizar chamada segura ( currentUser?) deve se verificar se está nulo
                if ( idUsuario != null ){
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil( idUsuario, dados )
                    exibirMensagem("Perfil atualizado com sucesso")
                    startActivity(
                        Intent(applicationContext, MainActivity::class.java)
                    )
                }
            }else{
                exibirMensagem("Preencha o nome corretamente")
            }
        }
    }

    private fun solicitarPermissoes() {
        //Verificar se tem permissao
        temPermissaoCamera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        temPermissaoGaleria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        // Lista de permissoes negadas
        var listaPermissoesNegadas = mutableListOf<String>()
        if (!temPermissaoCamera){
            listaPermissoesNegadas.add( Manifest.permission.CAMERA )
        }
        if (!temPermissaoGaleria){
            listaPermissoesNegadas.add( Manifest.permission.READ_MEDIA_IMAGES )
        }
        if( listaPermissoesNegadas.isNotEmpty()){
            // Solicitando multiplas permissoes
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){permissoes ->
                temPermissaoCamera = permissoes[Manifest.permission.CAMERA]
                    ?: temPermissaoCamera

                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: temPermissaoGaleria
            }
            gerenciadorPermissoes.launch( listaPermissoesNegadas.toTypedArray())
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarPerfil.tbPrincipal
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Editar perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}