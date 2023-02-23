package com.ruoyi.oacommon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * 自定义反射工具类
 */
public class ReflectionMyUtils {
    
    /**
     * 修改方法上注解的成员变量
     * @param memberNames 成员变量名称列表
     * @param memberValues 成员变量值列表
     * @param annotationClass 注解类型
     * @param clazz 类
     * @param methodName 方法名
     * @param parameterTypes 方法参数类型
     */
    public static void modifyAnnotationAttribute(List<String> memberNames, List<String> memberValues, Class<? extends Annotation> annotationClass, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            Annotation curLog = method.getAnnotation(annotationClass);
            InvocationHandler handler = Proxy.getInvocationHandler(curLog);
            Field hField = handler.getClass().getDeclaredField("memberValues");
            hField.setAccessible(true);
            Map memberValuesMap = (Map) hField.get(handler);
            for (int i = 0; i < memberNames.size(); i++) {
                memberValuesMap.put(memberNames.get(i), memberValues.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 修改方法上注解的成员变量
     * @param memberName 成员变量名称
     * @param memberValue 成员变量值
     * @param annotationClass 注解类型
     * @param clazz 类
     * @param methodName 方法名
     * @param parameterTypes 方法参数类型
     */
    public static void modifyAnnotationAttribute(String memberName, String memberValue, Class<? extends Annotation> annotationClass, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            Annotation curLog = method.getAnnotation(annotationClass);
            InvocationHandler handler = Proxy.getInvocationHandler(curLog);
            Field hField = handler.getClass().getDeclaredField("memberValues");
            hField.setAccessible(true);
            Map memberValuesMap = (Map) hField.get(handler);
            memberValuesMap.put(memberName, memberValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //TODO implement a function to return all members of an Annotaion.
    
    //TODO implement a function to return all function refs that uses an Annotation.
    
}
