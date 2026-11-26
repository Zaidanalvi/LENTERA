package com.kelompok8.lentera.learn.data

import android.content.Context
import com.kelompok8.lentera.core.data.GlossaryRepository
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.core.model.Island
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GlossaryRepositoryImpl internal constructor(
    private val readJson: () -> String,
) : GlossaryRepository, ProvinceInfoSource {

    constructor(context: Context, assetPath: String = DEFAULT_ASSET_PATH) : this(
        readJson = {
            context.applicationContext.assets.open(assetPath)
                .bufferedReader()
                .use { it.readText() }
        },
    )

    private class Loaded(
        val items: List<GlossaryItem>,
        val provinces: Map<String, ProvinceInfo>,
    )

    private val mutex = Mutex()
    private var cache: Loaded? = null

    private suspend fun loaded(): Loaded = cache ?: mutex.withLock {
        cache ?: withContext(Dispatchers.IO) { parse(readJson()) }.also { cache = it }
    }

    override suspend fun getProvinces(island: Island): List<String> =
        loaded().items.filter { it.island == island }.map { it.province }.distinct()

    override suspend fun getItems(province: String, category: GlossaryCategory): List<GlossaryItem> =
        loaded().items.filter { it.province == province && it.category == category }

    override suspend fun getProvinceInfo(province: String): ProvinceInfo? =
        loaded().provinces[province]

    private fun parse(json: String): Loaded {
        val root = JSONObject(json)

        val itemArray = root.getJSONArray("items")
        val items = List(itemArray.length()) { i ->
            val o = itemArray.getJSONObject(i)
            GlossaryItem(
                id = o.getString("id"),
                province = o.getString("province"),
                island = Island.valueOf(o.getString("island")),
                category = GlossaryCategory.valueOf(o.getString("category")),
                name = o.getString("name"),
                description = o.getString("description"),
                imageName = o.optString("imageName").ifEmpty { null },
            )
        }

        val provinceArray = root.optJSONArray("provinces")
        val provinces = if (provinceArray == null) {
            emptyMap()
        } else {
            (0 until provinceArray.length()).associate { i ->
                val o = provinceArray.getJSONObject(i)
                val name = o.getString("name")
                name to ProvinceInfo(
                    name = name,
                    description = o.optString("description"),
                    imageName = o.optString("imageName").ifEmpty { null },
                )
            }
        }

        return Loaded(items, provinces)
    }

    companion object {
        const val DEFAULT_ASSET_PATH = "glossary.json"
    }
}
