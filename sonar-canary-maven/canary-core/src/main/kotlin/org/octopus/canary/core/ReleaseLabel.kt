package org.octopus.canary.core

fun label(line: ReleaseLine, build: Int): String = "${line.value()}.$build"
