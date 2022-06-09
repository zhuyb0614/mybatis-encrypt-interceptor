package com.github.zhuyb0614.mei.encryptors;

import java.util.List;
import java.util.Map;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/29 3:47 下午
 */
public interface StringEncryptors {

    String encryptString(Object obj);

    Object decryptString(String str);

    Map<Object, String> encryptStrings(List<Object> objects);

    Map<String, Object> decryptStrings(List<String> strings);

}
