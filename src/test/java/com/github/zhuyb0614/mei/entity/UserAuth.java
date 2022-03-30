package com.github.zhuyb0614.mei.entity;

import com.github.zhuyb0614.mei.EncryptClass;
import lombok.Data;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/30 10:55 上午
 */
@Data
public class UserAuth implements EncryptClass {
    private Integer userId;
    private String identityNo;
    private String encryptIdentityNo;

}
