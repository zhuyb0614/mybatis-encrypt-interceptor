package com.github.zhuyb0614.mei.encryptor;

import java.util.List;
import java.util.Map;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/29 3:47 下午
 */
public interface StringEncryptor {

    String encryptString(String str);

    String decryptString(String str);

    Map<String, String> encryptStrings(List<String> strings);

    Map<String, String> decryptStrings(List<String> strings);

}
