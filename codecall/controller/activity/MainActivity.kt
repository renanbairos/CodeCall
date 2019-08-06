package br.com.codecall.codecall.controller.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.model.TipoUsuario
import br.com.codecall.codecall.model.Usuario
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_main)

        val botao_entrar: Button = findViewById(R.id.button_entrar)
        botao_entrar.setOnClickListener {
            runOnUiThread({button_entrar.setBackgroundColor(Color.rgb(162,162,162))})
            runOnUiThread({it.isEnabled = false})
            val email = findViewById<EditText>(R.id.editText_email)
            val senha = findViewById<EditText>(R.id.editText_senha)
            if (email.text.toString().equals("")
                || senha.text.toString().equals("")) {
                Toast.makeText(
                    this, "Email ou senha inválido(a)",
                    Toast.LENGTH_SHORT
                ).show()
                runOnUiThread({button_entrar.isEnabled = true})
                runOnUiThread({button_entrar.setBackgroundColor(Color.rgb(2,11,132))})
            } else {
                entrar(email.text.toString(),senha.text.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            goToDashboard(currentUser.uid)
        }
    }

    fun entrar(email: String, senha: String) {
        mAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val authID: String = mAuth.currentUser?.uid.toString()
                    goToDashboard(authID)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "Login inválido",
                        Toast.LENGTH_SHORT
                    ).show()
                    runOnUiThread({button_entrar.isEnabled = true})
                    runOnUiThread({button_entrar.setBackgroundColor(Color.rgb(2,11,132))})
                }
            }
    }

    private fun goToDashboard(authID: String) {
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("usuarios")
        usuariosRef.document(authID).get().addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    val usuario = it.result.toObject(Usuario::class.java)
                    if (usuario != null) {
                        verificarUsuario(usuario)
                    }
                } else {
                    Toast.makeText(applicationContext, "Erro", Toast.LENGTH_LONG).show()
                    runOnUiThread({button_entrar.isEnabled = true})
                    runOnUiThread({button_entrar.setBackgroundColor(Color.rgb(2,11,132))})
                }
            }
        )
    }

    private fun verificarUsuario(usuario: Usuario) {
        val intent: Intent
        if (usuario.tipoUsuario == TipoUsuario.SECRETARIO.tipoUsuario) {
            intent = Intent(this, SecretarioDashboardActivity::class.java)
        } else if (usuario.tipoUsuario == TipoUsuario.PROFESSOR.tipoUsuario) {
            intent = Intent(this, ProfessorDashboardActivity::class.java)
        } else {
            intent = Intent(this, AlunoDashboardActivity::class.java)
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}
