package com.github.zhuyb0614.mei;


import com.github.zhuyb0614.mei.encryptor.Encryptor;
import com.github.zhuyb0614.mei.encryptor.StringEncryptor;
import com.github.zhuyb0614.mei.encryptor.impl.CacheStringEncryptor;
import com.github.zhuyb0614.mei.encryptor.impl.EncryptClassEncryptor;
import com.github.zhuyb0614.mei.encryptor.impl.ReverseStringEncryptor;
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
    private List<Encryptor> encryptors;
    @Autowired
    private Encryptor<EncryptClass> encryptClassEncryptor;
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
    public StringEncryptor stringEncryptor() {
        return new ReverseStringEncryptor();
    }

    public StringEncryptor cacheStringEncryptor(MeiProperties meiProperties, StringEncryptor stringEncryptor) {
        if (meiProperties.getCacheSwitch()) {
            return new CacheStringEncryptor(meiProperties, stringEncryptor);
        } else {
            return stringEncryptor;
        }
    }

    @Bean
    @SuppressWarnings("all")
    public Encryptor<EncryptClass> encryptClassEncryptor(StringEncryptor stringEncryptor, MeiProperties meiProperties) {
        return new EncryptClassEncryptor(meiProperties, cacheStringEncryptor(meiProperties, stringEncryptor));
    }

    @PostConstruct
    public void addPageInterceptor() {
        QueryCipherTextFieldSwitch.setMeiProperties(meiProperties);
        if (encryptors == null) {
            encryptors = new ArrayList<>();
        }
        if (!encryptors.contains(encryptClassEncryptor)) {
            encryptors.add(encryptClassEncryptor);
        }
        Interceptor encryptResultInterceptor = new EncryptResultInterceptor(meiProperties, encryptors);
        Interceptor encryptParameterInterceptor = new EncryptParameterInterceptor(meiProperties, encryptors);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            sqlSessionFactory.getConfiguration().addInterceptor(encryptParameterInterceptor);
            sqlSessionFactory.getConfiguration().addInterceptor(encryptResultInterceptor);
        }
    }

}
