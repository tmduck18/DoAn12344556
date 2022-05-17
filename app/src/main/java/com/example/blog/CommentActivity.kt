package com.example.blog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blog.adapter.RCVCommentAdapter
import com.example.blog.data.ReadComment
import com.example.blog.data.ReadPost
import com.example.blog.data.ReadProfile
import com.example.blog.data.UploadComment
import com.example.blog.databinding.ActivityCommentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.grpc.Server
import java.util.*

class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var mAuth: FirebaseAuth
    private var idPost: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.btnBack.setOnClickListener { finish() }

        val ref = FirebaseDatabase.getInstance().getReference("User")
        val profileList = ArrayList<ReadProfile>()
        ref.orderByChild("userId").equalTo(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (uSnapshot in snapshot.children) {
                            val data = uSnapshot.getValue(ReadProfile::class.java)
                            profileList.add(data!!)
                            Glide.with(binding.root).load(profileList[0].userAvatar)
                                .into(binding.imAvatar)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseError", error.message)
                }

            })


        idPost = intent.getStringExtra("idPost")
        binding.btnPostComment.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    if (!binding.tvComment.text.isEmpty()) {
                        val id = UUID.randomUUID().toString()
                        val data = UploadComment(
                            id,
                            idPost,
                            mAuth.currentUser!!.uid,
                            binding.tvComment.text.toString(),
                            ServerValue.TIMESTAMP,
                            ServerValue.TIMESTAMP
                        )
                        val fDatabase =
                            FirebaseDatabase.getInstance().getReference("Comment").child(idPost!!)
                        fDatabase.child(id).setValue(data).addOnSuccessListener {
                            Snackbar.make(
                                binding.root,
                                "Add new comment success",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            hideKeyboard()
                            binding.tvComment.text.clear()
                        }.addOnFailureListener {
                            Snackbar.make(
                                binding.root,
                                "Add new comment failed",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }else Snackbar.make(
                        binding.root,
                        "Please enter comment",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })


        binding.rcvComment.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        (binding.rcvComment.layoutManager as LinearLayoutManager).reverseLayout = true
        (binding.rcvComment.layoutManager as LinearLayoutManager).stackFromEnd = true
        binding.rcvComment.setHasFixedSize(true)
        val fDatabase = FirebaseDatabase.getInstance().getReference("Comment")
        fDatabase.child(idPost!!).orderByChild("dateCreate").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentList = ArrayList<ReadComment>()
                if (snapshot.exists()) {
                    for (pSnapshot in snapshot.children) {
                        val data = pSnapshot.getValue(ReadComment::class.java)
                        commentList.add(data!!)
                    }
                }
                binding.rcvComment.adapter = RCVCommentAdapter(this@CommentActivity, commentList)
                Log.d("commentList", commentList.size.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database", error.message)
            }

        })

    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}