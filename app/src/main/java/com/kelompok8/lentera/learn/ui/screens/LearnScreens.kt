package com.kelompok8.lentera.learn.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.core.model.Island

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LearnTopBar(title: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = { TextButton(onClick = onBack) { Text("Kembali") } },
    )
}

internal fun categoryEmoji(category: GlossaryCategory): String = when (category) {
    GlossaryCategory.PAKAIAN_ADAT -> "👘"
    GlossaryCategory.RUMAH_ADAT -> "🏠"
    GlossaryCategory.MAKANAN -> "🍲"
    GlossaryCategory.TARIAN -> "💃"
    GlossaryCategory.LAGU -> "🎵"
}

@Composable
internal fun ItemImage(item: GlossaryItem, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val resId = remember(item.imageName) {
        item.imageName?.let { context.resources.getIdentifier(it, "drawable", context.packageName) } ?: 0
    }
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(categoryEmoji(item.category), style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun SelectMaterialScreen(
    provincesByIsland: Map<Island, List<String>>,
    onProvinceClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(provincesByIsland, query) {
        provincesByIsland
            .mapValues { (_, list) -> list.filter { it.contains(query.trim(), ignoreCase = true) } }
            .filterValues { it.isNotEmpty() }
    }

    Scaffold(topBar = { LearnTopBar("Pilih Materi", onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "search") {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Cari provinsi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            filtered.forEach { (island, provinces) ->
                item(key = "island_${island.name}") {
                    Text(
                        text = "Pulau ${island.displayName}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                items(provinces, key = { "${island.name}_$it" }) { province ->
                    ElevatedCard(
                        onClick = { onProvinceClick(province) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = province,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
            if (filtered.isEmpty()) {
                item(key = "empty") { Text("Provinsi tidak ditemukan") }
            }
        }
    }
}

@Composable
fun TableOfContentsScreen(
    province: String,
    onCategoryClick: (GlossaryCategory) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(topBar = { LearnTopBar(province, onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "title") {
                Text("Daftar Isi", style = MaterialTheme.typography.headlineSmall)
            }
            items(GlossaryCategory.values().toList(), key = { it.name }) { category ->
                ElevatedCard(
                    onClick = { onCategoryClick(category) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(categoryEmoji(category), style = MaterialTheme.typography.headlineMedium)
                        Text(category.displayName, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun SubMaterialScreen(
    province: String,
    category: GlossaryCategory,
    items: List<GlossaryItem>?,
    onItemClick: (GlossaryItem) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(topBar = { LearnTopBar("${category.displayName} $province", onBack) }) { padding ->
        if (items == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items, key = { it.id }) { item ->
                    Card(
                        onClick = { onItemClick(item) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ItemImage(
                                item = item,
                                modifier = Modifier.size(88.dp).clip(MaterialTheme.shapes.medium),
                            )
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialDetailScreen(
    item: GlossaryItem,
    onBack: () -> Unit,
) {
    Scaffold(topBar = { LearnTopBar(item.name, onBack) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ItemImage(
                item = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.large),
            )
            Text(item.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                "${item.category.displayName}  •  ${item.province}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(item.description, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
