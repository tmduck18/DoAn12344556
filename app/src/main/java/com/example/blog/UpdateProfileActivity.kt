package com.example.blog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blog.databinding.ActivityUpdateProfileBinding
import com.google.firebase.database.DatabaseReference

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnUpdateProfile.setOnClickListener {

            val userName = binding.editName.text.toString()
            val userPhone = binding.editSdt.text.toString()
            val userAddress = binding.editAddress.text.toString()
//            val userImg = binding.btnUpdateImg.

        }


    }

}