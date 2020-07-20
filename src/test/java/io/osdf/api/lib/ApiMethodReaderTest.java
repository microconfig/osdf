package io.osdf.api.lib;

import io.osdf.api.lib.example.ExampleApiClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static io.osdf.api.lib.ApiMethodReader.apiMethodReader;

class ApiMethodReaderTest {

    @Test
    void successTest() throws NoSuchMethodException {
        Method method = ExampleApiClass.class.getMethod("example", String.class);

        ApiMethodReader apiMethodReader = apiMethodReader(method);
        apiMethodReader.description();
        apiMethodReader.printHelp();
    }
}