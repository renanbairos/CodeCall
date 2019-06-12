package br.com.codecall.codecall.controller.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.adapter.ProfessorAdapter
import br.com.codecall.codecall.model.Chamada
import br.com.codecall.codecall.model.Materia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.*
import kotlin.collections.ArrayList

class ProfessorDashboardActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProfessorAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    internal var bitmap: Bitmap? = null

    fun onItemClicked(): ProfessorAdapter.OnItemClicked = object : ProfessorAdapter.OnItemClicked {
        override fun onItemClicked(materia: Materia) {
            val intent = Intent(applicationContext, ProfessorQRCodeActivity::class.java)
            intent.putExtra("materia", materia)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_dashboard_professor)

        consultar(mAuth.uid.toString())
    }

    fun consultar(mAuth: String) {
        val db = FirebaseFirestore.getInstance()
        var listaMateria: MutableList<Materia> = ArrayList<Materia>()
        db.collection("materias").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!task.result.isEmpty) {
                    for (document in task.result) {
                        val materia = document.toObject(Materia::class.java)
                        listaMateria.add(materia)
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

    private fun atualizarRecyclerView(listaMateria: MutableList<Materia>) {
        viewAdapter.setValue(listaMateria)
        viewAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView(listaMateria: MutableList<Materia>) {
        viewManager = LinearLayoutManager(this)
        viewAdapter = ProfessorAdapter(onItemClicked(), listaMateria)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewProfessor).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            val itemDecor = DividerItemDecoration(context, HORIZONTAL)
            addItemDecoration(itemDecor)
        }
    }
}
