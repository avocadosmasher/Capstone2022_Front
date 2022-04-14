package org.techtown.capstone2.data


data class Post(
    val id:Long,
    val user: Account,
    val title:String="",
    val body:String="",
){
    val hasBody:Boolean = body.isNotBlank()
}
