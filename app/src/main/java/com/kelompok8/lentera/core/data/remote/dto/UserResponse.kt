package com.kelompok8.lentera.core.data.remote.dto

data class UserResponse(
    val users: List<UserDto> = emptyList(),
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)
