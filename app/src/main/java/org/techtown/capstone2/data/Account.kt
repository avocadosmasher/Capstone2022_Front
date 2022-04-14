package org.techtown.capstone2.data

import androidx.annotation.DrawableRes

data class Account(
    val id: Long,
    val uid: Long,
    val firstName: String,
    val lastName: String,
    @DrawableRes val avatar: Int,
){
    val fullName: String = "$firstName $lastName"
}
