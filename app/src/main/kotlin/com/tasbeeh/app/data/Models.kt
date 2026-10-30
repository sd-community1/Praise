package com.tasbeeh.app.data

data class DhikrItem(
    val id: Int,
    val arabic: String,
    val transliteration: String
)

data class SessionRecord(
    val id: Long = System.currentTimeMillis(),
    val dhikrArabic: String,
    val count: Int,
    val target: Int,
    val completed: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class TasbeehState(
    val currentCount: Int = 0,
    val selectedDhikr: DhikrItem = DHIKR_LIST[0],
    val customDhikr: String = "",
    val target: Int = 33,
    val isCustomTarget: Boolean = false,
    val customTargetValue: Int = 100,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val isDarkTheme: Boolean = true
)

val DHIKR_LIST = listOf(
    DhikrItem(1, "سبحان الله", "Subhan Allah"),
    DhikrItem(2, "الحمد لله", "Alhamdulillah"),
    DhikrItem(3, "الله أكبر", "Allahu Akbar"),
    DhikrItem(4, "أستغفر الله", "Astaghfirullah"),
    DhikrItem(5, "لا إله إلا الله", "La ilaha illa Allah"),
    DhikrItem(6, "لا حول ولا قوة إلا بالله", "La hawla wa la quwwata illa billah"),
    DhikrItem(7, "اللهم صل وسلم على نبينا محمد", "Allahumma salli wa sallim 'ala nabiyyina Muhammad"),
    DhikrItem(0, "ذكر مخصص", "Custom Dhikr")
)

val PRESET_TARGETS = listOf(33, 99, 100, 500)
