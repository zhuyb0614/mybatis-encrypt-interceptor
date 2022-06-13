package com.github.zhuyb0614.mei;


import com.github.zhuyb0614.mei.encryptors.TypeSupportEncryptors;
import com.github.zhuyb0614.mei.encryptors.PrimitiveEncryptors;
import com.github.zhuyb0614.mei.encryptors.impl.CachePrimitiveEncryptors;
import com.github.zhuyb0614.mei.encryptors.impl.EncryptClassEncryptors;
import com.github.zhuyb0614.mei.encryptors.impl.ReversePrimitiveEncryptors;
import com.github.zhuyb0614.mei.interceptor.EncryptParameterInterceptor;
import com.github.zhuyb0614.mei.interceptor.EncryptResultInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuyunbo
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "mei.open-switch", havingValue = "on", matchIfMissing = true)
@Slf4j
@EnableConfigurationProperties(MeiProperties.class)
public class MybatisEiAutoConfiguration {

    @Resource
    private List<SqlSessionFactory> sqlSessionFactoryList;
    @Autowired
    private List<TypeSupportEncryptors> encryptors;
    @Autowired
    private TypeSupportEncryptors<EncryptClass> encryptClassTypeSupportEncryptors;
    @Autowired
    private MeiProperties meiProperties;

    /**
     * 默认字符串加密器,把字符串翻转
     * 使用时请实现com.github.zhuyb0614.mei.encryptor.IStringEncryptor,自定义的字符串加密机
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public PrimitiveEncryptors stringEncryptors() {
        return new ReversePrimitiveEncryptors();
    }

    public PrimitiveEncryptors cacheStringEncryptor(MeiProperties meiProperties, PrimitiveEncryptors primitiveEncryptors) {
        if (meiProperties.getCacheSwitch()) {
            return new CachePrimitiveEncryptors(meiProperties, primitiveEncryptors);
        } else {
            return primitiveEncryptors;
        }
    }

    @Bean
    @SuppressWarnings("all")
    public TypeSupportEncryptors<EncryptClass> encryptClassEncryptor(PrimitiveEncryptors primitiveEncryptors, MeiProperties meiProperties) {
        return new EncryptClassEncryptors(meiProperties, cacheStringEncryptor(meiProperties, primitiveEncryptors));
    }

    @PostConstruct
    public void addPageInterceptor() {
        QueryCipherTextFieldSwitch.setMeiProperties(meiProperties);
        if (encryptors == null) {
            encryptors = new ArrayList<>();
        }
        if (!encryptors.contains(encryptClassTypeSupportEncryptors)) {
            encryptors.add(encryptClassTypeSupportEncryptors);
        }
        Interceptor encryptResultInterceptor = new EncryptResultInterceptor(meiProperties, encryptors);
        Interceptor encryptParameterInterceptor = new EncryptParameterInterceptor(meiProperties, encryptors);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(encryptParameterInterceptor);
            sqlSessionFactory.getConfiguration().addInterceptor(encryptResultInterceptor);
        }
    }

}
