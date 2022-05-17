package com.example.blog.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.blog.R
import com.example.blog.data.ReadProfile
import com.example.blog.data.UploadPost
import com.example.blog.databinding.FragmentCreatePostBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import gun0912.tedimagepicker.builder.TedImagePicker
import java.util.*
import kotlin.concurrent.timerTask


class CreatePostFragment : Fragment() {

    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var mAuth: FirebaseAuth
    private var filePath: Uri? = null
    private var uriPhoto: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        LoadUserData()

        binding.constraintLayout5.visibility = View.GONE

        binding.layoutContent.setOnClickListener { hideKeyboard() }

        binding.btnPickimage.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                TedImagePicker.with(requireContext()).start { uri ->
                    Glide.with(requireActivity()).load(uri).into(binding.imPhoto)
                    binding.constraintLayout5.visibility = View.VISIBLE
                    binding.btnPickimage.visibility = View.GONE
                    filePath = uri
                }
            }

        })

        binding.btnClear.setOnClickListener { clearImage() }
        binding.btnPost.setOnClickListener { uploadPost() }


    }

    private fun uploadPost() {
        val id = UUID.randomUUID().toString()
        if (filePath != null) {
            uploadImage(id)
        } else if (!binding.edtTitle.text.isEmpty()) {
            uploadData(id)
        } else {
            Snackbar.make(
                binding.root, "Please enter a title or choose an image",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun uploadImage(id: String) {
        val fStorage = FirebaseStorage.getInstance().getReference("Post/$id")
        fStorage.putFile(filePath!!).addOnSuccessListener {

            fStorage.downloadUrl.addOnSuccessListener {
                uriPhoto = it.toString()
                Log.d("dowloadUrlImage", "Dowload url image success")
                uploadData(id)
            }.addOnFailureListener {
                Log.e("dowloadUrlImage", "Dowload url image failure")
            }
        }.addOnFailureListener() {
            Log.e("uploadImage", "Upload image failure")

        }
    }

    private fun uploadData(id: String) {
        val data = UploadPost(
            id, "Post", null, mAuth.currentUser!!.uid,
            binding.edtTitle.text.toString(), uriPhoto, ServerValue.TIMESTAMP, ServerValue.TIMESTAMP
        )
        val fDatabase = FirebaseDatabase.getInstance().getReference("Post/$id")
        fDatabase.setValue(data).addOnSuccessListener {
            Log.d("uploadPost", "Upload post success")
            Snackbar.make(
                binding.root,
                "Successfully added new post",
                Snackbar.LENGTH_LONG
            ).show()
            Handler().postDelayed(timerTask {
                val viewPager = activity?.findViewById<ViewPager>(R.id.viewpager_main)
                viewPager!!.currentItem = 0
                restoreDefaultUI()
            }, 1500)
        }
    }

    private fun restoreDefaultUI() {
        binding.edtTitle.text.clear()
        binding.imPhoto.setImageResource(0)
        binding.constraintLayout5.visibility = View.GONE
        binding.btnPickimage.visibility = View.VISIBLE
        filePath = null
        uriPhoto = null
        hideKeyboard()
    }

    private fun LoadUserData() {
        val ref = FirebaseDatabase.getInstance().getReference("User")
        val profileList = ArrayList<ReadProfile>()
        ref.orderByChild("userId").equalTo(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (uSnapshot in snapshot.children) {
                            val data = uSnapshot.getValue(ReadProfile::class.java)
                            profileList.add(data!!)
                            Glide.with(this@CreatePostFragment).load(profileList[0].userAvatar)
                                .into(binding.imvAvatar)
                            binding.tvUserName.text = profileList[0].userName
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseError", error.message)
                }

            })
    }

    private fun clearImage() {
        binding.imPhoto.setImageResource(0)
        binding.constraintLayout5.visibility = View.GONE
        binding.btnPickimage.visibility = View.VISIBLE
        filePath = null
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }


}