package com.kelompok8.lentera.core.data

import com.kelompok8.lentera.core.data.remote.UserRemoteDataSource
import com.kelompok8.lentera.core.data.remote.dto.toLeaderboardEntry
import com.kelompok8.lentera.core.model.LeaderboardFilter

class LeaderboardRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource = UserRemoteDataSource()
) : LeaderboardRepository {

    override suspend fun getOverallLeaderboard(
        page: Int,
        pageSize: Int,
        filter: LeaderboardFilter
    ): LeaderboardResult {
        val skip = (page - 1) * pageSize
        val response = remoteDataSource.getUsers(limit = pageSize, skip = skip)
        val entries = response.users.mapIndexed { index, dto ->
            val rank = skip + index + 1
            dto.toLeaderboardEntry(assignedRank = rank)
        }
        return LeaderboardResult(
            entries = entries,
            totalCount = if (response.total > 0) response.total else entries.size
        )
    }
}
