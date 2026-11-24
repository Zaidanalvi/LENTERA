package com.kelompok8.lentera.game

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.model.Island
import com.kelompok8.lentera.core.model.Question
import com.kelompok8.lentera.core.ui.LenteraBackground
import com.kelompok8.lentera.core.ui.avatarEmojis
import com.kelompok8.lentera.ui.theme.LENTERATheme
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.TextLight
import com.kelompok8.lentera.ui.theme.mPlus1p

// PJ: Daud — 10 soal (4 pilihan ganda, 3 blok kata, 3 susun huruf), cek jawaban, pindah soal.
// Kalau semua soal selesai, panggil onFinished(jumlahBenar).

private val PillBlue = Color(0xFF1620A7)
private val PillBorder = Color(0x38FFFFFF)
private val CardDark = Color(0xFF0D1233)
private val CardDarkBorder = Color(0xFF2A3166)
private val PromptCard = Color(0xFF1A2042)
private val OptionStart = Color(0xFF0A15A0)
private val OptionEnd = Color(0xFF2232D8)
private val TileBlue = Color(0xFF1530D8)
private val TileOrange = Color(0xFFE0901E)
private val AnswerBar = Color(0xFF1A2BC0)
private val ChipBlue = Color(0xFF1524C8)
private val ChipUsed = Color(0xFF6B6F80)
private val BlankGrey = Color(0xFF8A8FA3)
private val ActionGreen = Color(0xFF19B21E)
private val WrongRed = Color(0xFFE0343F)
private val ProgressFill = Color(0xFF6C9BFF)
private val ProgressTrack = Color(0xFF2A3166)

@Composable
fun GameScreen(chapterId: String, onFinished: (Int) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: GameViewModel = viewModel(
        key = "game_$chapterId",
        factory = viewModelFactory {
            initializer { GameViewModel(chapterId, JsonQuestionRepository(context)) }
        }
    )
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished(state.correctCount)
    }

    var confirmExit by remember { mutableStateOf(false) }
    val requestExit = { if (state.questions.isEmpty()) onBack() else confirmExit = true }
    BackHandler(enabled = !state.isFinished) { requestExit() }
    if (confirmExit) {
        ExitDialog(
            onExit = {
                confirmExit = false
                onBack()
            },
            onStay = { confirmExit = false }
        )
    }

    GameContent(
        chapterId = chapterId,
        state = state,
        onSelectOption = viewModel::selectOption,
        onPick = viewModel::pick,
        onUnpick = viewModel::unpick,
        onCheck = viewModel::checkAnswer,
        onNext = viewModel::next,
        onRetry = viewModel::loadQuestions,
        onBack = requestExit
    )
}

@Composable
private fun GameContent(
    chapterId: String,
    state: GameUiState,
    onSelectOption: (Int) -> Unit,
    onPick: (Int) -> Unit,
    onUnpick: (Int) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val question = state.currentQuestion
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameHeader(islandName(chapterId), state.coin, state.avatarId, onBack)

        when {
            state.isLoading -> Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LenteraLime)
            }

            state.errorMessage != null || question == null -> Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameText(state.errorMessage ?: "Soal tidak ditemukan", size = 16, align = TextAlign.Center)
                ActionButton("COBA LAGI", onClick = onRetry)
            }

            else -> {
                Spacer(Modifier.height(20.dp))
                GameText("SOAL ${state.currentIndex + 1}", size = 20)
                Spacer(Modifier.height(6.dp))
                GameText(
                    instructionFor(question),
                    size = 12,
                    color = LenteraLime,
                    align = TextAlign.Center,
                    weight = FontWeight.Medium
                )
                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (question) {
                        is Question.MultipleChoice ->
                            MultipleChoiceBody(question, state.input, state.checkedCorrect, onSelectOption)
                        is Question.LetterArrange ->
                            LetterGridBody(question, state.input, state.checkedCorrect, onPick, onUnpick)
                        is Question.WordBlock ->
                            FillBlankParagraph(question, state.input, state.checkedCorrect, onUnpick)
                    }
                }

                if (question is Question.WordBlock) {
                    Spacer(Modifier.height(16.dp))
                    WordChoicePanel(question, state.input, state.checkedCorrect, onPick)
                }

                Spacer(Modifier.height(12.dp))
                state.checkedCorrect?.let { correct ->
                    FeedbackText(correct)
                    if (!correct) {
                        GameText(
                            "Jawaban benar: ${correctAnswerOf(question)}",
                            size = 13,
                            align = TextAlign.Center,
                            weight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (state.isChecked) {
                    ActionButton(if (state.isLastQuestion) "SELESAI" else "LANJUT", onClick = onNext)
                } else {
                    ActionButton("PERIKSA JAWABAN", onClick = onCheck, enabled = state.canCheck)
                }
                Spacer(Modifier.height(20.dp))
                ProgressFooter(state.currentIndex, state.questions.size)
            }
        }
    }
}

