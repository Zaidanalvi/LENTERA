package com.kelompok8.lentera.core.data.remote

import com.kelompok8.lentera.core.data.remote.dto.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {
    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): UserResponse
}
