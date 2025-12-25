package com.trishit.quotd.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.trishit.quotd.MuseoModernoFamily
import com.trishit.quotd.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutMeSheet(onDismiss: () -> Unit) {
    var showFollowMeDialog by remember { mutableStateOf(false) }
    val imagePainter = painterResource(R.drawable.myphoto)


    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "My Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(120.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "👋Hi! I am Trishit Majumdar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MuseoModernoFamily
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Android Developer | Tech Enthusiast",
                fontSize = 16.sp,
                fontFamily = MuseoModernoFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            val context = LocalContext.current
            ButtonGroup(
                overflowIndicator = {},
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(60.dp),
                expandedRatio = ButtonGroupDefaults.ExpandedRatio,
                horizontalArrangement = ButtonGroupDefaults.HorizontalArrangement
            ) {
                clickableItem(
                    onClick = {showFollowMeDialog = true},
                    label = "Follow Me",
                    weight = 1f
                )
                clickableItem(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.buymeacoffee.com/trishit.me".toUri())
                        context.startActivity(browserIntent)
                    },
                    label = "Buy me a coffee",
                    weight = 1f
                )
            }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                val context = LocalContext.current
//                Button(onClick = { showFollowMeDialog = true }) {
//                    Text("Follow Me", fontFamily = MuseoModernoFamily)
//                }
//                Button(onClick = {
//                    val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.buymeacoffee.com/trishit.me".toUri())
//                    context.startActivity(browserIntent)
//                }) {
//                    Text("Buy me a coffee", fontFamily = MuseoModernoFamily)
//                }
//            }
        }
    }

    if (showFollowMeDialog) {
        FollowMeDialog(onDismiss = { showFollowMeDialog = false })
    }
}

@Composable
fun FollowMeDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val socialMedia = listOf(
        "Facebook" to "https://www.facebook.com/trishit.banerjee.3",
        "WhatsApp" to "https://wa.me/919876543210", // Placeholder, user can change
        "Instagram" to "https://www.instagram.com/trishit.banerjee",
        "Twitter" to "https://twitter.com/TrishitBanerjee",
        "GitHub" to "https://github.com/Quantum3600",
        "LinkedIn" to "https://www.linkedin.com/in/trishit-banerjee-a62123248/"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Follow Me", fontFamily = MuseoModernoFamily) },
        text = {
            LazyColumn {
                items(socialMedia) { (platform, url) ->
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No app found to handle this action", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(platform, fontFamily = MuseoModernoFamily)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = MuseoModernoFamily)
            }
        }
    )
}
