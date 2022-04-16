package com.example.testapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testapp.activity.AddNoteActivity
import com.example.testapp.adapters.NotesAdapter
import com.example.testapp.databinding.FragmentNoteBinding
import com.example.testapp.model.Notes
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class NoteFragment : Fragment(), NotesAdapter.ItemClickListener {
    private lateinit var binding: FragmentNoteBinding
    private val _binding get() = binding!!

    private lateinit var notesAdapter: NotesAdapter
    private var user:User? = null
    private var note:Notes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = requireActivity().intent.getParcelableExtra(Constants.USER)
        note = requireActivity().intent.getParcelableExtra(Constants.NOTE)


        notesAdapter = NotesAdapter(requireContext(), this@NoteFragment)

        val db = FirebaseFirestore.getInstance()
        val notesReference = db.collection(Constants.NOTES)
        notesReference.whereEqualTo(Constants.UID, user?.userId).get().addOnCompleteListener { task ->

            if (task.isSuccessful){
                task.result.let {
                    val notesList = it.documents
                        .map { snapShot ->
                            snapShot.toObject(Notes::class.java)
                        }.sortedByDescending { note->
                            note?.timestamp
                        }.filterNotNull()
                        .toMutableList()
                    notesAdapter.addNotes(notesList)
                    notesAdapter.notifyDataSetChanged()

                }
            }
            else{
                Toast.makeText(requireActivity(), "This was not Successful. Try Again", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { e->
            Toast.makeText(requireActivity(), e.localizedMessage, Toast.LENGTH_LONG).show()

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)

        initViews()


        return _binding.root
    }

    private fun initViews() {
//        notesAdapter.addNotes()
        binding.apply {
            val fullName = "Hello ${user?.firstName} ${user?.lastName}"
            fullnameText.text = fullName
            notesRecyclerView.setHasFixedSize(true)
            notesRecyclerView.adapter = notesAdapter
            addNote.setOnClickListener {
                toAddNoteActivity()
            }
        }
    }

    private fun toAddNoteActivity() {
        val intent = Intent(requireActivity(), AddNoteActivity::class.java)
        intent.putExtra(Constants.USER, user)
        startActivity(intent)
    }
}