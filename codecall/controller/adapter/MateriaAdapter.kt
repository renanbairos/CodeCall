package br.com.codecall.codecall.controller.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import br.com.codecall.codecall.R
import br.com.codecall.codecall.controller.activity.AlunoMateriasActivity
import br.com.codecall.codecall.model.Materia
import com.google.firebase.firestore.FirebaseFirestore

class MateriaAdapter(private var myDataset: HashMap<String, Materia>,
                     private var onItemClick: OnItemClick) :
    androidx.recyclerview.widget.RecyclerView.Adapter<MateriaAdapter.MyViewHolder>() {

    interface OnItemClick {
        fun onItemClick(idMateria: String)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    lateinit var botao_materia_consulta: Button
    var id_materia_consulta: String = ""
    val db = FirebaseFirestore.getInstance()
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.button_materia, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        botao_materia_consulta = view.findViewById(R.id.botao_materia_consulta)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val key = myDataset.keys.elementAt(position)
        botao_materia_consulta.text = myDataset[key]?.sigla
        id_materia_consulta = key
        val id_materia = id_materia_consulta
        botao_materia_consulta.setOnClickListener(){
            onItemClick.onItemClick(id_materia)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun setValue(listaMateria: HashMap<String, Materia>) {
        myDataset = listaMateria
    }

}