package com.kelompok8.lentera.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.ui.LenteraBackground
import com.kelompok8.lentera.core.ui.LenteraLogo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

private val StartRadius = 80.dp
private val LogoSize = 240.dp

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val finish by rememberUpdatedState(onFinished)
    val reveal = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300)
        launch { reveal.animateTo(1f, tween(durationMillis = 500, easing = FastOutSlowInEasing)) }
        textAlpha.animateTo(1f, tween(durationMillis = 600, delayMillis = 250))
        delay(200)
        finish()
    }

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        val endRadius = hypot(maxWidth.value, maxHeight.value) / 2f
        val radius = (StartRadius.value + (endRadius - StartRadius.value) * reveal.value).dp

        LenteraBackground(modifier = Modifier.clip(CircleRevealShape(radius))) {}
        Image(
            painter = painterResource(R.drawable.logo_lentera),
            contentDescription = "Logo LENTERA",
            modifier = Modifier.size(LogoSize)
        )
        LenteraLogo(
            modifier = Modifier
                .offset(y = 150.dp)
                .alpha(textAlpha.value)
        )
    }
}

private class CircleRevealShape(private val radius: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = with(density) { radius.toPx() }
        val center = Offset(size.width / 2f, size.height / 2f)
        return Outline.Generic(Path().apply { addOval(Rect(center, r)) })
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen(onFinished = {})
}
