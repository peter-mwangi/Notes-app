package com.example.testapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.R
import com.example.testapp.databinding.ActivityAddNoteBinding
import com.example.testapp.model.Notes
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var currentDateTime: String
    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                binding.addNoteDate.text = sdf.format(calendar.time)

            }

        binding.addNoteDate.setOnClickListener {
            DatePickerDialog(
                this@AddNoteActivity, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }

    private fun initViews() {
        binding.apply {
            saveNoteBtn.setOnClickListener {
                validateUserInputs()
            }

            addNoteCameraIcon.setOnClickListener {
                addPhotoDialog()
            }
        }
    }

    private fun addPhotoDialog() {
        AlertDialog.Builder(this)
            .setTitle("Upload using:")
            .setItems(R.array.media_options)
    }

    private fun validateUserInputs() {
        binding.apply {
            val title = addNoteTitle.editText?.text.toString().trim()
            val noteBody = addNoteDetail.editText?.text.toString().trim()
            val reminder = addNoteDate.text.toString()

            if (title.isNotEmpty()) {

                if (noteBody.isNotEmpty()) {

                    saveNote(title, noteBody, reminder)

                } else {
                    addNoteTitle.error = "Cannot be empty"
                }
            } else {
                addNoteDetail.error = "Cannot be empty"
            }
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

        }.addOnFailureListener { e ->
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

}