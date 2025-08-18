package com.trishit.quotd.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.trishit.quotd.MainNavTab

/**
 * A pill-shaped floating bottom bar aligned to the left with two circular icon-only tabs: Home and Favourites.
 * Adds a full-size circular indicator that slides between tabs using a spring animation.
 */
@Composable
fun BottomTabs(
    selectedTabState: MutableState<MainNavTab>,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryFixed,
    selectedTabColor: Color = MaterialTheme.colorScheme.primary,
    selectedIconColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    val shape = RoundedCornerShape(48.dp)
    val tabSize = 54.dp
    val spacerSize = 16.dp

    // Compute target X offset for the moving indicator inside the padded container
    val targetX = when (selectedTabState.value) {
        MainNavTab.Home -> 0.dp
        MainNavTab.Favourites -> tabSize + spacerSize
    }

    val indicatorX = animateDpAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "tab-indicator-x"
    )

    Row(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .clip(shape)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // We need a Box to stack the moving indicator under the icons
        Box {
            // Full-size circular indicator that slides under the selected tab
            Box(
                modifier = Modifier
                    .size(tabSize)
                    .offset(x = indicatorX.value, y = 0.dp)
                    .clip(CircleShape)
                    .background(selectedTabColor)
                    .semantics { contentDescription = "Selected Tab Indicator" }
            )

            // Foreground row with icons as tappable targets
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Home tab
                val isHome = selectedTabState.value == MainNavTab.Home
                CircleTab(
                    isSelected = isHome,
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    onClick = { if (!isHome) selectedTabState.value = MainNavTab.Home },
                    image = Icons.Default.Home,
                    description = "Home",
                    tabSize = tabSize,
                )

                Spacer(Modifier.size(spacerSize))

                // Favourites tab
                val isFav = selectedTabState.value == MainNavTab.Favourites
                CircleTab(
                    isSelected = isFav,
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    onClick = { if (!isFav) selectedTabState.value = MainNavTab.Favourites },
                    image = Icons.Default.Favorite,
                    description = "Favourites",
                    tabSize = tabSize,
                )
            }
        }
    }
}

@Composable
private fun CircleTab(
    isSelected: Boolean,
    selectedIconColor: Color,
    unselectedIconColor: Color,
    onClick: () -> Unit,
    image: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    tabSize: androidx.compose.ui.unit.Dp,
) {
    val iconTint = androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) selectedIconColor else unselectedIconColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tab-icon-color"
    )

    Box(
        modifier = Modifier
            .size(tabSize)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { contentDescription = if (isSelected) "Selected" else "Unselected" },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = image,
            contentDescription = description,
            tint = iconTint.value
        )
    }
}

