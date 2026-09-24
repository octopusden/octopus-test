package org.octopus.canary.app

import org.octopus.canary.core.ReleaseLine
import org.octopus.canary.core.label

fun greeting(line: String, build: Int): String = "octopus-test ${label(ReleaseLine(line), build)}"
