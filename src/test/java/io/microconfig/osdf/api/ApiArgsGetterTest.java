package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.parameters.AbstractParameter;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static io.microconfig.osdf.api.ApiArgsGetter.argsGetter;
import static io.microconfig.osdf.parameters.ParamType.REQUIRED;
import static io.microconfig.osdf.parameters.ParameterUtils.toList;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiArgsGetterTest {
    public static class FirstString extends AbstractParameter<String> {
        public FirstString() {
            super("first", "f", "description");
        }
    }

    public static class SecondString extends AbstractParameter<String> {
        public SecondString() {
            super("second", "s", "description");
        }
    }

    public static class ListParameter extends AbstractParameter<List<String>> {
        public ListParameter() {
            super("list", "l", "description");
        }
        @Override
        public List<String> get() {
            return toList(getValue());
        }
    }

    void methodWithSecondOptional(@ConsoleParam(FirstString.class) String first,
                                  @ConsoleParam(ListParameter.class) List<String> list,
                                  @ConsoleParam(SecondString.class) String second) {
        assertEquals(first, "first");
        assertEquals(list, of("a", "b", "c"));
        if (second != null) {
            assertEquals(second, "second");
        }
    }

    void methodWithSecondRequired(@ConsoleParam(FirstString.class) String first,
                                  @ConsoleParam(ListParameter.class) List<String> list,
                                  @ConsoleParam(value = SecondString.class, type = REQUIRED) String second) {
        assertEquals(first, "first");
        assertEquals(second, "second");
        assertEquals(list, of("a", "b", "c"));
    }

    void methodWithoutSecond(@ConsoleParam(FirstString.class) String first,
                             @ConsoleParam(ListParameter.class) List<String> list) {
        assertEquals(first, "first");
        assertEquals(list, of("a", "b", "c"));
    }

    @Test
    void allParametersWithFlags() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Method testMethod = getMethodWithSecondOptional();
        ApiArgsGetter apiArgsGetter = argsGetter(testMethod, new String[]{"-f", "first", "-l", "a,b,c", "-s", "second"});
        testMethod.invoke(this, apiArgsGetter.get());
    }

    @Test
    void allParametersWithoutFlags() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Method testMethod = getMethodWithoutSecond();
        ApiArgsGetter apiArgsGetter = argsGetter(testMethod, new String[]{"first", "a", "b", "c"});
        testMethod.invoke(this, apiArgsGetter.get());
    }

    @Test
    void someParametersWithoutFlags() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Method testMethod = getMethodWithSecondOptional();
        ApiArgsGetter apiArgsGetter = argsGetter(testMethod, new String[]{"first", "a", "b", "c"});
        testMethod.invoke(this, apiArgsGetter.get());
    }

    @Test
    void checkValidationForParametersOutsidePlainCall() throws NoSuchMethodException {
        Method testMethod = getMethodWithSecondRequired();
        ApiArgsGetter apiArgsGetter = argsGetter(testMethod, new String[]{"first", "a", "b", "c"});
        assertThrows(ParseException.class, apiArgsGetter::get);
    }

    @Test
    void combinedCall() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ParseException {
        Method testMethod = getMethodWithSecondRequired();
        ApiArgsGetter apiArgsGetter = argsGetter(testMethod, new String[]{"first", "a", "b", "c", "-s", "second"});
        testMethod.invoke(this, apiArgsGetter.get());
    }

    private Method getMethodWithSecondOptional() throws NoSuchMethodException {
        return ApiArgsGetterTest.class.getDeclaredMethod("methodWithSecondOptional", String.class, List.class, String.class);
    }

    private Method getMethodWithSecondRequired() throws NoSuchMethodException {
        return ApiArgsGetterTest.class.getDeclaredMethod("methodWithSecondRequired", String.class, List.class, String.class);
    }

    private Method getMethodWithoutSecond() throws NoSuchMethodException {
        return ApiArgsGetterTest.class.getDeclaredMethod("methodWithoutSecond", String.class, List.class);
    }
}