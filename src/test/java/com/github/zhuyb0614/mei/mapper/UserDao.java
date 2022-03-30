package com.github.zhuyb0614.mei.mapper;

import com.github.zhuyb0614.mei.entity.EncryptUser;
import com.github.zhuyb0614.mei.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserDao {

    @Select("select * from user where id= #{id}")
    User findById(Integer id);

    @Select("select * from user where id= #{id}")
    EncryptUser findEncryptUserById(Integer id);

    @Insert("INSERT INTO user (id, name, encrypt_name, age, email) VALUES (#{id}, #{name}, #{encryptName}, #{age}, #{email})")
    int insert(EncryptUser encryptUser);
}