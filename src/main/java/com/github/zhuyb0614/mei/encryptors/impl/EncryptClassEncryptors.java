package com.github.zhuyb0614.mei.encryptors.impl;

import com.github.zhuyb0614.mei.EncryptClass;
import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.anno.EncryptField;
import com.github.zhuyb0614.mei.encryptors.TypeSupportEncryptors;
import com.github.zhuyb0614.mei.encryptors.PrimitiveEncryptors;
import com.github.zhuyb0614.mei.pojo.SourceBeanFieldValue;
import com.github.zhuyb0614.mei.utils.LoopLimit;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 被EncryptClass标识的实现类加密器
 * @author zhuyunbo
 */
@Slf4j
public class EncryptClassEncryptors implements TypeSupportEncryptors<EncryptClass> {

    protected MeiProperties meiProperties;
    private PrimitiveEncryptors stringEncryptDecrypt;

    public EncryptClassEncryptors(MeiProperties meiProperties, PrimitiveEncryptors stringEncryptDecrypt) {
        this.meiProperties = meiProperties;
        this.stringEncryptDecrypt = stringEncryptDecrypt;
    }

    @Override
    public void encrypt(EncryptClass parameterObject, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues) {
        Class<?> aClass = parameterObject.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                if (annotation != null) {
                    Field encryptField = declaredField;
                    Field sourceFiled = aClass.getDeclaredField(annotation.sourceFiledName());
                    sourceFiled.setAccessible(true);
                    Object sourceTxt = sourceFiled.get(parameterObject);
                    if (sourceTxt != null && sourceTxt.toString().length() > 0) {
                        encryptField.setAccessible(true);
                        encryptField.set(parameterObject, stringEncryptDecrypt.encryptString(sourceTxt));
                        if (isRemoveSource) {
                            setFieldNull(parameterObject, sourceFiled);
                            sourceBeanFieldValues.add(new SourceBeanFieldValue(parameterObject, sourceFiled, sourceTxt));
                        }
                    }
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException e) {
                log.error("encrypt {} error", parameterObject, e);
            }
        }
    }

    /**
     * 调用set方法将字段赋值null
     * 方法查找规则为set+小驼峰转大驼峰,请注意set方法格式
     * 如 字段phone,将反射调用obj.setPhone(null);
     *
     * @param parameterObject
     * @param field
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setFieldNull(EncryptClass parameterObject, Field field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method setSourceFieldMethod = parameterObject.getClass().getMethod("set" + CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL).convert(field.getName()), field.getType());
        setSourceFieldMethod.invoke(parameterObject, (Object) null);
    }

    @Override
    public void encryptBatch(List<EncryptClass> parameterObject, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues) {
        if (parameterObject.size() == 1) {
            encrypt(parameterObject.get(0), isRemoveSource, sourceBeanFieldValues);
            return;
        }
        List<Object> sourceTxtList = getSourceTxtList(parameterObject);
        Map<Object, String> sourceTxtEncryptTxtMap = Maps.newHashMapWithExpectedSize(sourceTxtList.size());
        LoopLimit.loop(meiProperties.getBatchSize(), sourceTxtList, (strings -> {
            sourceTxtEncryptTxtMap.putAll(stringEncryptDecrypt.encryptStrings(strings));
        }));
        setEncryptTxt(parameterObject, sourceTxtEncryptTxtMap, isRemoveSource, sourceBeanFieldValues);
    }


    @Override
    public void decrypt(EncryptClass resultObject) {
        Class<?> aClass = resultObject.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                if (annotation != null) {
                    declaredField.setAccessible(true);
                    Object encryptTxt = declaredField.get(resultObject);
                    if (encryptTxt != null && encryptTxt.toString().length() > 0) {
                        Object sourceObject = stringEncryptDecrypt.decryptString(encryptTxt.toString());
                        setSource(resultObject, annotation, sourceObject);
                    }
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log.error("decrypt {} error", resultObject, e);
            }
        }
    }


    @Override
    public void decryptBatch(List<EncryptClass> resultObject) {
        if (resultObject.size() == 1) {
            decrypt(resultObject.get(0));
            return;
        }
        List<String> encryptTxtList = getEncryptTxtList(resultObject);
        Map<String, Object> encryptTxtSourceTxtMap = Maps.newHashMapWithExpectedSize(encryptTxtList.size());
        LoopLimit.loop(meiProperties.getBatchSize(), encryptTxtList, (strings -> {
            encryptTxtSourceTxtMap.putAll(stringEncryptDecrypt.decryptStrings(strings));
        }));
        setSourceTxt(resultObject, encryptTxtSourceTxtMap);
    }

    @Override
    public Class<EncryptClass> support() {
        return EncryptClass.class;
    }


    private void setEncryptTxt(List<EncryptClass> parameterObject, Map<Object, String> sourceTxtEncryptTxtMap, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues) {
        for (EncryptClass obj : parameterObject) {
            Class<?> aClass = obj.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                    if (annotation != null) {
                        Field encryptField = declaredField;
                        Field sourceFiled = aClass.getDeclaredField(annotation.sourceFiledName());
                        sourceFiled.setAccessible(true);
                        Object sourceTxt = sourceFiled.get(obj);
                        if (sourceTxt != null) {
                            encryptField.setAccessible(true);
                            encryptField.set(obj, sourceTxtEncryptTxtMap.get(sourceTxt));
                            if (isRemoveSource) {
                                setFieldNull(obj, sourceFiled);
                                sourceBeanFieldValues.add(new SourceBeanFieldValue(obj, sourceFiled, sourceTxt));
                            }
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.error("setEncryptTxt {} error", obj, e);
                }
            }
        }
    }

    private void setSourceTxt(List<EncryptClass> parameterObject, Map<String, Object> encryptTxtSourceTxtMap) {
        for (EncryptClass obj : parameterObject) {
            Class<?> aClass = obj.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                    if (annotation != null) {
                        Field encryptField = declaredField;
                        encryptField.setAccessible(true);
                        Object encryptTxt = encryptField.get(obj);
                        if (encryptTxt != null && encryptTxt.toString().length() > 0) {
                            setSource(obj, annotation, encryptTxtSourceTxtMap.get(encryptTxt));
                        }
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.error("setSourceTxt {} error", obj, e);
                }
            }

        }
    }

    private List<Object> getSourceTxtList(List<EncryptClass> parameterObject) {
        Set<Object> sourceTxtList = new HashSet<>();
        for (EncryptClass obj : parameterObject) {
            Class<?> aClass = obj.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                    if (annotation != null) {
                        Object sourceTxt = getSourceTxt(obj, annotation);
                        if (sourceTxt != null && sourceTxt.toString().length() > 0) {
                            sourceTxtList.add(sourceTxt);
                        }
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.error("getSourceTxt {} error", obj, e);
                }
            }
        }
        return new ArrayList<>(sourceTxtList);
    }


    private List<String> getEncryptTxtList(List<EncryptClass> parameterObject) {
        Set<String> encryptTxtList = new HashSet<>();
        for (Object obj : parameterObject) {
            Class<?> aClass = obj.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                    if (annotation != null) {
                        Field encryptField = declaredField;
                        encryptField.setAccessible(true);
                        Object encryptTxt = encryptField.get(obj);
                        if (encryptTxt != null && encryptTxt.toString().length() > 0) {
                            encryptTxtList.add(encryptTxt.toString());
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.error("getEncryptTxt {} error", obj, e);
                }
            }
        }
        return new ArrayList<>(encryptTxtList);
    }

    private void setSource(EncryptClass resultObject, EncryptField annotation, Object sourceObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String sourceFiledSetMethod = annotation.sourceFiledSetMethod();
        if (StringUtils.isEmpty(sourceFiledSetMethod)) {
            sourceFiledSetMethod = String.format("set%s", CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL).convert(annotation.sourceFiledName()));
        }
        Method setMethod = resultObject.getClass().getMethod(sourceFiledSetMethod, annotation.sourceFiledType());
        setMethod.invoke(resultObject, sourceObject);
    }

    private Object getSourceTxt(EncryptClass obj, EncryptField annotation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String sourceFiledGetMethod = annotation.sourceFiledGetMethod();
        if (StringUtils.isEmpty(sourceFiledGetMethod)) {
            sourceFiledGetMethod = String.format("get%s", CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL).convert(annotation.sourceFiledName()));
        }
        Method getMethod = obj.getClass().getMethod(sourceFiledGetMethod);
        return getMethod.invoke(obj);
    }
}
