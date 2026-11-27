package com.kelompok8.lentera.learn.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.ColorFilter
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
import com.kelompok8.lentera.learn.ui.LearnAudioController
import com.kelompok8.lentera.learn.ui.LearnBackground
import com.kelompok8.lentera.learn.ui.LearnFonts
import com.kelompok8.lentera.learn.ui.LearnTopBar
import com.kelompok8.lentera.learn.ui.SongPlayerBar
import com.kelompok8.lentera.learn.ui.categoryEmoji
import com.kelompok8.lentera.learn.ui.rememberDrawableId
import com.kelompok8.lentera.learn.ui.rememberLearnAudioController

// Palet warna diambil langsung dari desain Figma
private object Figma {
    val Neon = Color(0xFFDDFF00)
    val CardLight = Color(0xFF7F1BCB)
    val CardDark = Color(0xFF5F139A)
    val CardEdgeTop = Color(0x80D9A8FF)
    val CardEdgeBottom = Color(0x1AFFFFFF)
    val ChipTop = Color(0xFFA358DB)
    val ChipBottom = Color(0xFF8B40C6)
    val ChipShadow = Color(0xFF430E6B)
    val IslandSide = Color(0xFF035606)
    val DropTop = Color(0xFF51167D)
    val DropBottom = Color(0xFF340B53)
    val Row = Color(0x14FFFFFF)
    val RowHighlight = Color(0x1FFFFFFF)
    val RowShadow = Color(0x26000000)
    val ProvinceText = Color(0xFFD9D6FF)
    val Green = Color(0xFF1BAB14)
    val PageDark = Color(0xFF050938)
    val Subtitle = Color(0xFFCFA8EE)
    val Chevron = Color(0xFFB23BF0)
    val DetailCard = Color(0xFF101B39)
    val DetailBorder = Color(0xFF2B3550)
}

private val CardGradient = Brush.linearGradient(listOf(Figma.CardLight, Figma.CardDark))
private val CardEdge = Brush.verticalGradient(listOf(Figma.CardEdgeTop, Figma.CardEdgeBottom))

private const val MinImageRatio = 0.9f
private const val MaxImageRatio = 2.1f

private val ProvinceRowHeight = 46.dp
private val ProvinceRowGap = 15.dp
private const val VisibleProvinceRows = 6

// Hanya tiga pulau, urutan provinsi mengikuti daftar gambar
private val IslandOrder = listOf("sumatra", "jawa", "kalimantan")

private val ProvinceOrder = listOf(
    "Aceh", "Sumatera Utara", "Sumatera Barat", "Riau", "Kepulauan Riau", "Jambi",
    "Sumatera Selatan", "Kepulauan Bangka Belitung", "Bengkulu", "Lampung",
    "DKI Jakarta", "Jawa Barat", "Banten", "Jawa Tengah", "DI Yogyakarta", "Jawa Timur",
    "Kalimantan Barat", "Kalimantan Tengah", "Kalimantan Selatan", "Kalimantan Timur",
    "Kalimantan Utara",
)

private fun islandKey(island: Island): String? {
    val name = island.displayName.lowercase()
    return when {
        "sumat" in name -> "sumatra"
        "jaw" in name -> "jawa"
        "kalimantan" in name -> "kalimantan"
        else -> null
    }
}

private fun islandTitle(island: Island): String = when (islandKey(island)) {
    "sumatra" -> "Pulau Sumatera"
    "jawa" -> "Pulau Jawa"
    else -> "Pulau Kalimantan"
}

