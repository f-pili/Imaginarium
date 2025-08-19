package it.fpili.imaginarium;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test to ensure the test framework (JUnit + Maven Surefire)
 * is correctly configured and executable within the project.
 */
class MainTest {

    /**
     * A trivial assertion that will always pass.
     * If this test fails, it indicates a problem with the
     * JUnit configuration or build system.
     */
    @Test
    void basicTest() {
        assertTrue(true, "JUnit is working correctly");
    }
}
