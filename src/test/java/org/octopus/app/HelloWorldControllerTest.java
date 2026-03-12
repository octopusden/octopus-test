package org.octopus.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloWorldControllerTest {

    private final HelloWorldController controller = new HelloWorldController();

    @Test
    void shouldExecHello() throws Exception {
        assertEquals("Hello world", controller.helloWorld());
    }

    @Test
    void shouldExecUncoveredEndpoint() throws Exception {
        assertEquals("Uncovered method", controller.unCovered());
    }

}
