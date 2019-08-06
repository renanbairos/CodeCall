package br.com.codecall.codecall.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import br.com.codecall.codecall.model.Checkin
import br.com.codecall.codecall.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class AlunoDashboardActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    internal var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_dashboard_aluno)

        val botao_consultar_presencas: Button = findViewById(R.id.button_consultar_presencas)
        botao_consultar_presencas.setOnClickListener(){
            consultar()
        }

        val botao_realizar_chamada: Button = findViewById(R.id.button_realizar_chamada)
        botao_realizar_chamada.setOnClickListener {
            realizarChamada()
        }

        qrScanIntegrator?.setOrientationLocked(false)
        qrScanIntegrator = IntentIntegrator(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu -> {
                mAuth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun consultar() {
        val intent = Intent(this, AlunoMateriasActivity::class.java)
        startActivity(intent)
    }

    fun gravarCheckin(checkin: Checkin) {
        val db = FirebaseFirestore.getInstance()
        db.collection("checkin")
            .add(checkin)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.result_add_success), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            }
    }

    fun realizarChamada() {
        qrScanIntegrator?.initiateScan()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            //Se o QR code vier sem dados
            if (result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            } else {
                //Se o QR retornar dados
                try {
                    //Convertendo pra json
                    val obj = JSONObject(result.contents)
                    //Se um dia eu quiser capturar o horario, mudo o primeiro objeto
                    val checkin = Checkin(
                        Date(), mAuth.uid.toString(), obj.getString("idChamada")
                    )
                    gravarCheckin(checkin)
                } catch (e: JSONException) {
                    // Data com formato estranho
                    e.printStackTrace()
                    Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
        }
    }
}
