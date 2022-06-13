package com.github.zhuyb0614.mei.encryptors;

import com.github.zhuyb0614.mei.pojo.SourceBeanFieldValue;

import java.util.List;

/**
 * @author zhuyunbo
 */
public interface TypeSupportEncryptors<T> {
    /**
     * 加密单个对象
     *
     * @param parameterObject       待加密对象
     * @param isRemoveSource        是否清理明文字段
     * @param sourceBeanFieldValues 清理后的明文字段备份
     */
    void encrypt(T parameterObject, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues);

    /**
     * 批量加密多个对象
     *
     * @param parameterObject       待加密的对象集合
     * @param isRemoveSource        是否清理明文字段
     * @param sourceBeanFieldValues 清理后的明文字段备份
     */
    void encryptBatch(List<T> parameterObject, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues);

    /**
     * 解密单个对象
     *
     * @param resultObject
     */
    void decrypt(T resultObject);

    /**
     * 批量解密多个对象
     *
     * @param resultObject
     */
    void decryptBatch(List<T> resultObject);

    /**
     * 支持加解密的对象类型
     *
     * @return
     */
    Class<T> support();

}
