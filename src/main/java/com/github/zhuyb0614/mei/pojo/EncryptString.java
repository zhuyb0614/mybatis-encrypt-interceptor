package com.github.zhuyb0614.mei.pojo;

import com.github.zhuyb0614.mei.EncryptClass;
import com.github.zhuyb0614.mei.anno.EncryptField;
import lombok.Data;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/30 11:09 下午
 */
@Data
public class EncryptString implements EncryptClass {
    private String plainText;
    @EncryptField(sourceFiledName = "plainText")
    private String cipherText;

    public EncryptString(String plainText) {
        this.plainText = plainText;
    }
}
