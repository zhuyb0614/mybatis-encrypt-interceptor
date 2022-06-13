package com.github.zhuyb0614.mei.interceptor;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptors.TypeSupportEncryptors;
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
    protected List<TypeSupportEncryptors> encryptors;
    private Map<Class, TypeSupportEncryptors> encryptorsMap;

    public BaseInterceptor(MeiProperties meiProperties, List<TypeSupportEncryptors> encryptors) {
        this.meiProperties = meiProperties;
        this.encryptors = encryptors;
        this.encryptorsMap = new HashMap<>();
    }

    protected Optional<TypeSupportEncryptors> chooseEncryptors(Class parameterClass) {
        TypeSupportEncryptors encryptors = encryptorsMap.get(parameterClass);
        if (encryptors != null) {
            return Optional.of(encryptors);
        }
        for (TypeSupportEncryptors typeSupportEncryptors : this.encryptors) {
            if (typeSupportEncryptors.support().isAssignableFrom(parameterClass)) {
                encryptorsMap.put(parameterClass, typeSupportEncryptors);
                return Optional.of(typeSupportEncryptors);
            }
        }
        return Optional.empty();
    }
}
