package com.github.zhuyb0614.mei.interceptor;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptors.Encryptors;
import com.github.zhuyb0614.mei.pojo.SourceBeanFieldValue;
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
import org.springframework.util.CollectionUtils;

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

    public EncryptParameterInterceptor(MeiProperties meiProperties, List<Encryptors> encryptors) {
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
        List<SourceBeanFieldValue> sourceBeanFieldValues = doEncrypt(parameter, isRemoveSource);
        Object result = invocation.proceed();
        if (isRemoveSource && !CollectionUtils.isEmpty(sourceBeanFieldValues)) {
            sourceBeanFieldValues.forEach(SourceBeanFieldValue::resetValue);
        }
        return result;
    }

    private List<SourceBeanFieldValue> doEncrypt(Object parameter, boolean isRemoveSource) {
        List<SourceBeanFieldValue> sourceBeanFieldValues = null;
        if (isRemoveSource) {
            sourceBeanFieldValues = new ArrayList<>();
        }
        //多个参数mybatis参数将是hashmap key=参数名 value=参数对象
        if (parameter instanceof HashMap) {
            Set<Object> encryptedObjSet = Sets.newHashSetWithExpectedSize(((HashMap) parameter).size());
            for (Object value : (((HashMap) parameter).values())) {
                if (!encryptedObjSet.contains(value)) {
                    encryptSingleOrCollection(value, isRemoveSource, sourceBeanFieldValues);
                    encryptedObjSet.add(value);
                }
            }
        } else {
            encryptSingleOrCollection(parameter, isRemoveSource, sourceBeanFieldValues);
        }
        return sourceBeanFieldValues;
    }

    /**
     * 是否删除明文字段
     *
     * @param isQuery
     * @return
     */
    private boolean isRemoveSource(boolean isQuery) {
        //是查询SQL,未开启明文字段查询.将明文字段加密赋值给密文字段,并清空源明文字段
        //不是查询SQL,并关闭了明文字段写入,将明文字段加密赋值给密文字段,并清空源明文字段
        return isQuery ? !meiProperties.getQuerySourceFieldSwitch() : !meiProperties.getWriteSourceFieldSwitch();
    }


    private void encryptSingleOrCollection(Object parameter, boolean isRemoveSource, List<SourceBeanFieldValue> sourceBeanFieldValues) {
        if (parameter instanceof Collection) {
            Collection collection = (Collection) parameter;
            Map<Class, List<Object>> classListMap = (Map<Class, List<Object>>) collection.stream().collect(Collectors.groupingBy(Object::getClass));
            for (Map.Entry<Class, List<Object>> classListEntry : classListMap.entrySet()) {
                Optional<Encryptors> encryptorsOptional = chooseEncryptors(classListEntry.getKey());
                if (encryptorsOptional.isPresent()) {
                    log.debug("param encrypt before {}", classListEntry.getValue());
                    encryptorsOptional.get().encryptBatch(classListEntry.getValue(), isRemoveSource, sourceBeanFieldValues);
                    log.debug("param encrypt after {}", classListEntry.getValue());
                }
            }
        } else {
            Optional<Encryptors> encryptorsOptional = chooseEncryptors(parameter.getClass());
            if (encryptorsOptional.isPresent()) {
                log.debug("param encrypt before {}", parameter);
                encryptorsOptional.get().encrypt(parameter, isRemoveSource, sourceBeanFieldValues);
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
