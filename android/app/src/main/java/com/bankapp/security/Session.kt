package com.bankapp.session

data class Session(
    val sessionId: String,
    val userId: String,
    val userName: String,
    val phone: String,
    val balance: String,
    val expiration: Long
)