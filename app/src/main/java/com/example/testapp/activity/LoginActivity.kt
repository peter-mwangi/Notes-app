package com.example.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.R
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var mEmail: TextInputLayout
    private lateinit var mPassword: TextInputLayout
    private lateinit var loginBtn: Button
    private lateinit var signupLink: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()

        auth = FirebaseAuth.getInstance()
        loginBtn.setOnClickListener {
            validateUserInputs()
        }
        signupLink.setOnClickListener {
            intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUserInputs() {
        val email = mEmail.editText?.text.toString().trim()
        val password = mPassword.editText?.text.toString().trim()

        if (email.isNotEmpty()) {

            if (password.isNotEmpty()) {

                loginUser(email, password)
            } else {
                mPassword.error = "Required!"
            }
        } else {
            mEmail.error = "Required!"
        }
    }

    private fun loginUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                getCurrentUser()
            } else {
                Toast.makeText(this, "Error, Please Try Again", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUser() {

        val currentUserID = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection(Constants.USERS)
        usersRef.whereEqualTo(Constants.UID, currentUserID).get().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                task.result.let {
                    it.documents.map { snapShot ->
                        val user = snapShot.toObject(User::class.java)
                        toHomeActivity(user)
                    }
                }
            } else {
                Toast.makeText(this, "Try again later", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toHomeActivity(user: User?) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.USER, user)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun initView() {
        mEmail = findViewById(R.id.login_email)
        mPassword = findViewById(R.id.login_password)
        loginBtn = findViewById(R.id.login_btn)
        signupLink = findViewById(R.id.create_accout_link)
    }
}