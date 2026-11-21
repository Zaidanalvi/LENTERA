package com.kelompok8.lentera.core.data.remote

import com.kelompok8.lentera.core.data.remote.dto.UserResponse

class UserRemoteDataSource(
    private val apiService: UserApiService = RetrofitClient.apiService
) {
    suspend fun getUsers(limit: Int = 10, skip: Int = 0): UserResponse {
        return try {
            apiService.getUsers(limit = limit, skip = skip)
        } catch (e: Exception) {
            UserResponse()
        }
    }
}
