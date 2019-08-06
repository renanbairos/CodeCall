package br.com.codecall.codecall.controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.adapter.AlunoAdapter
import br.com.codecall.codecall.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class AlunoPresencasActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: AlunoAdapter
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_presencas)

        mAuth = FirebaseAuth.getInstance()
        consultarPresencasMateria(mAuth.uid.toString(), intent.getStringExtra("idMateria"))
    }

    private fun consultarPresencasMateria(authID: String, idMateria: String) {
        val db = FirebaseFirestore.getInstance()
        val listaHistorico: MutableList<Historico> = ArrayList()
        getCheckins(db, authID, listaHistorico, idMateria)
        setupRecyclerView(listaHistorico)
    }

    private fun getCheckins(
        db: FirebaseFirestore,
        authID: String,
        listaHistorico: MutableList<Historico>,
        idMateria: String
    ) {
        val checkinRef = db.collection("checkin").whereEqualTo("idAluno", authID)
        checkinRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.isEmpty) {
                    for (document in task.result) {
                        val checkin = document.toObject(Checkin::class.java)
                        getMaterias(db, checkin, listaHistorico, idMateria)
                    }
                } else {
                    Toast.makeText(applicationContext, "Sem presenças cadastradas", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Erro na consulta de presenças", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getMaterias(
        db: FirebaseFirestore,
        checkin: Checkin,
        listaHistorico: MutableList<Historico>,
        idMateria: String
    ) {
        db.collection("materias").document(idMateria).get()
            .addOnCompleteListener { task3 ->
                if (task3.isSuccessful) {
                    if (task3.result.exists()) {
                        val materia = task3.result.toObject(Materia::class.java)
                        if (materia != null)
                            getUsuarios(db, materia, checkin, listaHistorico)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Matéria desconhecida",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Erro na consulta de matérias",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun getUsuarios(
        db: FirebaseFirestore,
        materia: Materia,
        checkin: Checkin,
        listaHistorico: MutableList<Historico>
    ) {
        db.collection("usuarios")
            .document(materia.idProfessor).get()
            .addOnCompleteListener { task4 ->
                if (task4.result != null) {
                    val usuario = task4.result.toObject(Usuario::class.java)
                    if(usuario == null) {
                        Toast.makeText(applicationContext,"Erro na consulta de professores",Toast.LENGTH_LONG).show()
                    } else {
                        val historico: Historico = Historico()
                        historico.horaCriacao = checkin.dataCriacao
                        historico.nomeProfessor = usuario.nome
                        historico.siglaMateria = materia.sigla
                        listaHistorico.add(historico)
                    }
                } else {
                    Toast.makeText(applicationContext,"Erro na consulta de professores",Toast.LENGTH_LONG).show()
                }
                atualizarRecyclerView(listaHistorico)
            }.addOnFailureListener{
                Toast.makeText(applicationContext, "Erro no banco ao consultar professor", Toast.LENGTH_LONG).show()
            }
    }

    private fun atualizarRecyclerView(listaHistorico: MutableList<Historico>) {
        viewAdapter.setValue(listaHistorico)
        viewAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(listaCheckin: MutableList<Historico>) {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = AlunoAdapter(listaCheckin)

        recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewAluno).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }
}
