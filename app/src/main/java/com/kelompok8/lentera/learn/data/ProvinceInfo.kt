package com.kelompok8.lentera.learn.data

data class ProvinceInfo(
    val name: String,
    val description: String,
    val imageName: String?,
)

interface ProvinceInfoSource {
    suspend fun getProvinceInfo(province: String): ProvinceInfo?
}
