package com.example.blog.data

data class UploadProfile(
    val dateCreate: MutableMap<String, String>? = null,
    val dateUpdate: MutableMap<String, String>? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val userEmail: String? = null,
    val userPhoneNumber: String? = null,
    val userBirthday: String? = null,
    val userAddress: String? = null
)
