package com.example.data

import com.example.R
import com.example.model.FilterPreset

data class StickerItem(
    val id: String,
    val name: String,
    val category: String,
    val emojiOrVector: String, // SVG-like symbol or descriptive motif
    val defaultScale: Float = 1.0f,
    val isDoodle: Boolean = false
)

data class FrameItem(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val defaultPadding: Float = 16f,
    val defaultCornerRadius: Float = 12f,
    val defaultColorHex: String = "#FFF9EE"
)

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val sampleDrawableRes: Int,
    val filterId: String,
    val frameId: String,
    val sampleText: String
)

object BuiltInAssets {

    val SAMPLE_DRAWABLE_COFFEE = R.drawable.sample_coffee_1790413275216
    val SAMPLE_DRAWABLE_SUNSET = R.drawable.sample_sunset_1790413289876
    val SAMPLE_DRAWABLE_CAMERA = R.drawable.sample_camera_1790413303446
    val BANNER_SCRAPBOOK = R.drawable.banner_scrapbook_1790413258573
    val FLOWERS_SPLASH = R.drawable.jislly_flowers_1790413241590
    val AVATAR_USER = R.drawable.user_avatar_1790413318520

    // 15 ORIGINAL FILM FILTER PRESETS (Real Color Matrix 4x5)
    val FILTER_PRESETS: List<FilterPreset> = listOf(
        FilterPreset(
            id = "original",
            displayName = "Original",
            category = "All",
            description = "Natural unedited photo",
            colorMatrix = floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "kodak_gold",
            displayName = "Kodak",
            category = "Film",
            description = "Warm amber glow with nostalgic golden hour tones",
            colorMatrix = floatArrayOf(
                1.18f, 0.05f, 0.00f, 0f, 12f,
                0.02f, 1.08f, 0.00f, 0f, 6f,
                0.00f, 0.02f, 0.90f, 0f, -8f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "fuji_astia",
            displayName = "Fuji",
            category = "Film",
            description = "Crisp cool blues, emerald greens and fresh tones",
            colorMatrix = floatArrayOf(
                0.95f, 0.02f, 0.02f, 0f, -4f,
                0.02f, 1.12f, 0.02f, 0f, 8f,
                0.05f, 0.02f, 1.18f, 0f, 14f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "portra_400",
            displayName = "Portra",
            category = "Film",
            description = "Soft creamy skin tones, gentle contrast, pastel warmth",
            colorMatrix = floatArrayOf(
                1.10f, 0.06f, 0.02f, 0f, 10f,
                0.03f, 1.05f, 0.02f, 0f, 6f,
                0.02f, 0.04f, 0.96f, 0f, 4f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "agfa_vista",
            displayName = "Agfa",
            category = "Film",
            description = "Punchy vintage red-warm tones with rich contrast",
            colorMatrix = floatArrayOf(
                1.22f, 0.02f, 0.00f, 0f, 8f,
                0.00f, 1.06f, 0.02f, 0f, -2f,
                0.02f, 0.00f, 0.92f, 0f, -4f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "disposable_90s",
            displayName = "Flash 90s",
            category = "Retro",
            description = "Bright flash pop, punchy highlights and moody shadow falloff",
            colorMatrix = floatArrayOf(
                1.20f, 0.00f, 0.00f, 0f, 18f,
                0.00f, 1.16f, 0.00f, 0f, 15f,
                0.00f, 0.00f, 1.12f, 0f, 12f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "dreamy_bloom",
            displayName = "Dreamy",
            category = "Dreamy",
            description = "Soft ethereal glow, gentle pink blush diffusion",
            colorMatrix = floatArrayOf(
                1.12f, 0.04f, 0.04f, 0f, 16f,
                0.02f, 1.04f, 0.04f, 0f, 10f,
                0.04f, 0.02f, 1.10f, 0f, 18f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "polaroid_fade",
            displayName = "Polaroid",
            category = "Vintage",
            description = "Lifted matte blacks, cyan shadow cast, milky highlights",
            colorMatrix = floatArrayOf(
                0.96f, 0.04f, 0.02f, 0f, 22f,
                0.02f, 0.98f, 0.04f, 0f, 24f,
                0.04f, 0.06f, 0.92f, 0f, 30f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "sepia_1970",
            displayName = "1970s",
            category = "Vintage",
            description = "Classic warm monochrome sepia film from the 70s",
            colorMatrix = floatArrayOf(
                0.393f * 1.1f, 0.769f * 1.1f, 0.189f * 1.1f, 0f, 15f,
                0.349f * 1.05f, 0.686f * 1.05f, 0.168f * 1.05f, 0f, 10f,
                0.272f * 0.9f, 0.534f * 0.9f, 0.131f * 0.9f, 0f, 0f,
                0.000f, 0.000f, 0.000f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "washed_denim",
            displayName = "Denim",
            category = "Retro",
            description = "Muted vintage blues, desaturated earthy tones",
            colorMatrix = floatArrayOf(
                0.88f, 0.04f, 0.04f, 0f, 4f,
                0.04f, 0.92f, 0.06f, 0f, 6f,
                0.06f, 0.08f, 1.14f, 0f, 16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "sakura_pink",
            displayName = "Sakura",
            category = "Pastel",
            description = "Rosy pastel pink tint with luminous soft white highlights",
            colorMatrix = floatArrayOf(
                1.16f, 0.02f, 0.04f, 0f, 18f,
                0.02f, 1.02f, 0.02f, 0f, 8f,
                0.04f, 0.02f, 1.08f, 0f, 16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "matcha_green",
            displayName = "Matcha",
            category = "Pastel",
            description = "Sage and olive botanical tones with gentle muted contrast",
            colorMatrix = floatArrayOf(
                0.94f, 0.05f, 0.02f, 0f, 6f,
                0.04f, 1.15f, 0.02f, 0f, 14f,
                0.02f, 0.06f, 0.96f, 0f, 4f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "golden_hour",
            displayName = "Golden",
            category = "Everyday",
            description = "Sun-drenched honey radiance and warm afternoon vibes",
            colorMatrix = floatArrayOf(
                1.24f, 0.08f, 0.00f, 0f, 20f,
                0.02f, 1.10f, 0.00f, 0f, 12f,
                0.00f, 0.00f, 0.88f, 0f, -12f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "monochrome_film",
            displayName = "B&W Film",
            category = "Film",
            description = "Fine silver gelatin monochrome with deep analog blacks",
            colorMatrix = floatArrayOf(
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.299f, 0.587f, 0.114f, 0f, 0f,
                0.000f, 0.000f, 0.000f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "cinematic_teal",
            displayName = "Cinematic",
            category = "Retro",
            description = "Teal shadow tones with warm golden skin highlights",
            colorMatrix = floatArrayOf(
                1.15f, 0.00f, 0.02f, 0f, 8f,
                0.00f, 1.05f, 0.04f, 0f, 4f,
                0.00f, 0.06f, 1.18f, 0f, 14f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "clean_pure",
            displayName = "Clean",
            category = "Everyday",
            description = "High clarity natural boost with pristine whites",
            colorMatrix = floatArrayOf(
                1.06f, 0.00f, 0.00f, 0f, 6f,
                0.00f, 1.06f, 0.00f, 0f, 6f,
                0.00f, 0.00f, 1.06f, 0f, 6f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "cinestill_800t",
            displayName = "Cine 800T",
            category = "Film",
            description = "Tungsten cool cyan tones with radiant warm amber halations",
            colorMatrix = floatArrayOf(
                1.12f, 0.00f, 0.05f, 0f, 16f,
                0.00f, 1.05f, 0.02f, 0f, 4f,
                0.02f, 0.04f, 1.22f, 0f, 18f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "fuji_velvia",
            displayName = "Velvia 50",
            category = "Film",
            description = "Ultra vibrant slide film with vivid emeralds and royal blues",
            colorMatrix = floatArrayOf(
                1.25f, 0.02f, 0.00f, 0f, 8f,
                0.02f, 1.30f, 0.00f, 0f, 10f,
                0.00f, 0.02f, 1.25f, 0f, 12f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "fuji_pro400h",
            displayName = "Pro 400H",
            category = "Film",
            description = "Soft cyan-green shadow bias, airy bright pastel tones",
            colorMatrix = floatArrayOf(
                0.96f, 0.04f, 0.04f, 0f, 12f,
                0.02f, 1.14f, 0.02f, 0f, 16f,
                0.04f, 0.02f, 1.10f, 0f, 16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "kodak_trix",
            displayName = "Tri-X 400",
            category = "Film",
            description = "Legendary high-contrast documentary black and white film",
            colorMatrix = floatArrayOf(
                0.35f, 0.55f, 0.10f, 0f, -5f,
                0.35f, 0.55f, 0.10f, 0f, -5f,
                0.35f, 0.55f, 0.10f, 0f, -5f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "polaroid_600",
            displayName = "Polaroid 600",
            category = "Vintage",
            description = "Warm creamy matte blacks with nostalgic retro color shift",
            colorMatrix = floatArrayOf(
                1.08f, 0.05f, 0.00f, 0f, 24f,
                0.02f, 1.02f, 0.00f, 0f, 20f,
                0.00f, 0.04f, 0.88f, 0f, 22f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "kyoto_pastel",
            displayName = "Kyoto",
            category = "Pastel",
            description = "Gentle low-contrast Japanese aesthetic with creamy warm whites",
            colorMatrix = floatArrayOf(
                1.04f, 0.04f, 0.02f, 0f, 18f,
                0.02f, 1.02f, 0.02f, 0f, 16f,
                0.02f, 0.02f, 1.02f, 0f, 18f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "moody_nordic",
            displayName = "Nordic",
            category = "Retro",
            description = "Muted pines, slate charcoal tones and desaturated contrast",
            colorMatrix = floatArrayOf(
                0.86f, 0.04f, 0.04f, 0f, -6f,
                0.04f, 0.94f, 0.04f, 0f, 4f,
                0.04f, 0.04f, 1.02f, 0f, 6f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "super8_gold",
            displayName = "Super 8",
            category = "Retro",
            description = "1970s warm home movie camera film with rich amber hues",
            colorMatrix = floatArrayOf(
                1.22f, 0.06f, 0.00f, 0f, 18f,
                0.02f, 1.08f, 0.00f, 0f, 10f,
                0.00f, 0.00f, 0.82f, 0f, -8f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "muted_sage",
            displayName = "Sage",
            category = "Pastel",
            description = "Earthy botanical soft muted green with lifted shadow tones",
            colorMatrix = floatArrayOf(
                0.92f, 0.06f, 0.02f, 0f, 12f,
                0.04f, 1.10f, 0.04f, 0f, 18f,
                0.02f, 0.04f, 0.98f, 0f, 14f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "sunset_amber",
            displayName = "Amber Glow",
            category = "Everyday",
            description = "Deep sunset copper radiance with glowing golden light",
            colorMatrix = floatArrayOf(
                1.26f, 0.06f, 0.00f, 0f, 22f,
                0.00f, 1.08f, 0.00f, 0f, 14f,
                0.00f, 0.00f, 0.85f, 0f, -14f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "y2k_gloss",
            displayName = "Y2K Gloss",
            category = "Retro",
            description = "Vibrant millennium pop with glossy highlights and cyber cyan tint",
            colorMatrix = floatArrayOf(
                1.18f, 0.05f, 0.08f, 0f, 16f,
                0.04f, 1.15f, 0.02f, 0f, 12f,
                0.10f, 0.05f, 1.25f, 0f, 20f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "honey_glow",
            displayName = "Honey Glow",
            category = "Everyday",
            description = "Gentle honey warmth and sunbeam radiance for golden memories",
            colorMatrix = floatArrayOf(
                1.22f, 0.08f, 0.00f, 0f, 18f,
                0.04f, 1.12f, 0.00f, 0f, 12f,
                0.00f, 0.02f, 0.90f, 0f, -6f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "film_noir_40s",
            displayName = "Noir 1940",
            category = "Film",
            description = "Deep dramatic cinema contrast with rich silver specular highlights",
            colorMatrix = floatArrayOf(
                0.32f, 0.60f, 0.08f, 0f, -10f,
                0.32f, 0.60f, 0.08f, 0f, -10f,
                0.32f, 0.60f, 0.08f, 0f, -10f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "lavender_haze",
            displayName = "Lavender Haze",
            category = "Pastel",
            description = "Dreamy lilac glow with soft petal blush and luminous midtones",
            colorMatrix = floatArrayOf(
                1.12f, 0.04f, 0.08f, 0f, 16f,
                0.02f, 1.02f, 0.05f, 0f, 10f,
                0.08f, 0.04f, 1.22f, 0f, 22f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "blockbuster_teal_orange",
            displayName = "Teal & Orange",
            category = "Everyday",
            description = "Cinematic Hollywood complementary look with warm skin tones",
            colorMatrix = floatArrayOf(
                1.25f, 0.00f, -0.04f, 0f, 12f,
                0.02f, 1.08f, 0.02f, 0f, 4f,
                -0.05f, 0.06f, 1.24f, 0f, 16f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "kodachrome_64",
            displayName = "Kodachrome",
            category = "Vintage",
            description = "Authentic 1960s color slide film with deep rich red-yellow saturation",
            colorMatrix = floatArrayOf(
                1.24f, 0.02f, 0.00f, 0f, 14f,
                0.00f, 1.10f, 0.02f, 0f, 6f,
                0.02f, 0.00f, 0.88f, 0f, -10f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "matcha_latte",
            displayName = "Matcha Latte",
            category = "Pastel",
            description = "Milky soft Japanese cafe green with cream highlight diffusion",
            colorMatrix = floatArrayOf(
                0.96f, 0.06f, 0.02f, 0f, 8f,
                0.04f, 1.16f, 0.04f, 0f, 16f,
                0.02f, 0.04f, 0.98f, 0f, 10f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        ),
        FilterPreset(
            id = "faded_cinema",
            displayName = "Faded Cinema",
            category = "Retro",
            description = "Nostalgic 1970s drive-in cinema print with lifted dusty shadows",
            colorMatrix = floatArrayOf(
                1.08f, 0.04f, 0.02f, 0f, 22f,
                0.02f, 1.04f, 0.02f, 0f, 18f,
                0.00f, 0.02f, 0.92f, 0f, 24f,
                0.00f, 0.00f, 0.00f, 1f, 0f
            )
        )
    )

    // 150+ STICKERS CATALOG WITH COMPREHENSIVE THEMATIC CATEGORIES
    val STICKERS: List<StickerItem> = buildList {
        // 1. Botanical & Flowers (30 items)
        val botanical = listOf(
            "Forget-Me-Not Blue" to "🌸", "Daisy Bloom" to "🌼", "Cherry Blossom" to "🌺",
            "Lavender Sprig" to "🪻", "Eucalyptus Leaf" to "🌿", "Tulip Stem" to "🌷",
            "Sunflower Petal" to "🌻", "Four-Leaf Clover" to "🍀", "Baby's Breath" to "🌱",
            "Pressed Rose" to "🌹", "Botanical Fern" to "🪴", "Cotton Flower" to "🌾",
            "Olive Branch" to "🫒", "Golden Wreath" to "🏵️", "Wild Chamomile" to "🌼",
            "Blue Hydrangea" to "🪻", "Peony Blush" to "🌸", "Maple Leaf" to "🍁",
            "Ginkgo Leaf" to "🍂", "Sprout Leaf" to "🌱", "Herb Sprig" to "🌿",
            "Cactus Bloom" to "🌵", "Pine Cone" to "🌲", "Lotus Petal" to "🪷",
            "Wisteria Blossom" to "🍇", "Morning Glory" to "🫐", "Floral Cluster" to "💐",
            "Jasmine Star" to "⭐", "Wild Berry Vine" to "🫐", "Forget-Me-Not Garland" to "🌸"
        )
        botanical.forEachIndexed { i, (name, icon) ->
            add(StickerItem("botanical_$i", name, "Botanical", icon))
        }

        // 2. Sweet Treats & Fruits (26 items)
        val sweets = listOf(
            "Ripe Strawberry" to "🍓", "Wild Blueberry" to "🫐", "Twin Cherries" to "🍒",
            "Lemon Slice" to "🍋", "Juicy Peach" to "🍑", "Crisp Croissant" to "🥐",
            "Iced Matcha Latte" to "🍵", "Boba Milk Tea" to "🧋", "Iced Latte Cup" to "🥤",
            "Vintage Coffee Cup" to "☕", "Pink Frosted Donut" to "🍩", "Vanilla Macaron" to "🧁",
            "Strawberry Shortcake" to "🍰", "Ribbon Lollipop" to "🍭", "Chocolate Cookie" to "🍪",
            "Ice Cream Cone" to "🍦", "Honey Pot" to "🍯", "Apple Wedge" to "🍎",
            "Pancake Stack" to "🥞", "French Baguette" to "🥖", "Waffle Crisp" to "🧇",
            "Melon Slice" to "🍈", "Sweet Candy" to "🍬", "Pretzel Twist" to "🥨",
            "Bubble Soda" to "🍹", "Afternoon Tea Pot" to "🫖"
        )
        sweets.forEachIndexed { i, (name, icon) ->
            add(StickerItem("sweet_$i", name, "Sweets", icon))
        }

        // 3. Retro & Vintage (26 items)
        val vintage = listOf(
            "Vintage 35mm Camera" to "📷", "Instant Polaroid" to "📸", "Cassette Tape" to "📼",
            "Vinyl Record" to "📻", "Audio CD Disk" to "💿", "Postage Stamp" to "🏷️",
            "Antique Key" to "🗝️", "Retro Radio" to "📻", "Luggage Tag" to "🏷️",
            "Ticket Stub" to "🎟️", "Old Typewriter" to "⌨️", "Film Reel" to "🎞️",
            "Pocket Watch" to "⏱️", "Vintage Compass" to "🧭", "Quill Pen" to "✒️",
            "Wax Seal" to "💌", "Postcard Letter" to "✉️", "Magnifying Glass" to "🔍",
            "Hourglass Sand" to "⏳", "Old Book Volume" to "📖", "Silver Bell" to "🔔",
            "Framed Cameo" to "🖼️", "Classic Telephone" to "☎️", "Film Strip Slip" to "🎞️",
            "Antique Lamp" to "🏮", "Vintage Mirror" to "🪞"
        )
        vintage.forEachIndexed { i, (name, icon) ->
            add(StickerItem("vintage_$i", name, "Vintage", icon))
        }

        // 4. Ribbons, Bows & Sparkles (26 items)
        val bows = listOf(
            "Satin Pink Bow" to "🎀", "Blue Velvet Ribbon" to "🎗️", "Twin Hearts" to "💕",
            "Sparkle Star" to "✨", "Golden Star" to "⭐", "Shooting Star" to "💫",
            "Glitter Shimmer" to "🌟", "Hand-Drawn Heart" to "♡", "Angel Wings" to "🪽",
            "Little Crown" to "👑", "Party Confetti" to "🎉", "Love Letter" to "💌",
            "Heart With Ribbon" to "💝", "Sparkling Heart" to "💖", "White Heart" to "🤍",
            "Blue Heart" to "💙", "Yellow Starlet" to "✨", "Ribbon Knot" to "🎀",
            "Doodle Sparkle" to "✦", "Burst Star" to "💥", "Magic Wand" to "🪄",
            "Pastel Bow Tie" to "🎀", "Diamond Gem" to "💎", "Crystal Prism" to "🔮",
            "Glitter Droplets" to "🫧", "Halo Ring" to "💫"
        )
        bows.forEachIndexed { i, (name, icon) ->
            add(StickerItem("bow_$i", name, "Ribbons", icon))
        }

        // 5. Cats, Clouds & Doodles (26 items)
        val doodles = listOf(
            "Sleeping White Cat" to "🐱", "Pink Cat Paw" to "🐾", "Fluffy Cloud" to "☁️",
            "Smiling Sun" to "☀️", "Pastel Rainbow" to "🌈", "Crescent Moon" to "🌙",
            "Vintage Butterfly" to "🦋", "Paper Airplane" to "✈️", "Smiling Face" to "😊",
            "Doodle Daisy" to "🌼", "Swirl Ribbon" to "〰️", "Cute Ghostie" to "👻",
            "Fluffy Bunny" to "🐰", "Teddy Bear" to "🧸", "Music Notes" to "🎵",
            "Treble Clef" to "🎼", "Raindrop Bloom" to "💧", "Soft Breeze" to "🍃",
            "Sparkle Burst" to "✴️", "Mini Planet" to "🪐", "Doodle Arrow" to "🏹",
            "Little Mushroom" to "🍄", "Sweet Duckling" to "🐥", "Happy Puppy" to "🐶",
            "Sleepy Koala" to "🐨", "Lucky Clover Doodle" to "🍀"
        )
        doodles.forEachIndexed { i, (name, icon) ->
            add(StickerItem("doodle_$i", name, "Doodles", icon, isDoodle = true))
        }

        // 6. Stationery, Labels & Tape (25 items)
        val stationery = listOf(
            "Gingham Washi Tape" to "🩹", "Floral Masking Tape" to "🎀", "Kraft Paper Strip" to "📜",
            "Label 'GOOD DAY'" to "🏷️", "Label 'LOVE THIS'" to "🏷️", "Label 'JiSLLY VIBES'" to "✨",
            "Label 'SWEET MEMORIES'" to "📝", "Label 'FAVORITE'" to "⭐", "Label 'PHOTO DUMP'" to "📸",
            "Label 'SUNSET MOOD'" to "🌇", "Notebook Spiral" to "📒", "Paper Clip Brass" to "📎",
            "Binder Clip Pink" to "🖇️", "Push Pin Gold" to "📌", "Round Pin" to "📍",
            "Post-it Note" to "🗒️", "Torn Memo Paper" to "📄", "Bar Code Label" to "🏷️",
            "Date Stamp 'TODAY'" to "🗓️", "Airmail Border" to "✉️", "Receipt Slip" to "🧾",
            "Dymo Embossed Tag" to "🏷️", "Handwritten 'xo'" to "✍️", "Heart Doodle Tag" to "💌",
            "Palette Swatch" to "🎨"
        )
        stationery.forEachIndexed { i, (name, icon) ->
            add(StickerItem("stationery_$i", name, "Stationery", icon))
        }
    }

    // 40+ EDITABLE FRAMES CATALOG
    val FRAMES: List<FrameItem> = listOf(
        // None
        FrameItem("none", "None", "All", "Full-bleed borderless image", 0f, 0f),

        // Cute Frames
        FrameItem("floral_buttermilk", "Floral Buttermilk", "Cute", "Cream yellow border with blue forget-me-not flower corners", 20f, 16f, "#F6EBC3"),
        FrameItem("floral_icy_blue", "Floral Icy Blue", "Cute", "Icy sky blue border with white daisy corners", 20f, 16f, "#8FBFE3"),
        FrameItem("gingham_mint", "Gingham Mint", "Cute", "Picnic mint check pattern frame", 18f, 12f, "#DDF3E6"),
        FrameItem("pink_ribbon", "Pink Ribbon", "Cute", "Powder pink frame with tied ribbon corner bows", 22f, 20f, "#F7D5DF"),
        FrameItem("daisies_garden", "Daisies Garden", "Cute", "Perimeter of blooming white and yellow daisies", 24f, 16f, "#FFF9EE"),
        FrameItem("heart_cutout", "Sweet Heart", "Cute", "Soft romantic border with heart corner accents", 22f, 24f, "#FCE7EE"),
        FrameItem("lavender_dream", "Lavender Dream", "Cute", "Soft lilac purple border with botanical sprigs", 20f, 16f, "#EAE1FF"),
        FrameItem("strawberry_milk", "Strawberry Milk", "Cute", "Baby pink border with mini strawberry accents", 18f, 14f, "#FADCE4"),

        // Vintage & Scrapbook
        FrameItem("polaroid_classic", "Polaroid Instant", "Vintage", "Authentic wide bottom white instant photo border", 24f, 8f, "#FFFDF8"),
        FrameItem("vintage_kraft", "Vintage Kraft", "Vintage", "Textured craft cardboard frame with corner masking tape", 22f, 10f, "#EBD8BE"),
        FrameItem("film_strip_35mm", "35mm Film Strip", "Film", "Black negative leader with sprocket holes & frame index", 26f, 6f, "#111418"),
        FrameItem("washi_tape_corners", "Washi Tape Corners", "Scrapbook", "Photo pinned down with 4 translucent pastel washi tapes", 18f, 12f, "#FAF4E8"),
        FrameItem("postage_stamp", "Postage Stamp", "Scrapbook", "Perforated wavy stamp edge border with postmark", 22f, 14f, "#F8F1E2"),
        FrameItem("spiral_notebook", "Spiral Notebook", "Scrapbook", "Pastel blue ruled notebook page with left spiral ring holes", 24f, 12f, "#F4F8FC"),
        FrameItem("graph_paper", "Graph Paper", "Scrapbook", "Aesthetic grid paper background with washi tape accents", 20f, 10f, "#F6F7F9"),
        FrameItem("torn_paper", "Torn Paper Edge", "Scrapbook", "Hand-torn organic fibrous deckle edge paper", 22f, 8f, "#FAF6EE"),
        FrameItem("darkroom_contact", "Darkroom Contact", "Film", "Analog darkroom contact sheet black border with film codes", 24f, 4f, "#0D1117"),
        FrameItem("slide_mount", "35mm Slide Mount", "Film", "Cardboard 2x2 slide mount with stamped date and Kodachrome font", 26f, 14f, "#F2ECE1"),

        // Minimal & Editorial
        FrameItem("minimal_white", "Gallery White", "Minimal", "Clean timeless museum matte border with subtle shadow", 22f, 4f, "#FFFFFF"),
        FrameItem("editorial_line", "Editorial Thin Line", "Minimal", "High-fashion double margin with crisp fine lines", 16f, 0f, "#FFFFFF"),
        FrameItem("minimal_ink", "Matte Ink", "Minimal", "Deep charcoal slate modern border", 20f, 8f, "#263443"),
        FrameItem("soft_pill", "Rounded Pill", "Minimal", "Smooth pill-shaped curved container with generous padding", 24f, 32f, "#FFF9EE"),
        FrameItem("floating_card", "Floating Card", "Minimal", "Elevated card surface with gentle ambient drop shadow", 18f, 18f, "#FFFFFF"),
        FrameItem("retro_tv", "Retro TV CRT", "Vintage", "Curved television tube monitor with rounded corners", 24f, 28f, "#2B2D31"),
        FrameItem("butter_cream", "Warm Cream", "Minimal", "Signature JISLLY warm buttermilk cream solid matte", 20f, 16f, "#FFF9EE"),
        FrameItem("pastel_sky", "Pastel Sky", "Minimal", "Muted baby blue matte with soft white inner stroke", 20f, 16f, "#DCEBF8"),

        // Additional Aesthetic Frames
        FrameItem("french_lace", "French Lace", "Cute", "Delicate vintage lace crochet perimeter pattern", 22f, 16f, "#FAF5EB"),
        FrameItem("film_kodak", "Film 400 Box", "Film", "Yellow and red vintage film carton border accent", 22f, 8f, "#FFE873"),
        FrameItem("memo_pad", "Pastel Memo Pad", "Scrapbook", "Cute sticky memo pad with tape top center", 22f, 12f, "#FEFBF0"),
        FrameItem("vintage_sepia_border", "Sepia Studio", "Vintage", "1900s portrait studio embossed card frame", 26f, 6f, "#E2D3B8"),
        FrameItem("golden_glimmer", "Golden Glimmer", "Cute", "Subtle golden foil corner stars and warm frame", 20f, 16f, "#FFFDF5"),
        FrameItem("picnic_plaid", "Picnic Plaid", "Cute", "Warm pastel peach and butter gingham pattern", 20f, 14f, "#FCEEDB"),
        FrameItem("matcha_mist", "Matcha Mist", "Minimal", "Soft sage green matte with clean radius", 18f, 14f, "#EBF5EE"),
        FrameItem("monochrome_polaroid", "Mono Instant", "Vintage", "Black instant photo frame for moody monochrome shots", 24f, 8f, "#1E2228"),
        FrameItem("baby_blue_ribbon", "Baby Blue Ribbon", "Cute", "Icy blue frame with delicate hand-drawn bows", 22f, 20f, "#E1EEF8"),
        FrameItem("doodle_stars", "Doodle Stars", "Cute", "Hand-drawn stars and sparkle doodles along edge", 20f, 16f, "#FFF9EE"),
        FrameItem("washi_duo", "Washi Duo Tape", "Scrapbook", "Diagonal top-left and bottom-right patterned tapes", 18f, 12f, "#FFFDF8"),
        FrameItem("archival_mat", "Archival Mat", "Minimal", "Beveled archival mat board with inner shadow", 26f, 2f, "#F7F5EE"),
        FrameItem("coffee_diary", "Coffee Diary", "Scrapbook", "Cafe receipt and coffee cup ring motif on cream paper", 22f, 12f, "#F6EFE2"),
        FrameItem("twilight_film", "Twilight Sprocket", "Film", "Deep midnight navy film border with cyan sprockets", 24f, 8f, "#141D26"),
        FrameItem("film_strip_double", "Double 35mm Strip", "Film", "Double vertical 35mm film strips with white frame index", 26f, 4f, "#0D0F12"),
        FrameItem("hasselblad_120", "Hasselblad 120", "Film", "Iconic medium format film edge notch with black matte", 22f, 6f, "#1A1D20"),
        FrameItem("airmail_stripes", "Airmail Travel", "Scrapbook", "Classic red & cyan diagonal striped postage border", 20f, 10f, "#FFFBF5"),
        FrameItem("dried_botanicals", "Herbarium Flora", "Cute", "Pressed botanical leaves and delicate floral perimeter", 24f, 18f, "#F7F3E9"),
        FrameItem("vintage_newspaper", "Aesthetic Gazette", "Vintage", "Vintage daily newspaper masthead scrapbook border", 22f, 8f, "#F5EFE3"),
        FrameItem("polaroid_wide", "Polaroid Wide", "Vintage", "Extra-wide vintage instant photo border with handwritten area", 26f, 12f, "#FFFDF9"),
        FrameItem("french_crochet", "Crochet Lace", "Cute", "Delicate vintage scalloped lace border pattern", 22f, 16f, "#FAF6EE"),
        FrameItem("washi_quad", "Washi Quad Corner", "Scrapbook", "Four pastel patterned washi tape strips securing corners", 20f, 12f, "#FAF5EB"),
        FrameItem("gold_double_line", "Gold Minimal Line", "Minimal", "Fine double margin lines in shimmering soft gold", 16f, 2f, "#FFFDF7"),
        FrameItem("retro_crt_scan", "CRT Monitor Scan", "Vintage", "Curved cathode tube display with scanline bezel", 24f, 24f, "#1F2328"),
        FrameItem("darkroom_36a", "Darkroom 36A", "Film", "Black border with yellow stamped frame count '36A EXP'", 24f, 4f, "#111417"),
        FrameItem("blossom_ribbon", "Blossom & Ribbon", "Cute", "Powder pink frame with floral sprigs and silk ribbons", 22f, 20f, "#FCEEF2"),
        FrameItem("scalloped_cloud", "Scalloped Cloud", "Cute", "Delicate organic scalloped wavy cloud border", 24f, 22f, "#FFF9EE"),
        FrameItem("y2k_cyber_glow", "Y2K Cyber Glow", "Cute", "Iridescent neon pastel sparkle border", 22f, 16f, "#EAE1FF"),
        FrameItem("kawaii_grid_journal", "Kawaii Grid Journal", "Scrapbook", "Pastel ruled diary page with tape and sticker motifs", 24f, 14f, "#FAF8F2"),
        FrameItem("vintage_postcard", "Vintage Postcard", "Scrapbook", "Antique postcard frame with postmark stamps", 24f, 10f, "#F8F2E4"),
        FrameItem("arch_museum", "Museum Arch", "Minimal", "Architectural soft arch top border", 26f, 32f, "#FFFDF9"),
        FrameItem("double_film_leader", "Double Film Leader", "Film", "Double vintage 35mm black film negative leader", 26f, 4f, "#0D0F13"),
        FrameItem("pastel_duo_ribbon", "Pastel Duo Ribbon", "Cute", "Two-tone corner ribbon bows with pastel border", 22f, 18f, "#FEEFF2")
    )

    // PRE-MADE SCRAPBOOK & PHOTO TEMPLATES
    val TEMPLATES: List<TemplateItem> = listOf(
        TemplateItem(
            id = "template_cafe",
            title = "Small edits Big vibes ♡",
            category = "Scrapbook",
            description = "Nostalgic cafe afternoon with iced latte, polaroids and forget-me-not flowers",
            sampleDrawableRes = SAMPLE_DRAWABLE_COFFEE,
            filterId = "kodak_gold",
            frameId = "polaroid_classic",
            sampleText = "Small edits\nBig vibes ♡"
        ),
        TemplateItem(
            id = "template_sunset",
            title = "Sunset Mood",
            category = "Film",
            description = "Dreamy pastel dusk sky with warm cinematic film grain and telephone lines",
            sampleDrawableRes = SAMPLE_DRAWABLE_SUNSET,
            filterId = "fuji_astia",
            frameId = "floral_icy_blue",
            sampleText = "sunset\nmood ♡"
        ),
        TemplateItem(
            id = "template_camera",
            title = "Good Things Take Time",
            category = "Cute",
            description = "Retro instant camera flat lay with sweet berries and floral stickers",
            sampleDrawableRes = SAMPLE_DRAWABLE_CAMERA,
            filterId = "portra_400",
            frameId = "washi_tape_corners",
            sampleText = "good\nthings\ntake time ✦"
        ),
        TemplateItem(
            id = "template_flower",
            title = "Floral Dream",
            category = "Pastel",
            description = "Soft aesthetic bouquet with hand-drawn ribbon bows and delicate doodles",
            sampleDrawableRes = FLOWERS_SPLASH,
            filterId = "dreamy_bloom",
            frameId = "floral_buttermilk",
            sampleText = "Edit + Create + Be You"
        ),
        TemplateItem(
            id = "template_retro_film",
            title = "35mm Nostalgia",
            category = "Film",
            description = "Authentic negative film leader with analog warmth and timestamp",
            sampleDrawableRes = SAMPLE_DRAWABLE_SUNSET,
            filterId = "agfa_vista",
            frameId = "film_strip_35mm",
            sampleText = "SEP 26 '26"
        ),
        TemplateItem(
            id = "template_vintage_post",
            title = "Airmail Scrapbook",
            category = "Vintage",
            description = "Perforated vintage stamp frame with travel doodles and washi tape",
            sampleDrawableRes = SAMPLE_DRAWABLE_COFFEE,
            filterId = "sepia_1970",
            frameId = "postage_stamp",
            sampleText = "sweet memories ♡"
        )
    )
}
