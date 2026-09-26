package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vignette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Buttermilk
import com.example.ui.theme.ComfortaaFontFamily
import com.example.ui.theme.IcyBlue
import com.example.ui.theme.Ink
import com.example.ui.theme.Lavender
import com.example.ui.theme.Mint
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.PowderPink

data class ToolGridItem(
    val title: String,
    val icon: ImageVector,
    val bgColor: Color,
    val toolKey: String
)

@Composable
fun ToolsScreen(
    onToolSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val essentialTools = listOf(
        ToolGridItem("Crop", Icons.Default.Crop, Mint, "crop"),
        ToolGridItem("Adjust", Icons.Default.Tune, Buttermilk, "adjust"),
        ToolGridItem("Retouch", Icons.Default.Brush, PowderPink, "adjust"),
        ToolGridItem("Blur", Icons.Default.BlurOn, Lavender, "adjust"),
        ToolGridItem("Rotate", Icons.Default.RotateRight, IcyBlue.copy(alpha = 0.6f), "crop"),
        ToolGridItem("Exposure", Icons.Default.WbSunny, Buttermilk, "adjust"),
        ToolGridItem("HSL", Icons.Default.ColorLens, Mint, "adjust"),
        ToolGridItem("Vignette", Icons.Default.Vignette, Lavender, "adjust"),
        ToolGridItem("Sharpen", Icons.Default.ShutterSpeed, PowderPink, "adjust"),
        ToolGridItem("Noise", Icons.Default.Grain, IcyBlue.copy(alpha = 0.6f), "adjust"),
        ToolGridItem("Warmth", Icons.Default.WbSunny, Buttermilk, "adjust"),
        ToolGridItem("Fade", Icons.Default.InvertColors, Mint, "adjust")
    )

    val aiTools = listOf(
        ToolGridItem("AI Enhance", Icons.Default.AutoAwesome, Buttermilk, "auto_enhance"),
        ToolGridItem("Background", Icons.Default.AspectRatio, Lavender, "adjust"),
        ToolGridItem("Object Remove", Icons.Default.Brush, PowderPink, "adjust")
    )

    val moreTools = listOf(
        ToolGridItem("Canvas", Icons.Default.AspectRatio, Mint, "crop"),
        ToolGridItem("Resize", Icons.Default.Crop, IcyBlue.copy(alpha = 0.6f), "crop"),
        ToolGridItem("Compress", Icons.Default.Compress, Buttermilk, "adjust"),
        ToolGridItem("GIF", Icons.Default.Animation, Lavender, "adjust")
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Tools",
                    fontFamily = ComfortaaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(text = "👑", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Essential Editing Tools Grid (4 columns)
            Text(
                text = "Essential Tools",
                fontFamily = ComfortaaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                essentialTools.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { tool ->
                            Box(modifier = Modifier.weight(1f)) {
                                ToolGridCard(tool = tool, onClick = { onToolSelected(tool.toolKey) })
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Tools Header with "New" Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "AI Tools",
                    fontFamily = ComfortaaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF476F))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "New",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                aiTools.forEach { tool ->
                    Box(modifier = Modifier.weight(1f)) {
                        ToolGridCard(tool = tool, onClick = { onToolSelected(tool.toolKey) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // More Tools
            Text(
                text = "More",
                fontFamily = ComfortaaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                moreTools.forEach { tool ->
                    Box(modifier = Modifier.weight(1f)) {
                        ToolGridCard(tool = tool, onClick = { onToolSelected(tool.toolKey) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ToolGridCard(
    tool: ToolGridItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .clickable(onClick = onClick)
            .testTag("tool_grid_${tool.title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tool.bgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = tool.title,
                tint = Ink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tool.title,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Ink,
                maxLines = 1
            )
        }
    }
}
