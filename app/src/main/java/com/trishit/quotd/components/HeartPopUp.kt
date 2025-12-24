package com.trishit.quotd.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HeartPopup(
    isVisible: Boolean,
    isCentered: Boolean,
    offsetX: Dp,
    offsetY: Dp,
    onAnimationEnd: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val heartSize = 100.dp

    LaunchedEffect(isVisible) {
        if (isVisible) {
            scale.snapTo(0f)
            alpha.snapTo(1f)

            launch {
                scale.animateTo(
                    targetValue = 1.2f,
                    animationSpec = spring(
                        stiffness = 300f,
                        dampingRatio = 0.5f
                    )
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        stiffness = 300f,
                        dampingRatio = 0.5f
                    )
                )
            }

            launch {
                delay(300)
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
                onAnimationEnd()
            }
        }
    }

    if (isVisible || alpha.value > 0f) {
        Box(
            contentAlignment = if (isCentered) Alignment.Center else Alignment.TopStart,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Floating Heart",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(heartSize * scale.value)
                    .then(
                        if (!isCentered) {
                            Modifier.offset {
                                IntOffset(
                                    (offsetX.roundToPx() - (heartSize.roundToPx() * scale.value / 2).toInt()),
                                    (offsetY.roundToPx() - (heartSize.roundToPx() * scale.value / 2).toInt())
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
                    .alpha(alpha.value)
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFF007F), Color(0xFFFFA500))
                        )
                        onDrawWithContent {
                            with(drawContext.canvas.nativeCanvas) {
                                val checkPoint = saveLayer(null, null)
                                drawContent()
                                drawRect(brush = brush, blendMode = BlendMode.SrcIn)
                                restoreToCount(checkPoint)
                            }
                        }
                    }
                    .graphicsLayer(alpha = 0.99f)
            )
        }
    }
}
