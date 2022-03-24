package com.example.tasks.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.service.helper.FingerPrintHelper
import com.example.tasks.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Inicializa eventos
        setListeners();
        observe()
        // Verifica se usuário está logado
        verifyLoggedUser()


    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_login) {
            handleLogin()
        } else if (v.id == R.id.text_register) {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showAutentication() {
        //Executor
        val executor : Executor = ContextCompat.getMainExecutor(this)

        //BiometricsPrompt
        val biometricPrompt = BiometricPrompt( this@LoginActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
               override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                   super.onAuthenticationSucceeded(result)
                   startActivity(Intent(applicationContext, MainActivity::class.java))
                   finish()
               }
           }

        )

        //BiometricsPrompt INFO
        var info: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Título")
            .setSubtitle("Subtitulo")
            .setDescription("Descrição")
            .setNegativeButtonText("Cancelar") //Obrigatorio
            .build()

        biometricPrompt.authenticate(info)
    }

    /**
     * Inicializa os eventos de click
     */
    private fun setListeners() {
        button_login.setOnClickListener(this)
        text_register.setOnClickListener(this)
    }

    /**
     * Verifica se usuário está logado
     */
    private fun verifyLoggedUser() {
        mViewModel.verifyLoggedUser()
    }

    /**
     * Observa ViewModel
     */
    private fun observe() {
        mViewModel.login.observe(this, Observer {
            if(it.success()){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                val message = it.failure()
                val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0,0 )
                toast.show()

            }
        })

        mViewModel.loggedUser.observe(this, Observer {
            if(it) {
                // Verifica se tem leitor Biometrico
                if (FingerPrintHelper.isAuthenticationAvailable(this)) {
                    showAutentication()
                }
            }
        })
    }

    /**
     * Autentica usuário
     */
    private fun handleLogin() {
        val email = edit_email.text.toString()
        val password = edit_password.text.toString()

        mViewModel.doLogin(email, password)
    }

}
