package br.com.codecall.codecall.controller.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.activity.ProfessorDashboardActivity
import br.com.codecall.codecall.model.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProfessorAdapter(private var onItemClicked: OnItemClicked, private var myDataset: MutableList<Materia>) :
    RecyclerView.Adapter<ProfessorAdapter.MyViewHolder>() {

    interface OnItemClicked{fun onItemClicked(materia: Materia)}

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    lateinit var text_materia_nome: TextView
    lateinit var text_materia_sigla: TextView
    lateinit var text_materia_periodo: TextView
    lateinit var botao_gerar_chamada: Button
    val db = FirebaseFirestore.getInstance()
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_view_professor, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        text_materia_nome = view.findViewById(R.id.text_materia_nome)
        text_materia_sigla = view.findViewById(R.id.text_materia_sigla)
        text_materia_periodo = view.findViewById(R.id.text_materia_periodo)
        botao_gerar_chamada = view.findViewById(R.id.botao_gerar_chamada)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        text_materia_nome.text = myDataset.get(position).nome
        text_materia_sigla.text = myDataset.get(position).sigla
        text_materia_periodo.text = myDataset.get(position).periodo
        botao_gerar_chamada.setOnClickListener { onItemClicked.onItemClicked(myDataset.get(position)) }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun setValue(listaMateria: MutableList<Materia>) {
        myDataset = listaMateria
    }
}