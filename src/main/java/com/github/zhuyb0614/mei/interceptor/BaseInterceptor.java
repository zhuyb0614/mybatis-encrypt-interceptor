package com.github.zhuyb0614.mei.interceptor;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptor.Encryptor;
import org.apache.ibatis.plugin.Interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhuyunbo
 */
public abstract class BaseInterceptor implements Interceptor {
    protected MeiProperties meiProperties;
    protected List<Encryptor> encryptors;
    private Map<Class, Optional<Encryptor>> encryptorMap;

    public BaseInterceptor(MeiProperties meiProperties, List<Encryptor> encryptors) {
        this.meiProperties = meiProperties;
        this.encryptors = encryptors;
        this.encryptorMap = new HashMap<>();
    }

    protected Optional<Encryptor> chooseEncryptor(Class parameterClass) {
        Optional<Encryptor> encryptorOptional = encryptorMap.get(parameterClass);
        if (encryptorOptional != null) {
            return encryptorOptional;
        }
        for (Encryptor encryptor : encryptors) {
            if (encryptor.support().isAssignableFrom(parameterClass)) {
                encryptorOptional = Optional.of(encryptor);
                encryptorMap.put(parameterClass, encryptorOptional);
                return encryptorOptional;
            }
        }
        return Optional.empty();
    }
}
