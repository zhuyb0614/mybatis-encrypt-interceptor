package com.github.zhuyb0614.mei.encryptors;

import com.github.zhuyb0614.mei.pojo.SourceBeanFieldValue;

import java.util.List;

/**
 * @author zhuyunbo
 */
public interface Encryptors<T> {

    void encrypt(T parameterObject, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues);

    void encryptBatch(List<T> parameterObject, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues);

    void decrypt(T resultObject);

    void decryptBatch(List<T> resultObject);

    Class<T> support();

}
