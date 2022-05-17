package com.example.blog.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blog.LoginActivity
import com.example.blog.R
import com.example.blog.adapter.RCVHomeAdapter
import com.example.blog.data.ReadPost
import com.example.blog.data.ReadProfile
import com.example.blog.databinding.FragmentHomeBinding
import com.example.blog.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.StorageReference
import io.grpc.Context
import java.util.*
import kotlin.concurrent.timerTask

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        getUserData()
        binding.btnLogout.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                buildDialog()!!.show()
            }
        })

        // recycleview post
        val activity = activity as android.content.Context
        binding.rcvProfile.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        (binding.rcvProfile.layoutManager as LinearLayoutManager).reverseLayout = true
        (binding.rcvProfile.layoutManager as LinearLayoutManager).stackFromEnd = true
        binding.rcvProfile.setHasFixedSize(true)
        val fDatabase = FirebaseDatabase.getInstance().getReference("Post")
                fDatabase.orderByChild("idUser").equalTo(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val postList = ArrayList<ReadPost>()
                        if (snapshot.exists()) {
                            for (pSnapshot in snapshot.children) {
                                val data = pSnapshot.getValue(ReadPost::class.java)
                                postList.add(data!!)
                                binding.tvPostNumber.setText(postList.size.toString()+" Post")
                            }
                        }
                        binding.rcvProfile.adapter = RCVHomeAdapter(activity, postList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DataPost", error.message)
            }

        })

    }
    //logout

    private fun buildDialog(): AlertDialog.Builder? {
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setTitle("Logout")
        builder.setMessage("Do you want to logout?")
        builder.setPositiveButton("Logout") { dialog, which ->
            mAuth.signOut()
            val intent = Intent(activity,LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        builder.setNeutralButton("Cancel") { dialog, which -> }
        return builder
    }


    private fun getUserData(){
        val databaseReference = FirebaseDatabase.getInstance().getReference("User")
        val profileList = ArrayList<ReadProfile>()
        databaseReference.orderByChild("userId").equalTo(mAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                if (snapshot.exists()){
                    for (uSnapshot in snapshot.children) {
                        val data = uSnapshot.getValue(ReadProfile::class.java)
                        profileList.add(data!!)
                        binding.tvUserName.setText(profileList[0].userName)
                        Glide.with(binding.root).load(profileList[0].userAvatar)
                            .into(binding.imvAvatar)
                        binding.tvFullName.setText(profileList[0].userName)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", error.message)
            }

        })
    }


}