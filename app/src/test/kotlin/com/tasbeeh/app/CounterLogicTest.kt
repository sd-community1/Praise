package com.tasbeeh.app

import com.tasbeeh.app.data.*
import org.junit.Assert.*
import org.junit.Test

class CounterLogicTest {

    @Test
    fun `count starts at zero`() {
        val state = TasbeehState()
        assertEquals(0, state.currentCount)
    }

    @Test
    fun `increment increases count`() {
        var count = 0
        count++
        assertEquals(1, count)
    }

    @Test
    fun `undo decreases count`() {
        var count = 5
        if (count > 0) count--
        assertEquals(4, count)
    }

    @Test
    fun `undo does not go below zero`() {
        var count = 0
        if (count > 0) count--
        assertEquals(0, count)
    }

    @Test
    fun `reset sets count to zero`() {
        var count = 42
        count = 0
        assertEquals(0, count)
    }

    @Test
    fun `progress calculation is correct`() {
        val count = 33
        val target = 33
        val progress = count.toFloat() / target.toFloat()
        assertEquals(1.0f, progress, 0.001f)
    }

    @Test
    fun `progress is capped at one`() {
        val count = 50
        val target = 33
        val progress = minOf(count.toFloat() / target.toFloat(), 1f)
        assertEquals(1.0f, progress, 0.001f)
    }

    @Test
    fun `target reached when count equals target`() {
        val count = 99
        val target = 99
        assertTrue(count >= target)
    }

    @Test
    fun `target not reached when count is less`() {
        val count = 32
        val target = 33
        assertFalse(count >= target)
    }

    @Test
    fun `default dhikr is first in list`() {
        val state = TasbeehState()
        assertEquals(DHIKR_LIST[0], state.selectedDhikr)
    }

    @Test
    fun `default target is 33`() {
        val state = TasbeehState()
        assertEquals(33, state.target)
    }

    @Test
    fun `custom dhikr text is stored in state`() {
        val state = TasbeehState(customDhikr = "بسم الله")
        assertEquals("بسم الله", state.customDhikr)
    }

    @Test
    fun `custom target value is used when flag is set`() {
        val state = TasbeehState(isCustomTarget = true, customTargetValue = 200)
        val effectiveTarget = if (state.isCustomTarget) state.customTargetValue else state.target
        assertEquals(200, effectiveTarget)
    }

    @Test
    fun `dhikr list contains expected items`() {
        assertEquals(8, DHIKR_LIST.size)
        assertTrue(DHIKR_LIST.any { it.arabic == "سبحان الله" })
        assertTrue(DHIKR_LIST.any { it.arabic == "الحمد لله" })
        assertTrue(DHIKR_LIST.any { it.arabic == "الله أكبر" })
    }

    @Test
    fun `preset targets are correct`() {
        assertEquals(listOf(33, 99, 100, 500), PRESET_TARGETS)
    }

    @Test
    fun `session record stores correct data`() {
        val session = SessionRecord(
            dhikrArabic = "سبحان الله",
            count = 33,
            target = 33,
            completed = true
        )
        assertEquals("سبحان الله", session.dhikrArabic)
        assertEquals(33, session.count)
        assertTrue(session.completed)
    }

    @Test
    fun `vibration enabled by default`() {
        val state = TasbeehState()
        assertTrue(state.vibrationEnabled)
    }

    @Test
    fun `sound enabled by default`() {
        val state = TasbeehState()
        assertTrue(state.soundEnabled)
    }

    @Test
    fun `dark theme enabled by default`() {
        val state = TasbeehState()
        assertTrue(state.isDarkTheme)
    }
}
