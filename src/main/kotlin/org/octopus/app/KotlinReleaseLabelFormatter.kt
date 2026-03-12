package org.octopus.app

class KotlinReleaseLabelFormatter {
    fun format(component: String, version: String, stable: Boolean): String {
        val suffix = if (stable) "stable" else "candidate"
        return "$component:$version ($suffix)"
    }
}
