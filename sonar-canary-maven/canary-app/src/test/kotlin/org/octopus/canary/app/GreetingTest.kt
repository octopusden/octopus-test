package org.octopus.canary.app

import kotlin.test.Test
import kotlin.test.assertEquals

class GreetingTest {

    @Test
    fun `renders the release label from the core module`() {
        assertEquals("octopus-test 2.3.17", greeting("2.3", 17))
    }
}
