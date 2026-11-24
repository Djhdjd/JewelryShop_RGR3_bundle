package com.example.jewelryshop.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShopBenchmark {
    @get:Rule val rule = MacrobenchmarkRule()
    private val target = "com.example.jewelryshop"
    @Test fun coldStart() = rule.measureRepeated(packageName = target, metrics = listOf(StartupTimingMetric()), compilationMode = CompilationMode.None(), startupMode = StartupMode.COLD, iterations = 5) { pressHome(); startActivityAndWait() }
    @Test fun catalogScroll() = rule.measureRepeated(
        packageName = target, metrics = listOf(FrameTimingMetric()), iterations = 5,
        compilationMode = CompilationMode.None(), startupMode = null,
        setupBlock = { pressHome(); startActivityAndWait() },
    ) {
        val list = device.wait(Until.findObject(By.res(target, "catalogScroll")), 5_000) ?: error("Catalog scroll not found")
        repeat(3) { list.scroll(Direction.DOWN, 0.7f); list.scroll(Direction.UP, 0.7f) }
    }
}
