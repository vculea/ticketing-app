package ro.fruit.packeting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaVersionTest {
    @Test
    void usesJava25() {
        assertEquals(25, Runtime.version().feature());
    }
}
