package com.example.blog.data

data class UploadComment(
    val idComment: String? = null,
    val idPost: String? = null,
    val idUser: String? = null,
    val comment: String? = null,
    val dateCreate: MutableMap<String, String>? = null,
    val dateUpdate: MutableMap<String, String>? = null
)