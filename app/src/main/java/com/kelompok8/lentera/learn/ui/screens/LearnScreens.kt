package com.kelompok8.lentera.learn.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.core.model.GlossaryCategory
import com.kelompok8.lentera.core.model.GlossaryItem
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.model.User
import com.kelompok8.lentera.learn.data.ProvinceInfo
import com.kelompok8.lentera.learn.ui.CardBrush
import com.kelompok8.lentera.learn.ui.LearnBackground
import com.kelompok8.lentera.learn.ui.LearnColors
import com.kelompok8.lentera.learn.ui.LearnFonts
import com.kelompok8.lentera.learn.ui.LearnTopBar
import com.kelompok8.lentera.learn.ui.categoryEmoji
import com.kelompok8.lentera.learn.ui.rememberDrawableId

private val ProvinceRowHeight = 42.dp
private const val VisibleProvinceRows = 6

private fun islandTitle(island: Island): String = when (island) {
    Island.SUMATRA -> "Pulau Sumatera"
    else -> "Pulau ${island.displayName}"
}

@Composable
fun IslandListScreen(
    provincesByIsland: Map<Island, List<String>>,
    user: User?,
    onProvinceClick: (String) -> Unit,
) {
    var expandedIsland by rememberSaveable { mutableStateOf<String?>(null) }

    LearnBackground {
        Column(Modifier.fillMaxSize()) {
            LearnTopBar(title = "Belajar", user = user)
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "heading") {
                    Text(
                        text = "Pelajari Budaya,\nTambah Wawasan",
                        color = LearnColors.Neon,
                        fontFamily = LearnFonts.Heading,
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                items(provincesByIsland.entries.toList(), key = { it.key.name }) { (island, provinces) ->
                    IslandCard(
                        island = island,
                        provinces = provinces,
                        expanded = expandedIsland == island.name,
                        onToggle = {
                            expandedIsland = if (expandedIsland == island.name) null else island.name
                        },
                        onProvinceClick = onProvinceClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun IslandCard(
    island: Island,
    provinces: List<String>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onProvinceClick: (String) -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, LearnColors.CardBorder, shape),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(CardBrush)
                .clickable(onClick = onToggle)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Column(Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = islandTitle(island),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                LearnChip()
            }
            IslandImage(
                island = island,
                modifier = Modifier.align(Alignment.CenterEnd).size(width = 78.dp, height = 52.dp),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(LearnColors.ProvinceList)
                    .heightIn(max = ProvinceRowHeight * VisibleProvinceRows)
                    .verticalScroll(rememberScrollState()),
            ) {
                provinces.forEach { province ->
                    ProvinceRow(province) { onProvinceClick(province) }
                }
            }
        }
    }
}

@Composable
private fun LearnChip() {
    Row(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LearnColors.Chip)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Belajar",
            color = LearnColors.ChipText,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(4.dp))
        Text(text = "📖", fontSize = 10.sp)
    }
}

@Composable
private fun IslandImage(island: Island, modifier: Modifier = Modifier) {
    val resId = rememberDrawableId("island_${island.name.lowercase()}")
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = island.displayName,
            contentScale = ContentScale.Fit,
            modifier = modifier,
        )
    } else {
        Box(
            modifier
                .clip(RoundedCornerShape(50))
                .background(LearnColors.Green.copy(alpha = 0.6f)),
        )
    }
}

@Composable
private fun ProvinceRow(province: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(ProvinceRowHeight - 1.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = province.uppercase(),
                color = Color.White,
                fontFamily = LearnFonts.Mono,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier.size(22.dp).clip(CircleShape).background(LearnColors.Green),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "›",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(LearnColors.Divider))
    }
}

@Composable
fun ProvinceScreen(
    province: String,
    info: ProvinceInfo?,
    previews: Map<GlossaryCategory, List<GlossaryItem>>?,
    user: User?,
    onCategoryClick: (GlossaryCategory) -> Unit,
) {
    LearnBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(230.dp)) {
                ProvinceBanner(info?.imageName, Modifier.matchParentSize())
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x66000000), Color.Transparent, Color(0xCC000000)),
                            ),
                        ),
                )
                LearnTopBar(
                    title = null,
                    user = user,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "Provinsi $province",
                        color = LearnColors.Neon,
                        fontFamily = LearnFonts.Heading,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    val description = info?.description.orEmpty()
                    if (description.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = description,
                            color = Color.White,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.fillMaxWidth(0.8f),
                        )
                    }
                }
            }
            Text(
                text = "Kategori Budaya",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 12.dp),
            )
            Column(
                Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GlossaryCategory.entries.forEach { category ->
                    CategoryRow(
                        category = category,
                        subtitle = previews?.get(category)?.firstOrNull()?.name,
                        onClick = { onCategoryClick(category) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProvinceBanner(imageName: String?, modifier: Modifier = Modifier) {
    val resId = rememberDrawableId(imageName)
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Box(
            modifier.background(
                Brush.verticalGradient(listOf(Color(0xFF2F6B3A), LearnColors.Background)),
            ),
        )
    }
}

@Composable
private fun CategoryRow(
    category: GlossaryCategory,
    subtitle: String?,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CardBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = category.displayName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Text(
            text = "›",
            color = LearnColors.Chevron,
            fontSize = 30.sp,
        )
    }
}

@Composable
fun CategoryDetailScreen(
    province: String,
    category: GlossaryCategory,
    items: List<GlossaryItem>?,
    user: User?,
    onBack: () -> Unit,
) {
    LearnBackground {
        Column(Modifier.fillMaxSize()) {
            LearnTopBar(title = province, user = user)
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Text(
                        text = "←",
                        color = Color.White,
                        fontSize = 24.sp,
                    )
                }
                Text(
                    text = category.displayName,
                    color = LearnColors.Neon,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )
            }
            if (items == null) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LearnColors.Neon)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(items, key = { it.id }) { item -> ItemCard(item) }
                }
            }
        }
    }
}

@Composable
private fun ItemCard(item: GlossaryItem) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(LearnColors.DetailCard)
            .border(1.dp, LearnColors.DetailBorder, shape)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ItemImage(
            item = item,
            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(6.dp)),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = item.name,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = item.description,
            color = Color.White,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ItemImage(item: GlossaryItem, modifier: Modifier = Modifier) {
    val resId = rememberDrawableId(item.imageName)
    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = item.name,
            contentScale = ContentScale.Crop,
            modifier = modifier,
        )
    } else {
        Box(
            modifier.background(
                Brush.verticalGradient(listOf(Color(0xFF1E2A78), Color(0xFF0B1344))),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = categoryEmoji(item.category), fontSize = 36.sp)
        }
    }
}