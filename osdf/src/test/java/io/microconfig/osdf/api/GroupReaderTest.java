package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleApiClass;
import io.microconfig.osdf.exceptions.OSDFException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static io.microconfig.osdf.api.ApiReader.reader;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupReaderTest {
    private final Method exampleMethod = getMethod("example");
    private final Method camelCaseMethod = getMethod("camelCase");
    private final ApiReader reader = reader(ExampleApiClass.class);

    @Test
    void methodByName() {
        Method fromReader = reader.methodByName("example");
        assertEquals(exampleMethod, fromReader);

        assertThrows(OSDFException.class, () -> reader.methodByName("unknownMethod"));
    }

    @Test
    void testDashedMethodNameCall() {
        Method fromReader = reader.methodByName("camel-case");
        assertEquals(camelCaseMethod, fromReader);
    }

    @Test
    void methods() {
        List<Method> methods = reader.methods();
        assertEquals(of(exampleMethod, camelCaseMethod), methods);
    }

    private Method getMethod(String name) {
        try {
            return ExampleApiClass.class.getMethod(name, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Example method not found");
        }
    }
}