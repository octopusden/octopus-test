package org.octopus.canary.core

import kotlin.test.Test
import kotlin.test.assertEquals

class ReleaseLabelTest {

    @Test
    fun `joins the line and the build number`() {
        assertEquals("2.3.17", label(ReleaseLine("2.3"), 17))
    }
}
