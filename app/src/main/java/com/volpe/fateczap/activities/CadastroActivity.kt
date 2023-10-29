package com.volpe.fateczap.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.volpe.fateczap.databinding.ActivityCadastroBinding
import com.volpe.fateczap.models.Usuario
import com.volpe.fateczap.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate( layoutInflater )
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        inicializarToolbar()
        inicializarEventoClique()
    }

    private fun inicializarEventoClique() {
        binding.btnCadastrar.setOnClickListener{
            if( validarCampos() ){
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener{ resultado ->
            if ( resultado.isSuccessful ){
                // Salva dados do usuario no fire
                val idUsuario = resultado.result.user?.uid
                if ( idUsuario != null ){
                    val usuario = Usuario(
                        idUsuario, nome, email
                    )
                    salvarUsuarioFirestore( usuario )
                }
                // Inicio da activity
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )

            }
        }.addOnFailureListener{erro ->
            try {
                throw erro
            }catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                erroSenhaFraca.printStackTrace()
                exibirMensagem("Senha fraca, digite outra com letras, números e caracteres especiais")
            }catch (erroUsuarioExistente: FirebaseAuthUserCollisionException){
                erroUsuarioExistente.printStackTrace()
                exibirMensagem("E-mail já cadastrado")
            }catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem("E-mail inválido, digite novamente")
            }
        }
    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {
        firestore
            .collection("usuarios")
            .document( usuario.id )
            .set( usuario )
            .addOnSuccessListener {
                exibirMensagem("Cadastro efetuado com sucesso")
            }.addOnFailureListener{
                exibirMensagem("Erro ao fazer seu cadastro")
            }

    }

    private fun validarCampos(): Boolean{

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if ( nome.isNotEmpty()){
            binding.textInputNome.error = null
            if ( email.isNotEmpty() ){
                binding.textInputEmail.error = null

                if ( senha.isNotEmpty() ){
                    binding.textInputSenha.error = null
                    return true
                }else{
                    binding.textInputSenha.error = "Preencha a sua senha!"
                    return false
                }

            }else{
                binding.textInputEmail.error = "Preencha o seu email!"
                return false
            }
        }else{
            binding.textInputNome.error = "Preencha o seu nome!"
            return false
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.IncludeToolbar.tbPrincipal
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}