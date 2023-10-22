package com.volpe.fateczap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.volpe.fateczap.databinding.ActivityLoginBinding
import com.volpe.fateczap.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setContentView( binding.root )
        inicializarEventosClique()
        firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if( usuarioAtual != null ){
            startActivity(
            Intent(this, MainActivity::class.java)
            )
        }
    }

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener{
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener{
            if ( validarCampos() ){
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {

        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {
            exibirMensagem("Logado com sucesso")
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
            )
        }.addOnFailureListener { erro ->
            try {
                throw erro
            }catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("E-mail não cadastrado")
            }catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem("E-mail ou senha estão incorretos")
            }catch (e: Exception) {
                e.printStackTrace()
                exibirMensagem("Ocorreu um erro ao logar, tente novamente!")
            }
        }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        // Verificar o campo de email e senha da tela de login
        if ( email.isNotEmpty() ){
            binding.textInputLayoutLoginEmail.error = null

            if ( senha.isNotEmpty() ){
                binding.textInputLayoutLoginSenha.error = null
                return true
            }else{
                binding.textInputLayoutLoginSenha.error = "Preencha a senha"
                return false
            }

        }else{
            binding.textInputLayoutLoginEmail.error = "Preencha o email"
            return false
        }
    }
}