package com.github.zhuyb0614.mei;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mei")
@Data
public class MeiProperties {
    public static final int MAX_BATCH_SIZE = 1000;
    /**
     * 查询明文列开关
     */
    private Boolean querySourceFieldSwitch = Boolean.TRUE;
    /**
     * 是否写入明文字段开关
     */
    private Boolean writeSourceFieldSwitch = Boolean.TRUE;
    /**
     * 批量加解密的单批数据量，最大为1000。超过1000将使用1000
     */
    private Integer batchSize = MAX_BATCH_SIZE;
    /**
     * 是否开启缓存
     */
    private Boolean cacheSwitch = Boolean.TRUE;
    /**
     * 缓存最大数据量
     */
    private Integer cacheMaximumSize = 60000;
    /**
     * 缓存过期时间
     */
    private Integer cacheExpireAfterWriteSeconds = 3600;


    public void setBatchSize(Integer batchSize) {
        this.batchSize = Math.min(batchSize, MAX_BATCH_SIZE);
    }

}
