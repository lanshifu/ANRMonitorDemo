package com.lizhi.smartlife.mynativeapplication;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {
    private ReflectUtil() {
    }

    public static <T> T newInstance(Class<T> ofClass, Class<?>[] argTypes, Object[] args) {
        try {
            Constructor<T> con = ofClass.getConstructor(argTypes);
            return con.newInstance(args);
        } catch (Exception var4) {
            throwBuildException(var4);
            return null;
        }
    }

    public static Object invoke(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName, (Class[])null);
            return method.invoke(obj, (Object[])null);
        } catch (Exception var3) {
            throwBuildException(var3);
            return null;
        }
    }

    public static Object invokeStatic(Object obj, String methodName) {
        try {
            Method method = ((Class)obj).getMethod(methodName, (Class[])null);
            return method.invoke(obj, (Object[])null);
        } catch (Exception var3) {
            throwBuildException(var3);
            return null;
        }
    }

    public static Object invoke(Object obj, String methodName, Class<?> argType, Object arg) {
        try {
            Method method = obj.getClass().getMethod(methodName, argType);
            return method.invoke(obj, arg);
        } catch (Exception var5) {
            throwBuildException(var5);
            return null;
        }
    }

    public static Object invoke(Object obj, String methodName, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2) {
        try {
            Method method = obj.getClass().getMethod(methodName, argType1, argType2);
            return method.invoke(obj, arg1, arg2);
        } catch (Exception var7) {
            throwBuildException(var7);
            return null;
        }
    }

    public static Object getField(Object obj, String fieldName){
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception var3) {
            return null;
        }
    }

    public static void throwBuildException(Exception t){
    }

}
