package org.octopus.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinReleaseLabelFormatterTest {

    private val formatter = KotlinReleaseLabelFormatter()

    @Test
    fun shouldFormatStableLabel() {
        assertEquals("components-registry:2.1.0 (stable)", formatter.format("components-registry", "2.1.0", true))
    }

    @Test
    fun shouldFormatCandidateLabel() {
        assertEquals("components-registry:2.1.0-rc1 (candidate)", formatter.format("components-registry", "2.1.0-rc1", false))
    }
}
