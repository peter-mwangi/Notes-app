package com.example.testapp.repository

import com.example.testapp.model.Notes
import com.example.testapp.utils.Constants
import com.example.testapp.utils.Results
import com.google.firebase.firestore.FirebaseFirestore

class NotesRepository {
    //Data Model class
    val db = FirebaseFirestore.getInstance()
    val notesRef = db.collection(Constants.NOTES)

    fun createNote(note: Notes, result: (Results<Boolean>) -> Unit) {
        notesRef.add(note).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result(Results.Success(true))
            } else {
                result(Results.Error("Note Addition was unsuccessful"))
            }
        }.addOnFailureListener { e ->
            result(Results.Error(e.localizedMessage))
        }
    }
    fun getNoteId(): String{
        return notesRef.document().id
    }

}