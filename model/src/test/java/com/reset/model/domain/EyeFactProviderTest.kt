package com.reset.model.domain

import com.reset.model.domain.EyeFactProvider
import com.reset.model.domain.model.EyeFact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EyeFactProviderTest {

    @Test
    fun `picks the index produced by the injected random`() {
        val seed = 99
        val expected = Random(seed).nextInt(EyeFact.COUNT)

        val provider = EyeFactProvider(Random(seed))

        assertEquals(expected, provider.random().index)
    }

    @Test
    fun `always returns an in-bounds index`() {
        val provider = EyeFactProvider(Random(0))

        repeat(200) {
            val index = provider.random().index
            assertTrue("index $index out of bounds", index in 0 until EyeFact.COUNT)
        }
    }
}
