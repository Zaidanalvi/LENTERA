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
) : GlossaryRepository {

    constructor(context: Context, assetPath: String = DEFAULT_ASSET_PATH) : this(
        readJson = {
            context.applicationContext.assets.open(assetPath)
                .bufferedReader()
                .use { it.readText() }
        },
    )

    private val mutex = Mutex()
    private var cache: List<GlossaryItem>? = null

    private suspend fun items(): List<GlossaryItem> = cache ?: mutex.withLock {
        cache ?: withContext(Dispatchers.IO) { parse(readJson()) }.also { cache = it }
    }

    override suspend fun getProvinces(island: Island): List<String> =
        items().filter { it.island == island }.map { it.province }.distinct()

    override suspend fun getItems(province: String, category: GlossaryCategory): List<GlossaryItem> =
        items().filter { it.province == province && it.category == category }

    private fun parse(json: String): List<GlossaryItem> {
        val array = JSONObject(json).getJSONArray("items")
        return List(array.length()) { i ->
            val o = array.getJSONObject(i)
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
    }

    companion object {
        const val DEFAULT_ASSET_PATH = "glossary.json"
    }
}
