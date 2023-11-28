package com.example.pppbnotesfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pppbnotesfirebase.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val noteCollectionRef = firestore.collection("notes")
    private val noteListLiveData : MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        observeNotes()
        getAllNotes()

        with(binding){
            btnAddNote.setOnClickListener {
                try {
                    val intentToEditActivity = Intent(this@MainActivity, EditActivity::class.java)
                    startActivity(intentToEditActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            inputSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
                override fun afterTextChanged(s: Editable?) {
                    val keyword = s.toString()
                    getAllNotes(keyword)
                }

            })
        }
    }
    private fun getAllNotes(keyword : String = "") {
        observerNoteChanges()
    }
    private fun observerNoteChanges() {
        noteCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for note changes: ", error)
            }
            val notes = snapshots?.toObjects(Note::class.java)
            if (notes != null) {
                noteListLiveData.postValue(notes)
            }
        }
    }
    private fun observeNotes() {
        noteListLiveData.observe(this) {
                notes ->
            val adapterNote = NoteAdapter(notes) { note ->
                val intentToEditActivity = Intent(this@MainActivity, EditActivity::class.java)
                intentToEditActivity.putExtra("id", note.id)
                startActivity(intentToEditActivity)
            }
            binding.rvNotes.apply {
                adapter = adapterNote
                layoutManager = LinearLayoutManager(this@MainActivity)

            }
        }
    }
    override fun onResume() {
        super.onResume()
        getAllNotes()
    }
}