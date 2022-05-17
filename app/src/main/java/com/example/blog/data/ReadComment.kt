package com.example.blog.data

data class ReadComment(
    val idComment: String? = null,
    val idPost: String? = null,
    val idUser: String? = null,
    val comment: String? = null,
    val dateCreate: Long? = null,
    val dateUpdate: Long? = null
)