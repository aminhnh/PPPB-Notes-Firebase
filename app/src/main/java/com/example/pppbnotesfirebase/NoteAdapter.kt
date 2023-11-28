package com.example.pppbnotesfirebase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

typealias OnClickMember = (Note) -> Unit
class NoteAdapter(private val listMember : List<Note>, private val onClickMember: OnClickMember) : RecyclerView.Adapter<NoteAdapter.ItemNoteViewHolder>() {
    inner class ItemNoteViewHolder(private val binding : ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note : Note) {
            with(binding) {
                txtTitle.text = note.title
                txtDesc.text = note.description

                itemView.setOnClickListener {
                    onClickMember(note)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemNoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemNoteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listMember.size
    }

    override fun onBindViewHolder(holder: ItemNoteViewHolder, position: Int) {
        holder.bind(listMember[position])
    }
}