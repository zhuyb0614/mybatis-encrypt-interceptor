package com.github.zhuyb0614.mei;


import com.github.zhuyb0614.mei.encryptors.Encryptors;
import com.github.zhuyb0614.mei.encryptors.StringEncryptors;
import com.github.zhuyb0614.mei.encryptors.impl.CacheStringEncryptors;
import com.github.zhuyb0614.mei.encryptors.impl.EncryptClassEncryptors;
import com.github.zhuyb0614.mei.encryptors.impl.ReverseStringEncryptors;
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
    private List<Encryptors> encryptors;
    @Autowired
    private Encryptors<EncryptClass> encryptClassEncryptors;
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
    public StringEncryptors stringEncryptor() {
        return new ReverseStringEncryptors();
    }

    public StringEncryptors cacheStringEncryptor(MeiProperties meiProperties, StringEncryptors stringEncryptors) {
        if (meiProperties.getCacheSwitch()) {
            return new CacheStringEncryptors(meiProperties, stringEncryptors);
        } else {
            return stringEncryptors;
        }
    }

    @Bean
    @SuppressWarnings("all")
    public Encryptors<EncryptClass> encryptClassEncryptor(StringEncryptors stringEncryptors, MeiProperties meiProperties) {
        return new EncryptClassEncryptors(meiProperties, cacheStringEncryptor(meiProperties, stringEncryptors));
    }

    @PostConstruct
    public void addPageInterceptor() {
        QueryCipherTextFieldSwitch.setMeiProperties(meiProperties);
        if (encryptors == null) {
            encryptors = new ArrayList<>();
        }
        if (!encryptors.contains(encryptClassEncryptors)) {
            encryptors.add(encryptClassEncryptors);
        }
        Interceptor encryptResultInterceptor = new EncryptResultInterceptor(meiProperties, encryptors);
        Interceptor encryptParameterInterceptor = new EncryptParameterInterceptor(meiProperties, encryptors);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(encryptParameterInterceptor);
            sqlSessionFactory.getConfiguration().addInterceptor(encryptResultInterceptor);
        }
    }

}
