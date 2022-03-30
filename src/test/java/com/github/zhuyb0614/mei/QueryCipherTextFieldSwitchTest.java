package com.github.zhuyb0614.mei;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/23 2:33 下午
 */
@Slf4j
public class QueryCipherTextFieldSwitchTest {

    @Test
    public void isQueryByEncryptField() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int threads = 10;
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            new Thread(() -> {
                log.info("t {} result : {}", finalI, QueryCipherTextFieldSwitch.isQueryByCipherTextField());
                countDownLatch.countDown();
            }).start();
        }
        Thread.sleep(3000);
        QueryCipherTextFieldSwitch.setMeiProperties(new MeiProperties());
        countDownLatch.await();
        long costTime = System.currentTimeMillis() - startTime;
        log.info("cost time {}", costTime);
        Assert.assertTrue(costTime > 3000);
    }
}