@Composable
fun IslandListScreen(
    provincesByIsland: Map<Island, List<String>>,
    user: User?,
    onProvinceClick: (String) -> Unit,
) {
    var expandedIsland by rememberSaveable { mutableStateOf<String?>(null) }
    val islands = provincesByIsland.entries
        .filter { islandKey(it.key) != null }
        .sortedBy { IslandOrder.indexOf(islandKey(it.key)) }
    // Seperti Figma: saat satu pulau dibuka, pulau lain disembunyikan
    val shownIslands = islands.filter { expandedIsland == null || it.key.name == expandedIsland }

    BackHandler(enabled = expandedIsland != null) { expandedIsland = null }

    LearnBackground {
        Column(Modifier.fillMaxSize()) {
            LearnTopBar(title = "Belajar", user = user)
            LazyColumn(
                contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item(key = "heading") {
                    Text(
                        text = "Pelajari Budaya,\nTambah Wawasan",
                        color = Figma.Neon,
                        fontFamily = LearnFonts.Heading,
                        fontSize = 26.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 14.dp),
                    )
                }
                if (expandedIsland != null) {
                    item(key = "back_all_islands") {
                        LearnBackPill(
                            text = "Semua Pulau",
                            onClick = { expandedIsland = null },
                        )
                    }
                }
                items(shownIslands, key = { it.key.name }) { (island, provinces) ->
                    IslandCard(
                        island = island,
                        provinces = provinces.sortedBy { name ->
                            ProvinceOrder.indexOf(name).let { if (it < 0) Int.MAX_VALUE else it }
                        },
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
    val shape = RoundedCornerShape(12.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Figma.DropTop, Figma.DropBottom))),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(78.dp)
                .clip(shape)
                .background(CardGradient)
                .border(1.dp, CardEdge, shape)
                .clickable(onClick = onToggle),
        ) {
            Column(Modifier.align(Alignment.CenterStart).padding(start = 10.dp)) {
                Text(
                    text = islandTitle(island),
                    color = Color.White,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                LearnChip()
            }
            IslandImage(
                island = island,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp)
                    .size(width = 99.dp, height = 66.dp),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 28.dp)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(
                            minOf(provinces.size, VisibleProvinceRows).let { rows ->
                                ProvinceRowHeight * rows + ProvinceRowGap * maxOf(rows - 1, 0)
                            },
                        )
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(ProvinceRowGap),
                ) {
                    provinces.forEach { province ->
                        ProvinceRow(province) { onProvinceClick(province) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LearnChip() {
    val shape = RoundedCornerShape(4.dp)
    Box(
        Modifier
            .size(width = 82.dp, height = 24.dp)
            .background(Figma.ChipShadow, shape),
    ) {
        Row(
            Modifier
                .size(width = 82.dp, height = 22.dp)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(Figma.ChipTop, Figma.ChipBottom)))
                .border(1.dp, Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color.Transparent)), shape)
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Belajar", color = Color.White, fontFamily = LearnFonts.Heading, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            BookIcon(Modifier.padding(end = 3.dp).size(width = 16.dp, height = 11.dp))
        }
    }
}

@Composable
private fun BookIcon(modifier: Modifier) {
    Canvas(modifier) {
        val page = Size(size.width * 0.45f, size.height)
        drawRoundRect(Color.White, size = page)
        drawRoundRect(Color.White, topLeft = Offset(size.width * 0.55f, 0f), size = page)
    }
}

@Composable
private fun IslandImage(island: Island, modifier: Modifier = Modifier) {
    val resId = rememberDrawableId("island_${islandKey(island)}")
    if (resId != 0) {
        val painter = painterResource(resId)
        Box(modifier) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Figma.IslandSide),
                modifier = Modifier.matchParentSize().offset(y = 4.dp),
            )
            Image(
                painter = painter,
                contentDescription = island.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize(),
            )
        }
    } else {
        Box(
            modifier
                .clip(RoundedCornerShape(50))
                .background(Figma.Green.copy(alpha = 0.6f)),
        )
    }
}

@Composable
private fun ProvinceRow(province: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(ProvinceRowHeight)
            .background(Figma.Row)
            .clickable(onClick = onClick),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Figma.RowHighlight))
        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 15.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = province.uppercase(),
                color = Figma.ProvinceText,
                fontFamily = LearnFonts.Mono,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier.size(26.dp).clip(CircleShape).background(Figma.Green),
                contentAlignment = Alignment.Center,
            ) {
                Chevron(Color.White, 3f, Modifier.size(width = 6.dp, height = 11.dp))
            }
        }
        Box(Modifier.fillMaxWidth().height(3.dp).background(Figma.RowShadow))
    }
}

@Composable
private fun Chevron(color: Color, strokeDp: Float, modifier: Modifier) {
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, size.height / 2f)
            lineTo(0f, size.height)
        }
        drawPath(
            path,
            color,
            style = Stroke(width = strokeDp.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
fun ProvinceScreen(
    province: String,
    info: ProvinceInfo?,
    previews: Map<GlossaryCategory, List<GlossaryItem>>?,
    user: User?,
    onCategoryClick: (GlossaryCategory) -> Unit,
    onBack: () -> Unit,
) {
    LearnBackground {
        Column(
            Modifier
                .fillMaxSize()
                .background(Figma.PageDark)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(Modifier.fillMaxWidth().height(302.dp)) {
                ProvinceBanner(info?.imageName, Modifier.matchParentSize())
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x99070B4A), Color.Transparent, Color(0x80000000)),
                            ),
                        ),
                )
                LearnTopBar(
                    title = null,
                    user = user,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
                LearnBackPill(
                    text = "Kembali",
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 24.dp, top = 76.dp),
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 26.dp, end = 26.dp, bottom = 26.dp),
                ) {
                    Text(
                        text = "Provinsi $province",
                        color = Figma.Neon,
                        fontFamily = LearnFonts.Heading,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    val description = info?.description.orEmpty()
                    if (description.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = description,
                            color = Color.White,
                            fontFamily = LearnFonts.Heading,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            Text(
                text = "Kategori Budaya",
                color = Color.White,
                fontFamily = LearnFonts.Heading,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 26.dp, end = 26.dp, top = 28.dp, bottom = 18.dp),
            )
            Column(
                Modifier.padding(start = 12.dp, end = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp),
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
                Brush.verticalGradient(listOf(Color(0xFF2F6B3A), Figma.PageDark)),
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
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .height(59.dp)
            .clip(shape)
            .background(CardGradient)
            .border(1.dp, CardEdge, shape)
            .clickable(onClick = onClick)
            .padding(start = 18.dp, end = 21.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = category.displayName,
                color = Color.White,
                fontFamily = LearnFonts.Heading,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    color = Figma.Subtitle,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Chevron(Figma.Chevron, 2f, Modifier.size(width = 12.dp, height = 22.dp))
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
    val audioController = if (category == GlossaryCategory.LAGU) rememberLearnAudioController() else null

    LearnBackground {
        Column(Modifier.fillMaxSize()) {
            LearnTopBar(title = province, user = user)
            Row(
                Modifier.fillMaxWidth().padding(start = 24.dp, end = 32.dp, top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LearnCircleBackButton(onClick = onBack)
                Text(
                    text = category.displayName,
                    color = Figma.Neon,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }
            if (items == null) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Figma.Neon)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 32.dp, end = 32.dp, top = 10.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(items, key = { it.id }) { item ->
                        ItemCard(item = item, audioController = audioController)
                    }
                }
            }
        }
    }
}

@Composable
private fun LearnCircleBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xD91620A7))
            .border(1.dp, Color(0x45FFFFFF), CircleShape)
            .clickable(onClickLabel = "Kembali", role = Role.Button, onClick = onClick),
    ) {
        BackArrow(Modifier.size(width = 18.dp, height = 14.dp))
    }
}