private fun islandName(chapterId: String): String {
    val key = chapterId.substringBefore("_")
    val island = Island.entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
    return "Pulau ${island?.displayName ?: key.replaceFirstChar { it.uppercase() }}"
}

@Composable
private fun GameHeader(title: String, coin: Int, avatarId: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBack)
            Spacer(Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(PillBlue, RoundedCornerShape(20.dp))
                    .border(1.dp, PillBorder, RoundedCornerShape(20.dp))
                    .padding(vertical = 3.dp, horizontal = 10.dp)
            ) {
                Image(painterResource(R.drawable.coin), contentDescription = "Koin", modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                GameText(coin.toString(), size = 14)
            }
        }
        GameText(title, size = 16, color = LenteraLime)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(PillBlue, CircleShape)
                .border(1.dp, PillBorder, CircleShape)
        ) {
            Image(
                painterResource(avatarEmojis.getOrElse(avatarId) { avatarEmojis[0] }),
                contentDescription = "Profil",
                modifier = Modifier.size(31.dp).clip(CircleShape)
            )
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(PillBlue)
            .border(1.dp, PillBorder, CircleShape)
            .clickable(onClickLabel = "Kembali", role = Role.Button, onClick = onClick)
    ) {
        Canvas(Modifier.size(14.dp)) {
            val arrow = Path().apply {
                moveTo(size.width * 0.68f, size.height * 0.12f)
                lineTo(size.width * 0.3f, size.height * 0.5f)
                lineTo(size.width * 0.68f, size.height * 0.88f)
            }
            drawPath(
                arrow,
                color = TextLight,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

@Composable
private fun ExitDialog(onExit: () -> Unit, onStay: () -> Unit) {
    AlertDialog(
        onDismissRequest = onStay,
        containerColor = CardDark,
        title = { GameText("Keluar dari permainan?", size = 18) },
        text = {
            GameText(
                "Jawabanmu di chapter ini tidak akan disimpan.",
                size = 14,
                weight = FontWeight.Medium
            )
        },
        confirmButton = {
            TextButton(onClick = onExit) { GameText("KELUAR", size = 14, color = WrongRed) }
        },
        dismissButton = {
            TextButton(onClick = onStay) { GameText("LANJUT MAIN", size = 14, color = LenteraLime) }
        }
    )
}

@Composable
private fun ProgressFooter(index: Int, total: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        GameText("Soal ${index + 1}/$total", size = 10, color = TextLight.copy(alpha = 0.8f))
        LinearProgressIndicator(
            progress = { (index + 1) / total.toFloat() },
            color = ProgressFill,
            trackColor = ProgressTrack,
            gapSize = 0.dp,
            drawStopIndicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
        )
    }
}

@Composable
private fun MultipleChoiceBody(
    question: Question.MultipleChoice,
    input: AnswerInput,
    checkedCorrect: Boolean?,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PromptCard)
                .border(1.dp, CardDarkBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            GameText(question.prompt, size = 14, align = TextAlign.Justify, weight = FontWeight.Medium)
        }
        QuestionImage(question.imageName)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            question.options.forEachIndexed { index, option ->
                val selected = input.selectedOption == index
                val background: Brush = when {
                    checkedCorrect == true && selected -> Brush.linearGradient(listOf(ActionGreen, ActionGreen))
                    checkedCorrect == false && selected -> Brush.linearGradient(listOf(WrongRed, WrongRed))
                    checkedCorrect == false && index == question.correctIndex ->
                        Brush.linearGradient(listOf(ActionGreen, ActionGreen))
                    checkedCorrect == null && selected -> Brush.linearGradient(listOf(ActionGreen, ActionGreen))
                    else -> Brush.horizontalGradient(listOf(OptionStart, OptionEnd))
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(background)
                        .clickable(enabled = checkedCorrect == null) { onSelect(index) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    GameText(option, size = 16, align = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun LetterGridBody(
    question: Question.LetterArrange,
    input: AnswerInput,
    checkedCorrect: Boolean?,
    onPick: (Int) -> Unit,
    onUnpick: (Int) -> Unit
) {
    val locked = checkedCorrect != null
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameText(question.prompt, size = 13, align = TextAlign.Center, weight = FontWeight.Medium)
            QuestionImage(question.imageName)
        }

        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            question.letters.chunked(JsonQuestionRepository.GRID_COLUMNS).forEachIndexed { row, letters ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    letters.forEachIndexed { col, letter ->
                        val index = row * JsonQuestionRepository.GRID_COLUMNS + col
                        val position = input.pickedIndices.indexOf(index)
                        LetterTile(
                            letter = letter,
                            color = if (position >= 0) TileOrange else TileBlue,
                            enabled = !locked
                        ) {
                            if (position >= 0) onUnpick(position) else onPick(index)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AnswerBar)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(question.answer.length) { position ->
                val index = input.pickedIndices.getOrNull(position)
                if (index != null) {
                    val letter = question.letters[index]
                    // setelah diperiksa, huruf yang posisinya sudah benar tetap hijau
                    val answerColor = when {
                        checkedCorrect == null -> TileBlue
                        letter.equals(question.answer.getOrNull(position) ?: ' ', ignoreCase = true) -> ActionGreen
                        else -> WrongRed
                    }
                    LetterTile(letter, answerColor, enabled = !locked) { onUnpick(position) }
                } else {
                    Box(
                        Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .border(1.5.dp, TextLight.copy(alpha = 0.4f), RoundedCornerShape(5.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterTile(letter: Char, color: Color, enabled: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color)
            .border(1.5.dp, TextLight, RoundedCornerShape(5.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        GameText(letter.toString(), size = 18)
    }
}

@Composable
private fun FillBlankParagraph(
    question: Question.WordBlock,
    input: AnswerInput,
    checkedCorrect: Boolean?,
    onUnpick: (Int) -> Unit
) {
    val locked = checkedCorrect != null
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val slotWidth = remember(question.blocks) {
        val widest = question.blocks.maxOfOrNull { measurer.measure(it, ChipTextStyle).size.width } ?: 0
        with(density) { widest.toDp() } + ChipHorizontalPadding * 2 + 2.dp
    }
    val parts = question.prompt.split(JsonQuestionRepository.BLANK)
    val text = buildAnnotatedString {
        parts.forEachIndexed { i, part ->
            append(part)
            if (i < parts.lastIndex) appendInlineContent("blank_$i", "[…]")
        }
    }
    val inlineContent = (0 until parts.lastIndex).associate { position ->
        "blank_$position" to InlineTextContent(
            Placeholder(
                width = with(density) { slotWidth.toSp() },
                height = with(density) { ChipHeight.toSp() },
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            val index = input.pickedIndices.getOrNull(position)
            if (index != null) {
                // dicek per kotak, jadi pemain tahu bagian mana yang salah
                val color = when {
                    checkedCorrect == null -> ChipUsed
                    question.blocks[index] == question.answer.getOrNull(position) -> ActionGreen
                    else -> WrongRed
                }
                WordChip(
                    question.blocks[index], color, enabled = !locked,
                    modifier = Modifier.fillMaxSize()
                ) { onUnpick(position) }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(BlankGrey.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                )
            }
        }
    }
    Text(
        text = text,
        inlineContent = inlineContent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        color = TextLight,
        fontFamily = mPlus1p,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 28.sp,
        textAlign = TextAlign.Justify
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordChoicePanel(
    question: Question.WordBlock,
    input: AnswerInput,
    checkedCorrect: Boolean?,
    onPick: (Int) -> Unit
) {
    val locked = checkedCorrect != null
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, CardDarkBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        question.blocks.forEachIndexed { index, word ->
            val used = index in input.pickedIndices
            WordChip(
                word, if (used) ChipUsed else ChipBlue, enabled = !used && !locked,
                modifier = Modifier.height(PanelChipHeight),
                textStyle = PanelChipTextStyle,
                horizontalPadding = 12.dp
            ) { onPick(index) }
        }
    }
}

private val ChipHeight = 22.dp
private val ChipHorizontalPadding = 6.dp
private val ChipTextStyle = TextStyle(fontFamily = mPlus1p, fontWeight = FontWeight.Medium, fontSize = 12.sp)

private val PanelChipHeight = 34.dp
private val PanelChipTextStyle = TextStyle(fontFamily = mPlus1p, fontWeight = FontWeight.Medium, fontSize = 14.sp)

@Composable
private fun WordChip(
    text: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier.height(ChipHeight),
    textStyle: TextStyle = ChipTextStyle,
    horizontalPadding: androidx.compose.ui.unit.Dp = ChipHorizontalPadding,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .border(1.dp, TextLight.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = textStyle, color = TextLight, maxLines = 1, softWrap = false)
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun QuestionImage(imageName: String?) {
    if (imageName.isNullOrBlank()) return
    val context = LocalContext.current
    val resId = remember(imageName) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }
    if (resId == 0) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
            .background(CardDark),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun instructionFor(question: Question): String = when (question) {
    is Question.MultipleChoice -> "Pilih satu jawaban yang benar"
    is Question.WordBlock -> "Ketuk kata di bawah untuk mengisi bagian kosong. Ketuk kata yang sudah terisi untuk membatalkan"
    is Question.LetterArrange -> "Ketuk huruf untuk menyusun jawaban. Ketuk huruf di kotak jawaban untuk menghapus"
}

private fun correctAnswerOf(question: Question): String = when (question) {
    is Question.MultipleChoice -> question.options.getOrElse(question.correctIndex) { "-" }
    is Question.WordBlock -> question.answer.joinToString(", ")
    is Question.LetterArrange -> question.answer.uppercase()
}

@Composable
private fun FeedbackText(correct: Boolean) {
    GameText(
        text = if (correct) "Benar!" else "Kurang tepat",
        size = 14,
        color = if (correct) ActionGreen else WrongRed,
        align = TextAlign.Center
    )
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = ActionGreen,
            contentColor = TextLight,
            disabledContainerColor = ActionGreen.copy(alpha = 0.4f),
            disabledContentColor = TextLight.copy(alpha = 0.7f)
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(50.dp)
    ) {
        Text(text, fontFamily = mPlus1p, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GameText(
    text: String,
    size: Int,
    modifier: Modifier = Modifier,
    color: Color = TextLight,
    align: TextAlign? = null,
    weight: FontWeight = FontWeight.Bold
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontFamily = mPlus1p,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size + 5).sp,
        textAlign = align
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Tipe 1 - Pilihan ganda")
@Composable
private fun GameMultipleChoicePreview() {
    GamePreview(
        Question.MultipleChoice(
            "p1",
            "Pulau Jawa memiliki beragam tradisi yang diwariskan secara turun-temurun. Salah satunya adalah " +
                "tradisi Sekaten. Di kota manakah tradisi Sekaten dikenal sebagai tradisi budaya yang masih dilestarikan?",
            null,
            listOf("Bandung", "Yogyakarta", "Surabaya", "Serang"), 1
        ),
        AnswerInput(selectedOption = 1)
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Tipe 2 - Grid huruf")
@Composable
private fun GameLetterPreview() {
    GamePreview(
        Question.LetterArrange(
            "p2", "Gambar di bawah ini merupakan senjata tradisional Jawa dengan nama?", null,
            "CSVXTGERIKJHAEKNCUORTYCGPUUXEJ".toList(), "KERIS"
        ),
        AnswerInput(pickedIndices = listOf(9, 6, 7))
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Tipe 3 - Paragraf rumpang")
@Composable
private fun GameFillBlankPreview() {
    GamePreview(
        Question.WordBlock(
            "p3",
            "Yogyakarta terkenal dengan makanan khas bernama ___ yang terbuat dari nangka muda. " +
                "Dari Jawa Timur, ada ___ , sup daging berkuah hitam dari kluwek.",
            null,
            listOf("Rendang", "Gudeg", "Pempek", "Rawon", "Papeda"),
            listOf("Gudeg", "Rawon")
        ),
        AnswerInput(pickedIndices = listOf(1, null))
    )
}

@Composable
private fun GamePreview(question: Question, input: AnswerInput) {
    LENTERATheme {
        LenteraBackground {
            GameContent(
                chapterId = "jawa_1",
                state = GameUiState(isLoading = false, questions = listOf(question), input = input),
                onSelectOption = {}, onPick = {}, onUnpick = {},
                onCheck = {}, onNext = {}, onRetry = {}, onBack = {}
            )
        }
    }
}
