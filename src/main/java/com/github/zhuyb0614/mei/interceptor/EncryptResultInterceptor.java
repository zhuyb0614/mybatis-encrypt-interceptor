package com.github.zhuyb0614.mei.interceptor;

import com.github.zhuyb0614.mei.MeiProperties;
import com.github.zhuyb0614.mei.encryptor.Encryptor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.util.CollectionUtils;

import java.sql.Statement;
import java.util.*;

/**
 * @author zhuyunbo
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class EncryptResultInterceptor extends BaseInterceptor {

    public EncryptResultInterceptor(MeiProperties meiProperties, List<Encryptor> encryptors) {
        super(meiProperties, encryptors);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (Objects.isNull(result)) {
            return null;
        }
        //查询结果处理了两种情况，单个对象和arraylist的对象集合
        if (result instanceof ArrayList) {
            ArrayList resultList = (ArrayList) result;
            if (!CollectionUtils.isEmpty(resultList)) {
                Optional<Encryptor> encryptorOptional = chooseEncryptor(resultList.get(0).getClass());
                if (encryptorOptional.isPresent()) {
                    encryptorOptional.get().decryptBatch(resultList);
                }
            }
        } else {
            Optional<Encryptor> encryptorOptional = chooseEncryptor(result.getClass());
            if (encryptorOptional.isPresent()) {
                encryptorOptional.get().decrypt(result);
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
