package com.volpe.fateczap

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
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

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        inicializarToolbar()
        solicitarPermissoes()
        inicializarEventosClique()
    }

    private fun uploadImagemStorage(uri: Uri) {

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