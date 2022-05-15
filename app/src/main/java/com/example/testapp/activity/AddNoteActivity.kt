package com.example.testapp.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.testapp.R
import com.example.testapp.businesslogic.NotesViewModel
import com.example.testapp.databinding.ActivityAddNoteBinding
import com.example.testapp.model.Notes
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.example.testapp.utils.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var currentDateTime: String
    private lateinit var user: User
    private var photoPath: String =""
    private lateinit var cameraUri: Uri
    private lateinit var notesViewModel: NotesViewModel

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){activityResults ->
        if (activityResults.resultCode == RESULT_OK){
            photoPath = cameraUri.toString()
            Glide.with(this).load(photoPath).into(binding.addNoteImage)
        }
    }
    private val galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResults ->
        if (activityResults.resultCode == RESULT_OK){
            val imageUri = activityResults.data?.data
            if (imageUri != null){
                photoPath = imageUri.toString()
                Glide.with(this).load(photoPath).into(binding.addNoteImage)
            }
            else{
                Toast.makeText(this@AddNoteActivity, "No image was selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        user = intent.getParcelableExtra(Constants.USER)!!
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        addNoteLiveData()

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

    private fun addNoteLiveData() {
        notesViewModel.createPublicNoteLiveData.observe(this){
            when(it.status){
                Status.LOADING ->{
                    // TODO: Progress Bar
                }
                Status.SUCCESS ->{
                    toHomeActivity()
                }
                Status.ERROR ->{
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initViews() {
        binding.apply {
            saveNoteBtn.setOnClickListener {
                validateUserInputs()
            }

            addNoteCameraIcon.setOnClickListener {
                checkPermission()
            }
        }
    }

    private fun addPhotoDialog() {
        AlertDialog.Builder(this)
            .setTitle("Upload using:")
            .setItems(R.array.media_options) { _, i ->
                if (i == 0) {
                    toGallery()
                } else if (i == 1) {
                    toCamera()
                }
            }.create()
            .show()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission
                (
                this@AddNoteActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission
                (
                this@AddNoteActivity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission
                (
                this@AddNoteActivity,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            addPhotoDialog()

        } else {
            ActivityCompat.requestPermissions(
                this@AddNoteActivity,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ),
                100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                toCamera()
            }
            else{
                Toast.makeText(this@AddNoteActivity, "The App won't work without permissions", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this@AddNoteActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null){
            val file = createStorageFile()
            cameraUri = FileProvider.getUriForFile(this@AddNoteActivity, "com.example.testapp.fileprovider", file)
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    private fun createStorageFile(): File {
        val timestamp = SimpleDateFormat("yyyymmdd hhmmss", Locale.getDefault()).format(Date())
        val imageName = "/jpeg_$timestamp"
        return File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!, imageName)
    }

    private fun toGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type="image/*"
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        galleryActivityResultLauncher.launch(Intent.createChooser(galleryIntent, "Selected image:"))
    }

    private fun getFileExtensions(): String? {
        val resolver = this.contentResolver
        val mimeType = MimeTypeMap.getSingleton()
        return mimeType.getExtensionFromMimeType(resolver.getType(photoPath.toUri()))
    }

    private fun validateUserInputs() {
        binding.apply {
            val title = addNoteTitle.editText?.text.toString().trim()
            val noteBody = addNoteDetail.editText?.text.toString().trim()
            val reminder = addNoteDate.text.toString()

            if (title.isNotEmpty()) {

                if (noteBody.isNotEmpty()) {
                    if (photoPath.isNotEmpty()){
                        storeImageInFirestore(title, noteBody, reminder)
                    }
                    else{
                        saveNote(title, noteBody, reminder)
                    }

                } else {
                    addNoteTitle.error = "Cannot be empty"
                }
            } else {
                addNoteDetail.error = "Cannot be empty"
            }
        }
    }

    private fun storeImageInFirestore(title: String, noteBody: String, reminder: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){

            val storageRef = FirebaseStorage.getInstance().getReference("uploads")
            val fileRef = storageRef.child("notes_images")
                .child("${System.currentTimeMillis()}.${getFileExtensions()}")
            fileRef.putFile(photoPath.toUri()).addOnCompleteListener { task->
                if (task.isSuccessful){
                    Toast.makeText(this@AddNoteActivity, "Image uploaded to firebase storage successfully", Toast.LENGTH_SHORT).show()
                    saveNote(title, noteBody, reminder)
                }
                else{
                    Toast.makeText(this@AddNoteActivity, "Failed to upload the image to firebase storage", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { e->
                Toast.makeText(this@AddNoteActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveNote(title: String, noteBody: String, reminder: String) {
        val uId = user.userId!!
        val currentDate = SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())
        val currentTime = SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis())
        currentDateTime = "$currentDate $currentTime"
        val noteId = notesViewModel.getNoteId()
        val note = Notes(noteId, uId, title, currentDateTime, noteBody, reminder, photoPath)
        notesViewModel.createNote(note)
    }

    private fun toHomeActivity() {
        val intent = Intent(this@AddNoteActivity, HomeActivity::class.java)
        intent.putExtra(Constants.USER, user)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}