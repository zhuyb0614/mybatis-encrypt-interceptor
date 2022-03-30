package com.github.zhuyb0614.mei.entity;

import com.github.zhuyb0614.mei.EncryptClass;
import com.github.zhuyb0614.mei.anno.EncryptField;
import lombok.Data;
import lombok.ToString;

/**
 * 主要是为了单测用,可以直接修改User
 */
@Data
@ToString(callSuper = true)
public class EncryptUser implements EncryptClass {
    private Integer id;
    private String name;
    @EncryptField(sourceFiledName = "name")
    private String encryptName;
}
