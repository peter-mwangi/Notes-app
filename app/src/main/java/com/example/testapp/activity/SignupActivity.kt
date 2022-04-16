package com.example.testapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.testapp.R
import com.example.testapp.model.User
import com.example.testapp.utils.Constants
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var fName: TextInputLayout
    private lateinit var lName: TextInputLayout
    private lateinit var mEmail: TextInputLayout
    private lateinit var mPassword: TextInputLayout
    private lateinit var confirmPassword: TextInputLayout
    private lateinit var loginLink: TextView
    private lateinit var signupBtn: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        initViews()
        signupBtn.setOnClickListener {

            validateInputData()
        }
        loginLink.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputData() {
        val firstName = fName.editText?.text.toString().trim()
        val lastName = lName.editText?.text.toString().trim()
        val email = mEmail.editText?.text.toString().trim()
        val password = mPassword.editText?.text.toString().trim()
        val cPassword = confirmPassword.editText?.text.toString().trim()

        if (firstName.isNotEmpty()) {

            if (lastName.isNotEmpty()) {

                if (email.isNotEmpty()) {

                    if (password.isNotEmpty()) {

                        if (password == cPassword) {

                            signupNewUser(firstName, lastName, email, password)

                        } else {
                            confirmPassword.error = "Password Mismatch!"
                        }
                    } else {
                        mPassword.error = "Required!"
                    }

                } else {
                    mEmail.error = "Required!"
                }
            } else {
                lName.error = "Required!"
            }
        } else {
            fName.error = "Required!"
        }
    }

    private fun signupNewUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "User Signed Up Successfully", Toast.LENGTH_LONG).show()
                saveUserData(firstName, lastName, email)
            } else {
                Toast.makeText(this, "Error Try again later!", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

    private fun saveUserData(firstName: String, lastName: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection(Constants.USERS)
        val userId = usersRef.document().id
        val userInfo = User(userId, firstName, lastName, email)
        usersRef.document(userId).set(userInfo).addOnSuccessListener {
            Toast.makeText(this, "User Data Saved Successfully", Toast.LENGTH_LONG).show()
            toHomeActivity(userInfo)
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun toHomeActivity(userInfo: User) {
        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
//        intent.putExtra(Constants.USER, userInfo)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun initViews() {
        fName = findViewById(R.id.first_name)
        lName = findViewById(R.id.last_name)
        mEmail = findViewById(R.id.email_address)
        mPassword = findViewById(R.id.password_field)
        confirmPassword = findViewById(R.id.confirm_password_field)
        signupBtn = findViewById(R.id.submit_btn)
        loginLink = findViewById(R.id.login_link)
    }
}