package org.octopus.app;

import java.util.Random;

/**
 * Deliberately defective, so the SonarCloud pull-request report has real Java to show.
 * Delete with the rest of this verification branch.
 */
public class SonarProbe {

    /** Vulnerability: a credential in source. */
    private static final String TOKEN = "not-a-real-token-please-delete";

    /** Bug: reference equality on strings. */
    public boolean isRelease(String label) {
        return label == "release";
    }

    /** Security hotspot: java.util.Random is predictable. */
    public int weakId() {
        return new Random().nextInt(100);
    }

    /** Code smells: collapsible if, console output, unused local. */
    public void describe(String name, boolean verbose) {
        int unused = 1;
        if (verbose) {
            if (name != null) {
                System.out.println(name + " " + TOKEN);
            }
        }
    }
}
