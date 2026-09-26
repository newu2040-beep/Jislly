package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BuiltInAssets
import com.example.model.Adjustments
import com.example.processing.ImageFilterEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("JISLLY", appName)
    }

    @Test
    fun testFilterPresetsAvailable() {
        assertTrue("Filter presets should have at least 25 items", BuiltInAssets.FILTER_PRESETS.size >= 25)
        val cinestill = BuiltInAssets.FILTER_PRESETS.find { it.id == "cinestill_800t" }
        assertNotNull("Cinestill filter should exist", cinestill)
        assertEquals(20, cinestill!!.colorMatrix.size)
    }

    @Test
    fun testStickersLibrarySize() {
        assertTrue("Sticker library should have at least 150 stickers", BuiltInAssets.STICKERS.size >= 150)
    }

    @Test
    fun testFramesLibrarySize() {
        assertTrue("Frames library should have at least 50 frames", BuiltInAssets.FRAMES.size >= 50)
    }

    @Test
    fun testCompositeColorMatrixGeneration() {
        val matrix = ImageFilterEngine.buildCompositeColorMatrix(
            filterId = "cinestill_800t",
            filterIntensity = 0.9f,
            adjustments = Adjustments(
                exposure = 10f,
                contrast = 15f,
                saturation = 10f,
                temperature = 5f,
                clarity = 12f,
                dehaze = 8f,
                hue = 15f,
                channelRed = 5f,
                channelGreen = -2f,
                channelBlue = 8f
            )
        )
        assertEquals(20, matrix.size)
    }
}
