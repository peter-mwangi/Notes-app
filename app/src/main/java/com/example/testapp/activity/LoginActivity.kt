package com.example.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityLoginBinding
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.example.testapp.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

    }

    private fun initView() {
        binding.apply {
            loginBtn.setOnClickListener {
                validateUserInputs()
            }
            createAccoutLink.setOnClickListener {
                val signupIntent = Intent(this@LoginActivity, SignupActivity::class.java)
                startActivity(signupIntent)
            }
        }
    }

    private fun validateUserInputs() {
        binding.apply {
            val email = loginEmail.editText?.text.toString().trim()
            val password = loginPassword.editText?.text.toString().trim()
            if (email.isNotEmpty()) {

                if (password.isNotEmpty()) {

                    loginUser(email, password)
                } else {
                    loginPassword.error = "Required!"
                }
            } else {
                loginEmail.error = "Required!"
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                getCurrentUser(auth)
            } else {
                Toast.makeText(this, "Error, Please Try Again", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUser(auth: FirebaseAuth) {
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection(Constants.USERS)
        val uID = auth.currentUser!!.uid
        usersRef.whereEqualTo(Constants.UID, uID).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.let {
                    it.documents.map { snapShot ->
                        val user = snapShot.toObject(User::class.java)
                        val sessionManager = SessionManager(this@LoginActivity)
                        sessionManager.storeUserInfo(user!!)
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
        val loginIntent = Intent(this, HomeActivity::class.java)
        loginIntent.putExtra(Constants.USER, user)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginIntent)
    }

}