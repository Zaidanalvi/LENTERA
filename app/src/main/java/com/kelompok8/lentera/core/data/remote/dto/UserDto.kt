package com.kelompok8.lentera.core.data.remote.dto

import com.kelompok8.lentera.core.model.LeaderboardEntry

data class UserDto(
    val id: Int? = null,
    val uid: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val name: String? = null,
    val email: String? = null,
    val image: String? = null,
    val score: Int? = null,
    val totalScore: Int? = null,
    val rank: Int? = null
)

fun UserDto.toLeaderboardEntry(assignedRank: Int): LeaderboardEntry {
    val displayName = name ?: listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { username ?: "Pemain" }
    val points = totalScore ?: score ?: (1000 - (assignedRank * 30)).coerceAtLeast(100)
    val userUid = uid ?: id?.toString() ?: assignedRank.toString()
    return LeaderboardEntry(
        uid = userUid,
        rank = rank ?: assignedRank,
        name = displayName,
        totalScore = points,
        avatarId = (id ?: assignedRank) % 4
    )
}
