package br.com.codecall.codecall.controller.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.model.TipoUsuario
import br.com.codecall.codecall.model.Usuario
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)

        val botao_entrar: Button = findViewById(R.id.button_entrar)
        botao_entrar.setOnClickListener {
            var email = findViewById<EditText>(R.id.editText_email)
            var senha = findViewById<EditText>(R.id.editText_senha)
            entrar(email.text.toString(),senha.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        var currentUser = mAuth.currentUser
        if (currentUser != null) {
            goToDashboard(currentUser.uid.toString())
        }
    }

    fun entrar(email: String, senha: String) {
        mAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    var authID: String = mAuth.currentUser?.uid.toString()
                    goToDashboard(authID)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "Login inválido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun goToDashboard(authID: String) {
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("usuarios")
        usuariosRef.whereEqualTo("authID", authID).get().addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        val usuario = document.toObject(Usuario::class.java)
                        verificarUsuario(usuario)
                    }
                } else {
                    Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun verificarUsuario(usuario: Usuario) {
        if (usuario.tipoUsuario == TipoUsuario.SECRETARIO.tipoUsuario) {
            val intent = Intent(this, SecretarioDashboardActivity::class.java)
            startActivity(intent)
        } else if (usuario.tipoUsuario == TipoUsuario.PROFESSOR.tipoUsuario) {
            val intent = Intent(this, ProfessorDashboardActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, AlunoDashboardActivity::class.java)
            startActivity(intent)
        }
    }
}
