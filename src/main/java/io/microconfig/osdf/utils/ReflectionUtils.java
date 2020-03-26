package io.microconfig.osdf.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

public class ReflectionUtils {
    @SuppressWarnings("Convert2MethodRef")
    public static <T> List<T> annotations(Method method, Class<? extends T> annotationClass) {
        List<T> annotations = new ArrayList<>();
        processAnnotation(method, annotationClass, param -> annotations.add(param));
        return annotations;
    }

    public static <T> void processAnnotation(Method method, Class<? extends T> annotationClazz, Consumer<? super T> process) {
        for (Annotation[] parameterAnnotation : method.getParameterAnnotations()) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotationClazz.isInstance(annotation)) {
                    T annotationInstance = annotationClazz.cast(annotation);
                    process.accept(annotationInstance);
                }
            }
        }
    }

    public static <T> void processAnnotation(Method method, Class<? extends T> annotationClazz, ObjIntConsumer<? super T> process) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; ++i) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotationClazz.isInstance(annotation)) {
                    T annotationInstance = annotationClazz.cast(annotation);
                    process.accept(annotationInstance, i);
                }
            }
        }
    }
}
