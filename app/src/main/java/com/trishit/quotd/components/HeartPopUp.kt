package com.trishit.quotd.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    offsetX: Dp,
    offsetY: Dp,
    onAnimationEnd: () -> Unit
) {
    if (isVisible) {
        val coroutineScope = rememberCoroutineScope()
        val scale = remember { Animatable(0f) }
        val alpha = remember { Animatable(1f) }
        val heartSize = 100.dp // Adjust size as needed

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                scale.animateTo(
                    targetValue = 1.2f, // Scale up slightly more than final
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
            coroutineScope.launch {
                delay(300) // Start fading out after a short delay
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
                onAnimationEnd() // Notify when animation is complete
            }
        }

        Box(
            modifier = Modifier
                .offset {
                    // Center the heart on the tap position
                    IntOffset(
                        offsetX.roundToPx(),
                        offsetY.roundToPx()
                    )
                }
                .alpha(alpha.value)
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Floating Heart",
                // Use a gradient for the heart color
                tint = Color.Unspecified, // Important: Set to Unspecified when using a brush
                modifier = Modifier
                    .size(heartSize * scale.value)
                    .then(
                        Modifier.drawWithCache {
                            val brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF007F), Color(0xFFFFA500)) // Red-Pink to Orange
                            )
                            onDrawWithContent {
                                with(drawContext.canvas.nativeCanvas) {
                                    val checkPoint = saveLayer(null, null)
                                    drawContent() // This draws the icon shape as a mask
                                    drawRect(brush = brush, blendMode = BlendMode.SrcIn)
                                    restoreToCount(checkPoint)
                                }
                            }
                        }.graphicsLayer(alpha = 0.99f) // Required for brush to work with tint
                    ) // Apply gradient using a Brush
            )
        }
    }
}
