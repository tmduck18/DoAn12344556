package com.example.blog.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blog.CommentActivity
import com.example.blog.DetailsPostActivity
import com.example.blog.R
import com.example.blog.data.ReadPost
import com.example.blog.data.ReadProfile
import com.example.blog.databinding.ItemPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_post.view.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class RCVHomeAdapter(val activity: Context, val postList: ArrayList<ReadPost>) :
    RecyclerView.Adapter<RCVHomeAdapter.ViewHolder>() {

    private lateinit var mAuth: FirebaseAuth

    class ViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        mAuth = FirebaseAuth.getInstance()

        // đọc dữ liệu từ post List
        val idPost = postList[position].idPost
        val typePost = postList[position].typePost
        val idShare = postList[position].idShare
        val idUser = postList[position].idUser
        val title = postList[position].title
        val photo = postList[position].photo
        val dateCreate = postList[position].dateCreate
        val dateUpdate = postList[position].dateUpdate

        LoadUser(holder.binding.root, idUser, holder.binding.imvAvatar, holder.binding.tvName)

        // chuyển đổi và hiển thị dateCreate
        LoadDate(dateCreate, holder.binding.tvDateCreate)

        LoadLikeNumber(idPost, holder.binding.like, holder.binding.tvLikeNumber)

        LoadCommentNumber(idPost, holder.binding.tvCommentNumber)


        holder.binding.detailsLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(holder.binding.root.context, DetailsPostActivity::class.java)
                intent.putExtra("idPost", idPost)
                holder.binding.root.context.startActivity(intent)
            }
        })

        // xét sự kiện cho btn like
        holder.binding.btnLike.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var like = true
                val fDatabase = FirebaseDatabase.getInstance().getReference("Like")
                fDatabase.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (like == true) {
                            if (snapshot.child(idPost!!).hasChild(mAuth.currentUser?.uid!!)) {
                                fDatabase.child(idPost).child(mAuth.currentUser!!.uid).removeValue()
                                like = false
                            } else {
                                fDatabase.child(idPost).child(mAuth.currentUser!!.uid)
                                    .setValue(true)
                                like = false
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("DatabaseError", error.message)
                    }

                })
            }

        })

        holder.binding.btnComment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(holder.binding.root.context, CommentActivity::class.java)
                intent.putExtra("idPost", idPost)
                holder.binding.root.context.startActivity(intent)
            }

        })

        if (title == null) {
            holder.binding.tvTitle.visibility = View.GONE
        }

        if (typePost == "Post") {
            holder.binding.layoutShare.visibility = View.GONE
            holder.binding.tvTitle.tvTitle.text = title
            Glide.with(holder.binding.root).load(photo).into(holder.binding.imPhoto)

        } else {
            holder.binding.tvTypePost.text = "Shared a post"
            holder.binding.tvTitle.text = title

        }


    }

    private fun LoadUser(
        context: CardView,
        idUser: String?,
        imAvatar: CircleImageView,
        tvName: TextView
    ) {
        val ref = FirebaseDatabase.getInstance().getReference("User")
        val profileList = ArrayList<ReadProfile>()
        ref.orderByChild("userId").equalTo(idUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (uSnapshot in snapshot.children) {
                            val data = uSnapshot.getValue(ReadProfile::class.java)
                            profileList.add(data!!)
                            Glide.with(context).load(profileList[0].userAvatar)
                                .into(imAvatar)
                            tvName.text = profileList[0].userName
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DatabaseError", error.message)
                }

            })
    }


    private fun LoadDate(dateCreate: Long?, tvDateCreate: TextView) {
        val format = SimpleDateFormat("dd/MM/yyyy")
        tvDateCreate.text = format.format(dateCreate)
    }

    private fun LoadLikeNumber(idPost: String?, like: ImageView, tvLikeNumber: TextView) {
        val fDatabase = FirebaseDatabase.getInstance().getReference("Like")
        fDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(idPost!!).hasChild(mAuth.currentUser!!.uid)) {
                    tvLikeNumber.text = snapshot.child(idPost).childrenCount.toString() + " Like"
                    like.setImageResource(R.drawable.ic_like_red)
                } else {
                    tvLikeNumber.text = snapshot.child(idPost).childrenCount.toString() + " Like"
                    like.setImageResource(R.drawable.ic_like)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", error.message)
            }
        })
    }

    private fun LoadCommentNumber(idPost: String?, tvCommentNumber: TextView) {
        val fDatabase = FirebaseDatabase.getInstance().getReference("Comment")
        fDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvCommentNumber.text =
                    snapshot.child(idPost!!).childrenCount.toString() + " Comment"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", error.message)
            }

        })
    }



}

