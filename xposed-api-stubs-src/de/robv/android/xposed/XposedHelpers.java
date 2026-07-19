package de.robv.android.xposed;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class XposedHelpers {
    private XposedHelpers() {
    }

    public static XC_MethodHook.Unhook findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        return new XC_MethodHook.Unhook();
    }

    public static Class<?> findClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        ClassLoader loader = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
        if (loader != null) {
            return Class.forName(className, false, loader);
        }
        return Class.forName(className);
    }

    public static Object callMethod(Object obj, String methodName, Object... args) throws ReflectiveOperationException {
        Class<?> clazz = obj.getClass();
        Method target = null;
        for (Method method : clazz.getMethods()) {
            if (!method.getName().equals(methodName)) {
                continue;
            }
            if (method.getParameterCount() != args.length) {
                continue;
            }
            target = method;
            break;
        }
        if (target == null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }
                if (method.getParameterCount() != args.length) {
                    continue;
                }
                target = method;
                break;
            }
        }
        if (target == null) {
            throw new NoSuchMethodException(methodName);
        }
        target.setAccessible(true);
        return target.invoke(obj, args);
    }

    public static Object getObjectField(Object obj, String fieldName) throws ReflectiveOperationException {
        Field field = findField(obj.getClass(), fieldName);
        return field.get(obj);
    }

    public static int getIntField(Object obj, String fieldName) throws ReflectiveOperationException {
        Field field = findField(obj.getClass(), fieldName);
        return field.getInt(obj);
    }

    public static void setObjectField(Object obj, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = findField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
