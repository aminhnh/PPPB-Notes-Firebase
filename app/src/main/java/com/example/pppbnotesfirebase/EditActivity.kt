package com.example.pppbnotesfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.pppbnotesfirebase.databinding.ActivityEditBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val noteCollectionRef = firestore.collection("notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val noteId = intent.getStringExtra("id")

        with(binding) {
            if (!noteId.isNullOrEmpty()) {
                // UPDATE NOTE
                val note = getNoteById(noteId)
                if (note != null){
                    fillEditText(note)
                }

                btnSave.setOnClickListener {
                    val updatedNote = Note(
                        noteId,
                        inputTitle.text.toString(),
                        inputDesc.text.toString(),
                        getCurrentDate(),
                        getCurrentTime()
                    )
                    updateNoteAndReturn(updatedNote)
                }
            } else {
                // ADD NOTE
                btnSave.setOnClickListener {
                    val newNote = Note(
                        id = "",
                        title = inputTitle.text.toString(),
                        description = inputDesc.text.toString(),
                        last_updated_date = getCurrentDate(),
                        last_updated_time = getCurrentTime()
                    )
                    insertNoteAndReturn(newNote)
                }
            }
            btnDelete.setOnClickListener {
                if (!noteId.isNullOrEmpty() ){
                    val emptyNote = Note(noteId, "", "", "", "")
                    deleteNoteAndReturn(emptyNote)
                }
            }
        }

    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(Date())
    }
    private fun insertNoteAndReturn(note: Note) {
        if (note.description.isEmpty() && note.title.isEmpty() ){
            Toast.makeText(this, "Unable to save empty note", Toast.LENGTH_SHORT).show()
            returnToMainActivity()
        } else {
            noteCollectionRef.add(note).addOnSuccessListener {
                    documentReference ->
                val createdNoteId = documentReference.id
                note.id = createdNoteId
                documentReference.set(note).addOnSuccessListener {
                    Log.d("MainActivity", "Note added successfully")
                }.addOnFailureListener {
                        exception ->
                    Log.d("MainActivity", "Failed to set note", exception)
                }
            }.addOnFailureListener {
                    exception ->
                Log.d("MainActivity", "Failed to add note", exception)
                Toast.makeText(this, "Error adding note", Toast.LENGTH_SHORT).show()
            }
            returnToMainActivity()
        }
    }
    private fun updateNoteAndReturn(note: Note) {
        if (note.description.isEmpty() && note.title.isEmpty() ){
            Toast.makeText(this, "Unable to save empty note", Toast.LENGTH_SHORT).show()
        } else {
            noteCollectionRef.document(note.id).set(note).addOnFailureListener {
                Log.d("MainActivity", "Error updating note", it)
                Toast.makeText(this, "Error updating note", Toast.LENGTH_SHORT).show()
            }
        }
        returnToMainActivity()
    }
    private fun deleteNoteAndReturn(note: Note) {
        if (note.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting item!")
        }
        noteCollectionRef.document(note.id).delete().addOnFailureListener {
            Log.d("MainActivity", "Error deleting item!")
        }
        returnToMainActivity()
    }
    private fun returnToMainActivity() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)
    }
    private fun fillEditText(note: Note) {
        with(binding){
            inputTitle.setText(note.title)
            inputDesc.setText(note.description)
        }
    }
    private fun getNoteById(noteId: String) : Note? {
        val note : Note? = null

        val noteDocumentRef = noteCollectionRef.document(noteId)
        noteDocumentRef.get().addOnSuccessListener {
                documentSnapshot ->
            if (documentSnapshot.exists()) {
                val note = documentSnapshot.toObject(Note::class.java)
            }
        }.addOnFailureListener {
            Log.d("MainActivity", "Error getting note by ID: ", it)
        }

        return note
    }
}