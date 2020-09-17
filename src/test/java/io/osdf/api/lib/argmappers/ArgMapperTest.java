package io.osdf.api.lib.argmappers;

import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.annotations.Arg;
import io.osdf.api.lib.definitionparsers.MethodDefinitionParserImpl;
import io.osdf.api.lib.definitions.MethodDefinition;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static io.osdf.api.lib.argmappers.ArgMapper.argMapper;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgMapperTest {
    private final ArgMapper argMapper = argMapper();

    @Test
    void allParametersWithFlags() {
        assertArgMap(getMethodWithSecondOptional(), of("-f", "first", "-l", "a,b,c", "-s", "second"));
    }

    @Test
    void allParametersWithoutFlags() {
        assertArgMap(getMethodWithoutSecond(), of("first", "a", "b", "c"));
    }

    @Test
    void someParametersWithoutFlags() {
        assertArgMap(getMethodWithSecondOptional(), of("first", "a", "b", "c"));
    }

    @Test
    void combinedCall() {
        assertArgMap(getMethodWithSecondRequired(), of("first", "a", "b", "c", "-s", "second"));
    }

    @Test
    void checkValidationForParametersOutsidePlainCall() {
        MethodDefinition method = getMethodWithSecondRequired();
        assertThrows(ApiException.class, () -> argMapper.map(of("first", "a", "b", "c"), method));
    }

    @Arg(optional = "first")
    @Arg(optional = "list")
    @Arg(optional = "second")
    void methodWithSecondOptional(String first, List<String> list, String second) {
        assertEquals("first", first);
        assertEquals(of("a", "b", "c"), list);
        if (second != null) {
            assertEquals("second", second);
        }
    }

    @Arg(optional = "first")
    @Arg(optional = "list")
    @Arg(required = "second")
    void methodWithSecondRequired(String first, List<String> list, String second) {
        assertEquals("first", first);
        assertEquals("second", second);
        assertEquals(of("a", "b", "c"), list);
    }

    @Arg(optional = "first")
    @Arg(optional = "list")
    void methodWithoutSecond(String first, List<String> list) {
        assertEquals("first", first);
        assertEquals(of("a", "b", "c"), list);
    }

    @SneakyThrows
    private MethodDefinition getMethodWithSecondOptional() {
        Method method = ArgMapperTest.class.getDeclaredMethod("methodWithSecondOptional", String.class, List.class, String.class);
        return new MethodDefinitionParserImpl().parse(method);
    }

    @SneakyThrows
    private MethodDefinition getMethodWithSecondRequired() {
        Method method = ArgMapperTest.class.getDeclaredMethod("methodWithSecondRequired", String.class, List.class, String.class);
        return new MethodDefinitionParserImpl().parse(method);
    }

    @SneakyThrows
    private MethodDefinition getMethodWithoutSecond() {
        Method method = ArgMapperTest.class.getDeclaredMethod("methodWithoutSecond", String.class, List.class);
        return new MethodDefinitionParserImpl().parse(method);
    }

    private void assertArgMap(MethodDefinition method, List<String> rawArgs) {
        List<Object> args = argMapper.map(rawArgs, method);
        assertInvoke(method, args);
    }

    @SneakyThrows
    private void assertInvoke(MethodDefinition method, List<Object> args) {
        method.getMethod().invoke(this, args.toArray());
    }

}