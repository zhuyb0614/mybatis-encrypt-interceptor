package com.github.zhuyb0614.mei.interceptor;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptor.Encryptor;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author zhuyunbo
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
@Slf4j
public class EncryptParameterInterceptor extends BaseInterceptor {

    public static final String QUERY = "query";

    public EncryptParameterInterceptor(MeiProperties meiProperties, List<Encryptor> encryptors) {
        super(meiProperties, encryptors);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];
        if (parameter == null) {
            return invocation.proceed();
        }
        boolean isQuery = QUERY.equals(invocation.getMethod().getName());
        //是查询SQL,并且开启的是明文字段查询.不做加密处理,直接使用原参数查询
        if (isQuery && meiProperties.getQuerySourceFieldSwitch()) {
            return invocation.proceed();
        }
        boolean isRemoveSource = isRemoveSource(isQuery);

        //多个参数mybatis参数将是hashmap key=参数名 value=参数对象
        if (parameter instanceof HashMap) {
            Set<Object> encryptedObjSet = Sets.newHashSetWithExpectedSize(((HashMap) parameter).size());
            for (Object value : (((HashMap) parameter).values())) {
                if (!encryptedObjSet.contains(value)) {
                    encryptSingleOrCollection(value, isRemoveSource);
                    encryptedObjSet.add(value);
                }
            }
        } else {
            encryptSingleOrCollection(parameter, isRemoveSource);
        }
        return invocation.proceed();
    }

    /**
     * 是否删除明文字段
     *
     * @param isQuery
     * @return
     */
    private boolean isRemoveSource(boolean isQuery) {
        boolean isRemoveSource = false;
        //是查询SQL,未开启明文字段查询.将明文字段加密赋值给密文字段,并清空源明文字段
        if (isQuery && !meiProperties.getQuerySourceFieldSwitch()) {
            isRemoveSource = true;
            //不是查询SQL,并关闭了明文字段写入,将明文字段加密赋值给密文字段,并清空源明文字段
        } else if (!isQuery && !meiProperties.getWriteSourceFieldSwitch()) {
            isRemoveSource = true;
        }
        return isRemoveSource;
    }


    private void encryptSingleOrCollection(Object parameter, boolean isRemoveSource) {
        if (parameter instanceof Collection) {
            Collection collection = (Collection) parameter;
            Map<Class, List<Object>> classListMap = (Map<Class, List<Object>>) collection.stream().collect(Collectors.groupingBy(Object::getClass));
            for (Map.Entry<Class, List<Object>> classListEntry : classListMap.entrySet()) {
                Optional<Encryptor> encryptorOptional = chooseEncryptor(classListEntry.getKey());
                if (encryptorOptional.isPresent()) {
                    log.debug("param encrypt before {}", classListEntry.getValue());
                    encryptorOptional.get().encryptBatch(classListEntry.getValue(), isRemoveSource);
                    log.debug("param encrypt after {}", classListEntry.getValue());
                }
            }
        } else {
            Optional<Encryptor> encryptorOptional = chooseEncryptor(parameter.getClass());
            if (encryptorOptional.isPresent()) {
                log.debug("param encrypt before {}", parameter);
                encryptorOptional.get().encrypt(parameter, isRemoveSource);
                log.debug("param encrypt after {}", parameter);
            }
        }
    }


    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
