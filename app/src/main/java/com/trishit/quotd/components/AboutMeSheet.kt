package com.trishit.quotd.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.trishit.quotd.FunnelDisplayFamily
import com.trishit.quotd.MuseoModernoFamily
import com.trishit.quotd.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutMeSheet(onDismiss: () -> Unit) {
    var showFollowMeDialog by remember { mutableStateOf(false) }
    val imagePainter = painterResource(R.drawable.myphoto)
    val privacyPolicyUrl = "https://github.com/Quantum3600/quotd/privacy-policy.md"
    val privacyString = buildAnnotatedString {
        pushLink(LinkAnnotation.Url(privacyPolicyUrl))
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                textDecoration = TextDecoration.None,
                fontFamily = MuseoModernoFamily,
                fontSize = 10.sp
            )
        ) {
            append("Privacy Policy")
        }
        pop()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "My Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(68.dp),
                expandedRatio = ButtonGroupDefaults.ExpandedRatio,
                horizontalArrangement = ButtonGroupDefaults.HorizontalArrangement
            ) {
                clickableItem(
                    onClick = {showFollowMeDialog = true},
                    icon = {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AlternateEmail,
                                contentDescription = "Follow Me"
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = "Follow Me",
                                fontFamily = FunnelDisplayFamily,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    label = "",
                    weight = 1f
                )
                clickableItem(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.buymeacoffee.com/trishit.me".toUri())
                        context.startActivity(browserIntent)
                    },
                    icon = {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Coffee,
                                contentDescription = "Support Me"
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = "Buy Me a Coffee",
                                fontFamily = FunnelDisplayFamily,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    label = "",
                    weight = 1f
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(privacyString)
        }
    }

    if (showFollowMeDialog) {
        FollowMeDialog(onDismiss = { showFollowMeDialog = false })
    }
}

@Composable
fun FollowMeDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val socialMedia: List<Triple<String, String, Int>> = listOf(
        Triple("Facebook", "https://www.facebook.com/com.trishit.quantum360", R.drawable.facebook),
        Triple("WhatsApp", "https://wa.me/919432854276", R.drawable.whatsapp),
        Triple("Instagram", "https://www.instagram.com/com.trishit.quantum360", R.drawable.instagram),
        Triple("X/Twitter", "https://x.com/Trishit18", R.drawable.x),
        Triple("GitHub", "https://github.com/Quantum3600", R.drawable.github),
        Triple("LinkedIn", "https://www.linkedin.com/in/trishit-majumdar-008344281/", R.drawable.linkedin)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Follow Me", fontFamily = FunnelDisplayFamily) },
        text = {
            LazyColumn {
                items(socialMedia) { (platform, url, icon) ->
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No app found to handle this action", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(icon),modifier = Modifier.size(24.dp), contentDescription = platform)
                            Spacer(Modifier.width(24.dp))
                            Text(platform, fontFamily = FunnelDisplayFamily, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
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
