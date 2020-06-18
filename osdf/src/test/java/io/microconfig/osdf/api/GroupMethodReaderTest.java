package io.microconfig.osdf.api;

import io.microconfig.osdf.api.example.ExampleApiClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static io.microconfig.osdf.api.ApiMethodReader.apiMethodReader;

class GroupMethodReaderTest {

    @Test
    void successTest() throws NoSuchMethodException {
        Method method = ExampleApiClass.class.getMethod("example", String.class);

        ApiMethodReader apiMethodReader = apiMethodReader(method);
        apiMethodReader.description();
        apiMethodReader.printHelp();
    }
}