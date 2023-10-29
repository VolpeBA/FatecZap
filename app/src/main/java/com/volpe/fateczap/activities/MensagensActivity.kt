package com.volpe.fateczap.activities

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.volpe.fateczap.databinding.ActivityMensagensBinding
import com.volpe.fateczap.models.Usuario
import com.volpe.fateczap.utils.Constantes

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate( layoutInflater )
    }
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        recuperarDadosUsuarioDestinatario()
        inicializarToolbar()
    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagens
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = ""
            if ( dadosDestinatario != null ){
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load( dadosDestinatario!!.foto )
                    .into( binding.imageFotoPerfil )

            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUsuarioDestinatario() {
        val extras = intent.extras
        if (extras != null){
            val origem = extras.getString("origem")
            if ( origem == Constantes.ORIGEM_CONTATO ){
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(
                        "dadosDestinatario",
                        Usuario::class.java
                    )
                }else {
                    extras.getParcelable(
                        "dadosDestinatario"
                    )

                }
            }else if( origem == Constantes.ORIGEM_CONVERSA ){
                // Recuperar dados da conversa
            }
        }
    }
}