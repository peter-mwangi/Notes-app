package com.example.testapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.model.Notes

class NotesAdapter(private val context:Context, private val itemClickListener: ItemClickListener): RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    private var notes:MutableList<Notes> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.note_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(notes[position])

    }

    override fun getItemCount(): Int {
        return notes.size

    }
    fun addNotes(updatedList: MutableList<Notes>){
        notes = updatedList
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private lateinit var noteTitle:TextView
        private lateinit var  noteDate: TextView
        private lateinit var notesCardView: CardView

        fun bind(notes: Notes) {
            noteTitle = itemView.findViewById(R.id.note_title)
            noteDate = itemView.findViewById(R.id.note_date)
            notesCardView = itemView.findViewById(R.id.notes_card_view)
            noteTitle.text = notes.title
            noteDate.text = notes.timestamp

            notesCardView.setOnClickListener(this)
        }


        override fun onClick(view: View?) {
            val note = notes[adapterPosition]
            if (view != null) {
                itemClickListener.itemClicked(view, note)
            }
        }
    }
    interface ItemClickListener{
        fun itemClicked(view:View, note: Notes){

        }
    }
}