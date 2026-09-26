package com.example.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Adjustments
import com.example.ui.theme.ComfortaaFontFamily
import com.example.ui.theme.NunitoFontFamily

@Composable
fun AdjustSheet(
    adjustments: Adjustments,
    onAdjustmentsChanged: (Adjustments) -> Unit,
    onAutoEnhance: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Light") }
    val tabs = listOf("Light", "Color", "Detail", "Effects", "Pro HSL")

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

            // Title & Auto-Enhance / Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Adjustments",
                    fontFamily = ComfortaaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = onAutoEnhance,
                        modifier = Modifier.testTag("auto_enhance_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Auto", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(36.dp).testTag("reset_adjustments_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-category Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tab,
                            fontFamily = NunitoFontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Controls based on active tab
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    "Light" -> {
                        AdjustmentSlider(
                            name = "Exposure",
                            value = adjustments.exposure,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(exposure = it)) }
                        )
                        AdjustmentSlider(
                            name = "Brightness",
                            value = adjustments.brightness,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(brightness = it)) }
                        )
                        AdjustmentSlider(
                            name = "Contrast",
                            value = adjustments.contrast,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(contrast = it)) }
                        )
                        AdjustmentSlider(
                            name = "Highlights",
                            value = adjustments.highlights,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(highlights = it)) }
                        )
                        AdjustmentSlider(
                            name = "Shadows",
                            value = adjustments.shadows,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(shadows = it)) }
                        )
                        AdjustmentSlider(
                            name = "Whites",
                            value = adjustments.whites,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(whites = it)) }
                        )
                        AdjustmentSlider(
                            name = "Blacks",
                            value = adjustments.blacks,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(blacks = it)) }
                        )
                    }
                    "Color" -> {
                        AdjustmentSlider(
                            name = "Saturation",
                            value = adjustments.saturation,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(saturation = it)) }
                        )
                        AdjustmentSlider(
                            name = "Vibrance",
                            value = adjustments.vibrance,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(vibrance = it)) }
                        )
                        AdjustmentSlider(
                            name = "Warmth (Temp)",
                            value = adjustments.temperature,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(temperature = it)) }
                        )
                        AdjustmentSlider(
                            name = "Tint",
                            value = adjustments.tint,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(tint = it)) }
                        )
                        AdjustmentSlider(
                            name = "Sepia Tone",
                            value = adjustments.sepia,
                            valueRange = 0f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(sepia = it)) }
                        )
                        AdjustmentSlider(
                            name = "Hue Shift",
                            value = adjustments.hue,
                            valueRange = -180f..180f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(hue = it)) }
                        )
                    }
                    "Detail" -> {
                        AdjustmentSlider(
                            name = "Sharpen",
                            value = adjustments.sharpen,
                            valueRange = 0f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(sharpen = it)) }
                        )
                        AdjustmentSlider(
                            name = "Clarity (Midtone)",
                            value = adjustments.clarity,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(clarity = it)) }
                        )
                        AdjustmentSlider(
                            name = "Dehaze",
                            value = adjustments.dehaze,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(dehaze = it)) }
                        )
                    }
                    "Effects" -> {
                        AdjustmentSlider(
                            name = "Vignette",
                            value = adjustments.vignette,
                            valueRange = 0f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(vignette = it)) }
                        )
                        AdjustmentSlider(
                            name = "Film Grain",
                            value = adjustments.grain,
                            valueRange = 0f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(grain = it)) }
                        )
                        AdjustmentSlider(
                            name = "Vintage Fade",
                            value = adjustments.fade,
                            valueRange = 0f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(fade = it)) }
                        )
                        AdjustmentSlider(
                            name = "Dreamy Soft Glow",
                            value = adjustments.glow,
                            valueRange = 0f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(glow = it)) }
                        )
                    }
                    "Pro HSL" -> {
                        AdjustmentSlider(
                            name = "Red Channel",
                            value = adjustments.channelRed,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(channelRed = it)) }
                        )
                        AdjustmentSlider(
                            name = "Green Channel",
                            value = adjustments.channelGreen,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(channelGreen = it)) }
                        )
                        AdjustmentSlider(
                            name = "Blue Channel",
                            value = adjustments.channelBlue,
                            valueRange = -100f..100f,
                            onValueChange = { onAdjustmentsChanged(adjustments.copy(channelBlue = it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Done Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("done_adjust_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Done", fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdjustmentSlider(
    name: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value.toInt().toString(),
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("slider_${name.lowercase().replace(" ", "_")}")
        )
    }
}
