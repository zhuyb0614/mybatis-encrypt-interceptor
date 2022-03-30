package com.github.zhuyb0614.mei;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/23 11:32 上午
 */
@UtilityClass
@Slf4j
public class QueryCipherTextFieldSwitch {
    private static MeiProperties meiProperties;
    private Object lock = new Object();

    static void setMeiProperties(MeiProperties meiProperties) {
        synchronized (lock) {
            QueryCipherTextFieldSwitch.meiProperties = meiProperties;
            lock.notifyAll();
        }
    }

    /**
     * 当前是否通过密文字段查询
     *
     * @return
     */
    public static boolean isQueryByCipherTextField() {
        if (meiProperties == null) {
            synchronized (lock) {
                if (meiProperties == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        log.error("meiProperties wait error", e);
                    }
                }
            }
        }
        return !meiProperties.getQuerySourceFieldSwitch();
    }

}
