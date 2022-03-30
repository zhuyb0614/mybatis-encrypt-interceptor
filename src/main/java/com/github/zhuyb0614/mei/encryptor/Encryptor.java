package com.github.zhuyb0614.mei.encryptor;

import java.util.List;

/**
 * @author zhuyunbo
 */
public interface Encryptor<T> {

    void encrypt(T parameterObject, boolean isRemoveSource);

    void encryptBatch(List<T> parameterObject, boolean isRemoveSource);

    void decrypt(T resultObject);

    void decryptBatch(List<T> resultObject);

    Class<T> support();

}
