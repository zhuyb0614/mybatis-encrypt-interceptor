package com.github.zhuyb0614.mei;

import com.github.zhuyb0614.mei.entity.UserAuth;
import com.github.zhuyb0614.mei.mapper.UserAuthDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/30 10:58 上午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MybatisEncryptInterceptorApplication.class})
@Slf4j
public class UserAuthDaoTest {

    @Autowired
    private UserAuthDao userAuthDao;

    @Test
    public void findById() {
        UserAuth userAuth = userAuthDao.findById(1);
        log.info("user auth 1 {}", userAuth);
        Assert.assertTrue("dundun".equals(userAuth.getIdentityNo()));
    }

    @Test
    public void insert() {
        int id = 2;
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(id);
        String identityNo = "123456";
        userAuth.setIdentityNo(identityNo);
        userAuthDao.insert(userAuth);
        UserAuth param = new UserAuth();
        param.setIdentityNo(identityNo);
        UserAuth persistUserAuth = userAuthDao.findByIdentityNo(param);
        log.info("user auth 2 {}", persistUserAuth);
        Assert.assertTrue(identityNo.equals(persistUserAuth.getIdentityNo()));
    }
}