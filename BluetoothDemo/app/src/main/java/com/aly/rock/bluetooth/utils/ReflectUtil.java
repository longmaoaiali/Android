package com.aly.rock.bluetooth.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * create by likangning @ 2019-07-17
 */
public class ReflectUtil {
    public static <T extends Object> T receiveField(Object obj, String declared)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(declared);
        field.setAccessible(true);
        return (T) field.get(obj);
    }

    public static <T extends Object> T invokeMethod(Object obj, String methodName, Object... args) {
        try {
            Class[] cls = null;
            if (null != args) {
                cls = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    cls[i] = args[i].getClass();
                }
            }
            Method method = obj.getClass().getMethod(methodName, cls);
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static <T extends Object> T invokeMethodWithParam(Object target, String methodName,
                                                             boolean staticMethod,
                                                             boolean needReturnValue,
                                                             Class[] params, Object[] paramsValue) {
        try {
            Method declaredMethod;

            if (target instanceof Class) {
                declaredMethod = ((Class) target).getDeclaredMethod(methodName, params);
            } else {
                declaredMethod = target.getClass().getDeclaredMethod(methodName, params);
            }
            if (declaredMethod != null) {
                declaredMethod.setAccessible(true);
                Object result = declaredMethod.invoke(staticMethod ? null : target, paramsValue);
                return needReturnValue ? (T) result : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
