package com.trishit.quotd.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.EmojiEmotions
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.trishit.quotd.MuseoModernoFamily
import com.trishit.quotd.R

@Composable
fun OverflowMenu() {
    var menuExpanded by remember { mutableStateOf(false) }
    var showAboutMeSheet by remember { mutableStateOf(false) }
    var showFollowMeDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            shape = RoundedCornerShape(16.dp),
        ) {
            DropdownMenuItem(
                text = { Text("About Me", fontFamily =  MuseoModernoFamily) },
                onClick = {
                    showAboutMeSheet = true
                    menuExpanded = false
                },
                leadingIcon = { Icon(Icons.Rounded.EmojiEmotions, contentDescription = "About Me") }
            )
            DropdownMenuItem(
                text = { Text(text = "Buy Me a Coffee", fontFamily =  MuseoModernoFamily) },
                onClick = {
                    val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.buymeacoffee.com/trishit.me".toUri())
                    context.startActivity(browserIntent)
                },
                leadingIcon = { Icon(Icons.Rounded.Coffee, contentDescription = "Buy Me a Coffee") }
            )
            DropdownMenuItem(
                text = { Text(text = "Follow Me", fontFamily =  MuseoModernoFamily) },
                onClick = { showFollowMeDialog = true },
                leadingIcon = { Icon(Icons.Rounded.AlternateEmail, contentDescription = "Follow Me") }
            )
            DropdownMenuItem(
                text = { Text(text = "Share with Friends", fontFamily =  MuseoModernoFamily) },
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out Quotd, a great app for daily quotes! [https://github.com/Quantum3600/quotd/releases]")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                    menuExpanded = false
                },
                leadingIcon = { Icon(Icons.Rounded.Share, contentDescription = "Share with Friends") }
            )
            DropdownMenuItem(
                text = { Text("Rate the App", fontFamily =  MuseoModernoFamily) },
                onClick = {
                    val packageName = context.packageName
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                            "market://details?id=$packageName".toUri()))
                    } catch (e: ActivityNotFoundException) {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=$packageName".toUri()))
                    }
                    menuExpanded = false
                },
                leadingIcon = { Icon(Icons.Rounded.Star, contentDescription = "Rate the App") }
            )
            DropdownMenuItem(
                text = { Text("Suggest a Feature", fontFamily =  MuseoModernoFamily) },
                onClick = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:trishitquantum360@gmail.com".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, "Feedback for Quotd App")
                    }
                    try {
                        context.startActivity(emailIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                    menuExpanded = false
                },
                leadingIcon = { Icon(Icons.Rounded.Feedback, contentDescription = "Give Feedback") }
            )
        }
    }
    if (showAboutMeSheet) {
        AboutMeSheet(onDismiss = { showAboutMeSheet = false })
    }
    if (showFollowMeDialog) {
        FollowMeDialog(onDismiss = { showFollowMeDialog = false })
    }
}