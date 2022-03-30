package com.github.zhuyb0614.mei.encryptor;

import com.github.zhuyb0614.mei.entity.UserAuth;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.List;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/30 10:07 上午
 */
@Component
public class UserAuthEncryptor implements Encryptor<UserAuth> {

    @Override
    public void encrypt(UserAuth parameterObject, boolean isRemoveSource) {
        parameterObject.setEncryptIdentityNo(Base64Utils.encodeToString(parameterObject.getIdentityNo().getBytes()));
        if (isRemoveSource) {
            parameterObject.setIdentityNo(null);
        }
    }

    @Override
    public void encryptBatch(List<UserAuth> parameterObject, boolean isRemoveSource) {
        for (UserAuth userAuth : parameterObject) {
            encrypt(userAuth, isRemoveSource);
        }
    }

    @Override
    public void decrypt(UserAuth resultObject) {
        resultObject.setIdentityNo(new String(Base64Utils.decodeFromString(resultObject.getEncryptIdentityNo())));
    }

    @Override
    public void decryptBatch(List<UserAuth> resultObject) {
        for (UserAuth userAuth : resultObject) {
            decrypt(userAuth);
        }
    }

    @Override
    public Class<UserAuth> support() {
        return UserAuth.class;
    }
}
