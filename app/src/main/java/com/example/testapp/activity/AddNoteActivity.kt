package com.example.testapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.testapp.R
import com.example.testapp.model.Notes
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var noteTitle: TextInputLayout
    private lateinit var noteDesc: TextInputLayout
    private lateinit var reminderDate: TextView
    private lateinit var saveNoteBtn: Button
    private lateinit var cancelNoteBtn: Button
    private lateinit var currentDateTime: String
    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        initViews()
         user = intent.getParcelableExtra(Constants.USER)!!

        val calendar = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd.MM.yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                reminderDate.text = sdf.format(calendar.time)

            }

        reminderDate.setOnClickListener {
            DatePickerDialog(
                this@AddNoteActivity, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        saveNoteBtn.setOnClickListener {
            validateUserInputs()
        }
    }

    private fun validateUserInputs() {
        val title = noteTitle.editText?.text.toString().trim()
        val noteBody = noteDesc.editText?.text.toString().trim()
        val reminder = reminderDate.text.toString()

        if (title.isNotEmpty()) {

            if (noteBody.isNotEmpty()) {

                saveNote(title, noteBody, reminder)

            } else {
                noteDesc.error = "Cannot be empty"
            }
        } else {
            noteTitle.error = "Cannot be empty"
        }
    }

    private fun saveNote(title: String, noteBody: String, reminder: String) {
        val db = FirebaseFirestore.getInstance()
        val notesReference = db.collection(Constants.NOTES)

        val uId = user.userId!!
        val currentDate = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
        val currentTime = SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis())
        currentDateTime = "$currentDate $currentTime"
        val noteId = notesReference.document().id
        val note = Notes(noteId, uId, title, currentDateTime, noteBody, reminder)
        notesReference.add(note).addOnSuccessListener {
            Toast.makeText(this, "New Note Created", Toast.LENGTH_LONG).show()
            toHomeActivity(note)

        }.addOnFailureListener { e->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun toHomeActivity(note: Notes) {
        val intent = Intent(this@AddNoteActivity, HomeActivity::class.java)
        intent.putExtra(Constants.NOTE, note)
        intent.putExtra(Constants.USER, user)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun initViews() {
        noteTitle = findViewById(R.id.add_note_title)
        noteDesc = findViewById(R.id.add_note_detail)
        reminderDate = findViewById(R.id.add_note_date)
        saveNoteBtn = findViewById(R.id.save_note_btn)
        cancelNoteBtn = findViewById(R.id.cancel_note_btn)
    }

}