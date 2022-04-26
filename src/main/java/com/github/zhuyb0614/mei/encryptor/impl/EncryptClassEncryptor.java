package com.github.zhuyb0614.mei.encryptor.impl;

import com.github.zhuyb0614.mei.EncryptClass;
import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.anno.EncryptField;
import com.github.zhuyb0614.mei.encryptor.Encryptor;
import com.github.zhuyb0614.mei.encryptor.StringEncryptor;
import com.github.zhuyb0614.mei.utils.LoopLimit;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class EncryptClassEncryptor implements Encryptor<EncryptClass> {

    protected MeiProperties meiProperties;
    private StringEncryptor stringEncryptDecrypt;

    public EncryptClassEncryptor(MeiProperties meiProperties, StringEncryptor stringEncryptDecrypt) {
        this.meiProperties = meiProperties;
        this.stringEncryptDecrypt = stringEncryptDecrypt;
    }

    @Override
    public void encrypt(EncryptClass parameterObject, boolean isRemoveSource) {
        Class<?> aClass = parameterObject.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                if (annotation != null) {
                    Field encryptFiled = declaredField;
                    Object sourceTxt = getSourceTxt(parameterObject, annotation);
                    if (sourceTxt != null && sourceTxt.toString().length() > 0) {
                        encryptFiled.setAccessible(true);
                        encryptFiled.set(parameterObject, stringEncryptDecrypt.encryptString(sourceTxt.toString()));
                        if (isRemoveSource) {
                            setSourceTxt(parameterObject, annotation, null);
                        }
                    }
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log.error("encrypt {} error", parameterObject, e);
            }
        }
    }


    @Override
    public void encryptBatch(List<EncryptClass> parameterObject, boolean isRemoveSource) {
        if (parameterObject.size() == 1) {
            encrypt(parameterObject.get(0), isRemoveSource);
            return;
        }
        List<String> sourceTxtList = getSourceTxtList(parameterObject);
        Map<String, String> sourceTxtEncryptTxtMap = Maps.newHashMapWithExpectedSize(sourceTxtList.size());
        LoopLimit.loop(meiProperties.getBatchSize(), sourceTxtList, (strings -> {
            sourceTxtEncryptTxtMap.putAll(stringEncryptDecrypt.encryptStrings(strings));
        }));
        setEncryptTxt(parameterObject, sourceTxtEncryptTxtMap, isRemoveSource);    }


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
                        String decryptString = stringEncryptDecrypt.decryptString(encryptTxt.toString());
                        setSourceTxt(resultObject, annotation, decryptString);
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
        Map<String, String> encryptTxtSourceTxtMap = Maps.newHashMapWithExpectedSize(encryptTxtList.size());
        LoopLimit.loop(meiProperties.getBatchSize(), encryptTxtList, (strings -> {
            encryptTxtSourceTxtMap.putAll(stringEncryptDecrypt.decryptStrings(strings));
        }));
        setSourceTxt(resultObject, encryptTxtSourceTxtMap);    }

    @Override
    public Class<EncryptClass> support() {
        return EncryptClass.class;
    }


    private void setEncryptTxt(List<EncryptClass> parameterObject, Map<String, String> sourceTxtEncryptTxtMap, boolean isRemoveSource) {
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
                                setSourceTxt(obj, annotation, null);
                            }
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.error("setEncryptTxt {} error", obj, e);
                }
            }
        }
    }

    private void setSourceTxt(List<EncryptClass> parameterObject, Map<String, String> encryptTxtSourceTxtMap) {
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
                            setSourceTxt(obj, annotation, encryptTxtSourceTxtMap.get(encryptTxt));
                        }
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    log.error("setSourceTxt {} error", obj, e);
                }
            }

        }
    }

    private List<String> getSourceTxtList(List<EncryptClass> parameterObject) {
        Set<String> sourceTxtList = new HashSet<>();
        for (EncryptClass obj : parameterObject) {
            Class<?> aClass = obj.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    EncryptField annotation = declaredField.getAnnotation(EncryptField.class);
                    if (annotation != null) {
                        Object sourceTxt = getSourceTxt(obj, annotation);
                        if (sourceTxt != null && sourceTxt.toString().length() > 0) {
                            sourceTxtList.add(sourceTxt.toString());
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

    private void setSourceTxt(EncryptClass resultObject, EncryptField annotation, String decryptString) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String sourceFiledSetMethod = annotation.sourceFiledSetMethod();
        if (StringUtils.isEmpty(sourceFiledSetMethod)) {
            sourceFiledSetMethod = String.format("set%s", CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL).convert(annotation.sourceFiledName()));
        }
        Method setMethod = resultObject.getClass().getMethod(sourceFiledSetMethod, annotation.sourceFiledType());
        setMethod.invoke(resultObject, decryptString);
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
