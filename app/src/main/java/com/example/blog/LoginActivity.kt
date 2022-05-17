package com.example.blog

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.example.blog.data.UploadProfile
import com.example.blog.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    companion object {
        private const val RC_SIGN_IN = 120
        private const val TAG = "Google Sign In "
    }

    var emailValidation = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener { Sinup() }
        binding.btnLogin.setOnClickListener { Login() }
        binding.constraint.setOnClickListener { hideKeyboard() }


    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    // chuyển sang màn hình Signup
    private fun Sinup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun Login() {
        if (validation()) {
            mAuth.signInWithEmailAndPassword(
                binding.edtEmail.text.toString(),
                binding.edtPass.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithEmail:success")
                        val user = mAuth.currentUser
                        Log.d("tag", user?.uid.toString())
                        UpdateUI(user)
                    } else {
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Snackbar.make(
                            binding.root,
                            "Email or password is incorrect. Please check again",
                            Snackbar.LENGTH_LONG
                        )
                            .show()

                    }
                }
        } else {
            Log.d("checkValidation", "error")

        }
    }

    //check validation
    private fun validation(): Boolean {
        val email = binding.edtEmail.text.toString()
        val pass = binding.edtPass.text.toString()

        if (email.isEmpty()) {
            binding.tilEmail.error = "Please enter your email"
            return false
        } else {
            binding.tilEmail.error = null
            binding.tilEmail.isErrorEnabled = false
        }

        if (!email.matches(emailValidation.toRegex())) {
            binding.tilEmail.error = "Not a valid email"
            return false
        } else {
            binding.tilEmail.error = null
            binding.tilEmail.isErrorEnabled = false
        }

        if (pass.isEmpty()) {
            binding.tilPass.error = "Please enter your password"
            return false
        } else {
            binding.tilPass.isErrorEnabled = false
            binding.tilPass.error = null
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        CheckNetworkConnection()
        val user = mAuth.currentUser
        if (user != null) UpdateUI(user)
    }

    private fun CheckNetworkConnection() {
        if (!isConnected()) Handler().postDelayed({ buildDialog()?.show() }, 1300)
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
            val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            return mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
        }
        return false
    }

    private fun buildDialog(): AlertDialog.Builder? {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("No Internet Connection")
        builder.setPositiveButton("Ok") { dialog, which ->
            startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
        return builder
    }

    private fun UpdateUI(user: Any?) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }




}


