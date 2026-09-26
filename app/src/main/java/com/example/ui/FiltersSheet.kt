package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BuiltInAssets
import com.example.model.FilterPreset
import com.example.ui.theme.CaveatFontFamily
import com.example.ui.theme.ComfortaaFontFamily
import com.example.ui.theme.IcyBlue
import com.example.ui.theme.Ink
import com.example.ui.theme.NunitoFontFamily

@Composable
fun FiltersSheet(
    selectedFilterId: String,
    filterIntensity: Float,
    onFilterSelected: (String) -> Unit,
    onIntensityChanged: (Float) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Film", "Retro", "Vintage", "Pastel", "Everyday")

    val filteredPresets = if (selectedCategory == "All") {
        BuiltInAssets.FILTER_PRESETS
    } else {
        BuiltInAssets.FILTER_PRESETS.filter { it.category == selectedCategory || it.id == "original" }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Title & Active Filter Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Film Filters",
                    fontFamily = ComfortaaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val activePreset = BuiltInAssets.FILTER_PRESETS.find { it.id == selectedFilterId }
                Text(
                    text = "${activePreset?.displayName ?: "Original"} (${(filterIntensity * 100).toInt()}%)",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontFamily = NunitoFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Filter Thumbnails Strip
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPresets) { preset ->
                    val isSelected = preset.id == selectedFilterId
                    FilterThumbnailItem(
                        preset = preset,
                        isSelected = isSelected,
                        onClick = { onFilterSelected(preset.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Intensity Slider
            if (selectedFilterId != "original") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Intensity",
                            fontFamily = NunitoFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${(filterIntensity * 100).toInt()}%",
                            fontFamily = NunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = filterIntensity,
                        onValueChange = onIntensityChanged,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("filter_intensity_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel / Apply Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Cancel", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("apply_filter_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Apply", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FilterThumbnailItem(
    preset: FilterPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("filter_item_${preset.id}")
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    when (preset.id) {
                        "kodak_gold" -> Color(0xFFF7E1B5)
                        "fuji_astia" -> Color(0xFFCCE4F5)
                        "portra_400" -> Color(0xFFF9EAE1)
                        "agfa_vista" -> Color(0xFFFBD7D7)
                        "disposable_90s" -> Color(0xFFFFE6A8)
                        "dreamy_bloom" -> Color(0xFFFBE4EE)
                        "polaroid_fade" -> Color(0xFFE8E5DD)
                        "sepia_1970" -> Color(0xFFDFCCA8)
                        "sakura_pink" -> Color(0xFFFFE0EA)
                        "matcha_green" -> Color(0xFFE0F2E5)
                        "golden_hour" -> Color(0xFFFFD4A3)
                        "monochrome_film" -> Color(0xFFD6D9DC)
                        else -> Color(0xFFE9EDF0)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (preset.id) {
                    "kodak_gold" -> "🎞️"
                    "fuji_astia" -> "🏔️"
                    "portra_400" -> "🌸"
                    "agfa_vista" -> "☀️"
                    "disposable_90s" -> "⚡"
                    "dreamy_bloom" -> "✨"
                    "polaroid_fade" -> "📸"
                    "sepia_1970" -> "📜"
                    "sakura_pink" -> "🌺"
                    "matcha_green" -> "🍵"
                    "golden_hour" -> "🌅"
                    "monochrome_film" -> "⚫"
                    else -> "🌿"
                },
                fontSize = 26.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = preset.displayName,
            fontFamily = NunitoFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