@Composable
private fun LearnBackPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xD91620A7))
            .border(1.dp, Color(0x45FFFFFF), RoundedCornerShape(50))
            .clickable(onClickLabel = text, role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        BackArrow(Modifier.size(width = 15.dp, height = 12.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontFamily = LearnFonts.Heading,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BackArrow(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = 2.4.dp.toPx()
        drawLine(Color.White, Offset(0f, h / 2), Offset(w, h / 2), stroke, StrokeCap.Round)
        drawLine(Color.White, Offset(0f, h / 2), Offset(h / 2, 0f), stroke, StrokeCap.Round)
        drawLine(Color.White, Offset(0f, h / 2), Offset(h / 2, h), stroke, StrokeCap.Round)
    }
}

@Composable
private fun ItemCard(
    item: GlossaryItem,
    audioController: LearnAudioController? = null,
) {
    val shape = RoundedCornerShape(14.dp)
    val isPlayingSong = audioController?.let { it.activeItemId == item.id && it.isPlaying } == true

    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Figma.DetailCard)
            .border(1.dp, Figma.DetailBorder, shape)
            .padding(start = 16.dp, end = 14.dp, top = 17.dp, bottom = 26.dp),
    ) {
        ItemImage(
            item = item,
            isPlayingSong = isPlayingSong,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = item.name,
            color = Color.White,
            fontFamily = LearnFonts.Heading,
            fontSize = 26.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = item.description,
            color = Color.White,
            fontFamily = LearnFonts.Heading,
            fontSize = 11.sp,
            lineHeight = 14.6.sp,
            fontWeight = FontWeight.SemiBold,
        )
        if (item.category == GlossaryCategory.LAGU && audioController != null) {
            Spacer(Modifier.height(16.dp))
            SongPlayerBar(item = item, controller = audioController)
        }
    }
}

@Composable
private fun ItemImage(
    item: GlossaryItem,
    isPlayingSong: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val resId = rememberDrawableId(item.imageName)
    if (resId != 0) {
        val painter = painterResource(resId)
        val size = painter.intrinsicSize
        val naturalRatio = if (size.isSpecified && size.height > 0f) size.width / size.height else MaxImageRatio
        Box(modifier.aspectRatio(naturalRatio.coerceIn(MinImageRatio, MaxImageRatio))) {
            Image(
                painter = painter,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
            if (isPlayingSong) {
                PlayingIndicatorBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                )
            }
        }
    } else if (item.category == GlossaryCategory.LAGU) {
        Box(
            modifier.aspectRatio(MaxImageRatio).background(
                Brush.linearGradient(listOf(Color(0xFF281978), Color(0xFF131D5E), Color(0xFF0B1344))),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = categoryEmoji(item.category), fontSize = 38.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isPlayingSong) "Sedang Diputar" else "Lagu Daerah ${item.province}",
                    color = if (isPlayingSong) Figma.Neon else Figma.ProvinceText,
                    fontFamily = LearnFonts.Heading,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    } else {
        Box(
            modifier.aspectRatio(MaxImageRatio).background(
                Brush.verticalGradient(listOf(Color(0xFF1E2A78), Color(0xFF0B1344))),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = categoryEmoji(item.category), fontSize = 36.sp)
        }
    }
}

@Composable
private fun PlayingIndicatorBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xCC0A1059))
            .border(1.dp, Figma.Neon, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = "♫ Sedang Diputar",
            color = Figma.Neon,
            fontFamily = LearnFonts.Heading,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}