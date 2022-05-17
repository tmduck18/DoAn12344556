package com.example.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.blog.data.UploadProfile
import com.example.blog.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.concurrent.timerTask

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore

    var emailValidation = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        binding.constraint.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
            }
        })

        binding.btnLogin.setOnClickListener { Login() }
        binding.btnSignup.setOnClickListener { Signup() }

    }

    private fun Signup() {
        if (checkValidation()) {
            RegisterFirebase(binding.edtEmail.text.toString(), binding.edtPass.text.toString())
        }
    }

    private fun RegisterFirebase(email: String, pass: String) {
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuthencation", "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    createDataProfile(user)
                    Timer().schedule(timerTask {
                        val intent = Intent(this@SignupActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    }, 3000)
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Snackbar.make(
                        binding.root, "Authentication failed.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun createDataProfile(user: FirebaseUser?) {
        if (user != null) {
            val email = binding.edtEmail.text.toString()
            val uName = email.substring(0, email.indexOf("@"))
            val uId = user.uid
            val uAvatar =
                "https://firebasestorage.googleapis.com/v0/b/blog-93a0b.appspot.com/o/t%E1%BA%A3i%20xu%E1%BB%91ng.png?alt=media&token=55aaf24a-0285-40ee-8587-0f53a310f499"
            val data = UploadProfile(ServerValue.TIMESTAMP, ServerValue.TIMESTAMP, uId, uName, uAvatar, email)
            val ref = FirebaseDatabase.getInstance().getReference("User")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(user.uid)) {
                        ref.child(user.uid).setValue(data).addOnSuccessListener {
                            Log.d("addUserProfile", "success")
                        }.addOnFailureListener {
                            Log.d("addUserProfile", "failure")
                        }
                    } else Log.e("addUserProfile ", "Already have a profile" + user.uid)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("addUserProfile ", error.message)
                }

            })
        }
    }


    private fun checkValidation(): Boolean {
        val email = binding.edtEmail.text.toString()
        val pass = binding.edtPass.text.toString()
        val passConfirm = binding.edtPassConfirm.text.toString()
        if (email.isEmpty()) {
            binding.tilEmail.setError("Please enter your email")
            return false
        } else {
            binding.tilEmail.error = null
            binding.tilEmail.isErrorEnabled = false
        }

        if (!email.matches(emailValidation.toRegex())) {
            binding.tilEmail.setError("Not a valid email")
            return false
        } else {
            binding.tilEmail.error = null
            binding.tilEmail.isErrorEnabled = false
        }

        if (pass.isEmpty()) {
            binding.tilPass.setError("Please enter your password")
            return false
        } else {
            binding.tilPass.error = null
            binding.tilPass.isErrorEnabled = false
        }
        if (pass.length < 6) {
            binding.tilPass.setError("The password must be at least 6 characters")
            return false
        } else {
            binding.tilPass.error = null
            binding.tilPass.isErrorEnabled = false
        }

        if (passConfirm.isEmpty()) {
            binding.tilPassConfirm.setError("Please enter your confirm password")
            return false
        } else {
            binding.tilPassConfirm.error = null
            binding.tilPassConfirm.isErrorEnabled = false
        }

        if (passConfirm != pass) {
            binding.tilPassConfirm.setError("Password confirm doesn't match password")
            return false
        } else {
            binding.tilPassConfirm.error = null
            binding.tilPassConfirm.isErrorEnabled = false
        }

        return true
    }

    private fun Login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

}