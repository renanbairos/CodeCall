package br.com.codecall.codecall.controller.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.adapter.ProfessorAdapter
import br.com.codecall.codecall.model.Materia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class ProfessorDashboardActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: ProfessorAdapter
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    internal var bitmap: Bitmap? = null

    fun onItemClicked(): ProfessorAdapter.OnItemClicked = object : ProfessorAdapter.OnItemClicked {
        override fun onItemClicked(idMateria: String) {
            val intent = Intent(applicationContext, ProfessorQRCodeActivity::class.java)
            intent.putExtra("idMateria", idMateria)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_dashboard_professor)

        consultar()
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
        val db = FirebaseFirestore.getInstance()
        var listaMateria: HashMap<String,Materia> = HashMap()
        db.collection("materias").whereEqualTo("idProfessor", mAuth.currentUser?.uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.isEmpty) {
                    for (document in task.result) {
                        val materia = document.toObject(Materia::class.java)
                        listaMateria.put(document.id, materia)
                    }
                } else {
                    Toast.makeText(applicationContext, "Sem matérias cadastradas para o professor", Toast.LENGTH_LONG).show()
                }
                atualizarRecyclerView(listaMateria)
            } else {
                Toast.makeText(applicationContext, "Erro na consulta matérias", Toast.LENGTH_LONG).show()
            }
        }
        setupRecyclerView(listaMateria)
    }

    private fun atualizarRecyclerView(listaMateria: HashMap<String,Materia>) {
        viewAdapter.setValue(listaMateria)
        viewAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(listaMateria: HashMap<String,Materia>) {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = ProfessorAdapter(onItemClicked(), listaMateria)

        recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewProfessor).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            val itemDecor = androidx.recyclerview.widget.DividerItemDecoration(context, HORIZONTAL)
            addItemDecoration(itemDecor)
        }
    }
}
