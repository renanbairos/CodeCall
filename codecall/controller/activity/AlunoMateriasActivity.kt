package br.com.codecall.codecall.controller.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.adapter.MateriaAdapter
import br.com.codecall.codecall.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class AlunoMateriasActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewAdapter: MateriaAdapter
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var onItemClick = object : MateriaAdapter.OnItemClick {
            override fun onItemClick(idMateria: String) {
                consultarPresencasMateria(idMateria)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno_materias)

        mAuth = FirebaseAuth.getInstance()
        val listaMateria: HashMap<String, Materia> = HashMap()
        getMateriaAluno(mAuth.uid.toString())
        setupRecyclerView(listaMateria)
    }

    private fun consultarPresencasMateria(idMateria: String) {
        val intent = Intent(this, AlunoPresencasActivity::class.java)
        intent.putExtra("idMateria", idMateria)
        startActivity(intent)
    }

    private fun getMateriaAluno(
        authID: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val checkinRef = db.collection("materiaAluno").whereEqualTo("idAluno", authID)
        checkinRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.isEmpty) {
                    var materiaAluno: MateriaAluno
                    for (document in task.result) {
                        materiaAluno = document.toObject(MateriaAluno::class.java)
                        getMaterias(db, materiaAluno.idMateria)
                    }
                } else {
                    Toast.makeText(applicationContext, "Sem matérias cadastradas para o aluno.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Erro na consulta de matérias relacionadas ao aluno.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getMaterias(
        db: FirebaseFirestore,
        idMateria: String
    ) {
        val listaMateria: HashMap<String, Materia> = HashMap()
        db.collection("materias").document(idMateria).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val materia = document.toObject(Materia::class.java)
                    if (materia != null)
                        listaMateria.put(document.id, materia)
                    else
                        Toast.makeText(applicationContext, "Erro ao converter matéria(s).", Toast.LENGTH_LONG)
                            .show()
                } else {
                    Toast.makeText(applicationContext, "Registro de matéria apresenta erro.", Toast.LENGTH_LONG)
                        .show()
                }
                atualizarRecyclerView(listaMateria)
            }
    }

    private fun atualizarRecyclerView(listaMateria: HashMap<String, Materia>) {
        viewAdapter.setValue(listaMateria)
        viewAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(listaMateria: HashMap<String, Materia>) {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = MateriaAdapter(listaMateria, onItemClick)

        recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewMateria).apply {
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