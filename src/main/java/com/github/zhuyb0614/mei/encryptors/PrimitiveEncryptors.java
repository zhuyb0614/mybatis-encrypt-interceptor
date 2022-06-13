package com.github.zhuyb0614.mei.encryptors;

import java.util.List;
import java.util.Map;

/**
 * 基本类型加密器
 *
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/29 3:47 下午
 */
public interface PrimitiveEncryptors {
    /**
     * 加密单个数据
     *
     * @param obj
     * @return
     */
    String encryptString(Object obj);

    /**
     * 解密单个数据
     *
     * @param str
     * @return
     */
    Object decryptString(String str);

    /**
     * 批量加密多个数据
     *
     * @param objects
     * @return
     */
    Map<Object, String> encryptStrings(List<Object> objects);

    /**
     * 批量解密多个数据
     *
     * @param strings
     * @return
     */
    Map<String, Object> decryptStrings(List<String> strings);

}
