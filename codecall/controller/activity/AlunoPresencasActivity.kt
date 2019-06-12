package br.com.codecall.codecall.controller.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.adapter.AlunoAdapter
import br.com.codecall.codecall.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class AlunoPresencasActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: AlunoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_presencas)

        mAuth = FirebaseAuth.getInstance()
        consultarPresencas(mAuth.uid.toString())
    }

    private fun consultarPresencas(authID: String) {
        val db = FirebaseFirestore.getInstance()
        val listaHistorico: MutableList<Historico> = ArrayList()
        getCheckins(db, authID, listaHistorico)
        setupRecyclerView(listaHistorico)
    }

    private fun getCheckins(
        db: FirebaseFirestore,
        authID: String,
        listaHistorico: MutableList<Historico>
    ) {
        val checkinRef = db.collection("checkin").whereEqualTo("idAluno", authID)
        checkinRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.isEmpty) {
                    for (document in task.result) {
                        val checkin = document.toObject(Checkin::class.java)
                        getChamadas(db, checkin, listaHistorico)
                    }
                } else {
                    Toast.makeText(applicationContext, "Sem presenças cadastradas", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Erro na consulta de presenças", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getChamadas(
        db: FirebaseFirestore,
        checkin: Checkin,
        listaHistorico: MutableList<Historico>
    ) {
        db.collection("chamadas").document(checkin.idChamada).get().addOnCompleteListener { task2 ->
            if (task2.isSuccessful) {
                if (task2.result.exists()) {
                    val chamada = task2.result.toObject(Chamada::class.java)
                    getMaterias(db, chamada, checkin, listaHistorico)
                } else {
                    Toast.makeText(applicationContext, "Algum(ns) registros apresentam erro. Verifique com o professor.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Erro na consulta de chamadas", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun getMaterias(
        db: FirebaseFirestore,
        chamada: Chamada?,
        checkin: Checkin,
        listaHistorico: MutableList<Historico>
    ) {
        db.collection("materias").whereEqualTo("idMateria", chamada?.idMateria).get()
            .addOnCompleteListener { task3 ->
                if (task3.isSuccessful) {
                    if (!task3.result.isEmpty) {
                        for (document2 in task3.result) {
                            val materia = document2.toObject(Materia::class.java)
                            getUsuarios(db, materia, checkin, listaHistorico)
                        }
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
            .whereEqualTo("authID", materia.idProfessor).get()
            .addOnCompleteListener { task4 ->
                if (task4.isSuccessful) {
                    if (!task4.result.isEmpty) {
                        for (document3 in task4.result) {
                            val usuario = document3.toObject(Usuario::class.java)
                            val historico: Historico = Historico()
                            historico.horaCriacao = checkin.dataCriacao
                            historico.nomeProfessor = usuario.nome
                            historico.siglaMateria = materia.sigla
                            listaHistorico.add(historico)
                        }
                    } else {
                        Toast.makeText(applicationContext,"Professor desconhecido",Toast.LENGTH_LONG).show()
                    }
                    atualizarRecyclerView(listaHistorico)
                } else {
                    Toast.makeText(applicationContext,"Erro na consulta de professores",Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun atualizarRecyclerView(listaHistorico: MutableList<Historico>) {
        viewAdapter.setValue(listaHistorico)
        viewAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(listaCheckin: MutableList<Historico>) {
        viewManager = LinearLayoutManager(this)
        viewAdapter = AlunoAdapter(listaCheckin)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAluno).apply {
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
