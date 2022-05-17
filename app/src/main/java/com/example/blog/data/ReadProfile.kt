package com.example.blog.data

data class ReadProfile(
    val dateCreate: Long? = null,
    val dateUpdate: Long? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val userEmail: String? = null
)
