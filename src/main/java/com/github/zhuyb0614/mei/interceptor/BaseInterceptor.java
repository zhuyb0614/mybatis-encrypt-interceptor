package com.github.zhuyb0614.mei.interceptor;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptors.Encryptors;
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
    protected List<Encryptors> encryptors;
    private Map<Class, Optional<Encryptors>> encryptorMap;

    public BaseInterceptor(MeiProperties meiProperties, List<Encryptors> encryptors) {
        this.meiProperties = meiProperties;
        this.encryptors = encryptors;
        this.encryptorMap = new HashMap<>();
    }

    protected Optional<Encryptors> chooseEncryptors(Class parameterClass) {
        Optional<Encryptors> encryptorsOptional = encryptorMap.get(parameterClass);
        if (encryptorsOptional != null) {
            return encryptorsOptional;
        }
        for (Encryptors encryptors : this.encryptors) {
            if (encryptors.support().isAssignableFrom(parameterClass)) {
                encryptorsOptional = Optional.of(encryptors);
                encryptorMap.put(parameterClass, encryptorsOptional);
                return encryptorsOptional;
            }
        }
        return Optional.empty();
    }
}
