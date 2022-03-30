package com.github.zhuyb0614.mei;

import com.github.zhuyb0614.mei.entity.EncryptUser;
import com.github.zhuyb0614.mei.entity.User;
import com.github.zhuyb0614.mei.mapper.UserDao;
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
 * @date 2022/3/30 9:10 上午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MybatisEncryptInterceptorApplication.class})
@Slf4j
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void findById() {
        User user = userDao.findById(1);
        log.info("user 1 {}", user);
        Assert.assertTrue("yb".equals(user.getName()));
    }

    @Test
    public void findEncryptUserById() {
        int id = 1;
        EncryptUser encryptUser = userDao.findEncryptUserById(id);
        log.info("encrypt user 1 {}", encryptUser);
        User user = userDao.findById(id);
        log.info("user 1 {}", user);
        Assert.assertTrue("yunbo".equals(encryptUser.getName()));
    }

    @Test
    public void insert() {
        EncryptUser encryptUser = new EncryptUser();
        int id = 2;
        encryptUser.setId(id);
        encryptUser.setName("zhangsan");
        encryptUser.setAge(66);
        encryptUser.setEmail("zhangsan@xxx.com");
        userDao.insert(encryptUser);
        EncryptUser persistUser = userDao.findEncryptUserById(id);
        log.info("encrypt user 3 {}", persistUser);
        User user = userDao.findById(id);
        log.info("user 3 {}", user);
        Assert.assertTrue("zhangsan".equals(persistUser.getName()));
    }
}