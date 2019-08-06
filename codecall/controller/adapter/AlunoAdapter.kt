package br.com.codecall.codecall.controller.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.codecall.codecall.R
import br.com.codecall.codecall.model.*
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class AlunoAdapter(private var myDataset: MutableList<Historico>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<AlunoAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    lateinit var text_hora_criacao: TextView
    lateinit var text_data_criacao: TextView
    lateinit var text_materia_sigla: TextView
    lateinit var text_professor: TextView
    val db = FirebaseFirestore.getInstance()
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_view_aluno, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        text_hora_criacao = view.findViewById(R.id.text_hora_criacao)
        text_data_criacao = view.findViewById(R.id.text_data_criacao)
        text_materia_sigla = view.findViewById(R.id.text_materia_sigla)
        text_professor = view.findViewById(R.id.text_professor)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        text_hora_criacao.text = getHora(position)
        text_data_criacao.text = getData(position)
        text_materia_sigla.text = myDataset.get(position).siglaMateria
        text_professor.text = myDataset.get(position).nomeProfessor
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun setValue(listaHistorico: MutableList<Historico>) {
        myDataset = listaHistorico
    }

    private fun getHora(position: Int): String {
        return SimpleDateFormat("HH:mm").format(myDataset.get(position).horaCriacao).toString()
    }
    private fun getData(position: Int): String {
        return SimpleDateFormat("dd/MM").format(myDataset.get(position).horaCriacao).toString()
    }
}