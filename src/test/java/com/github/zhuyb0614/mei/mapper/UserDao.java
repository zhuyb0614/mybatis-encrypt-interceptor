package com.github.zhuyb0614.mei.mapper;

import com.github.zhuyb0614.mei.entity.EncryptUser;
import com.github.zhuyb0614.mei.entity.User;
import com.github.zhuyb0614.mei.pojo.EncryptString;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserDao {

    @Select("select * from user where id= #{id}")
    User findById(Integer id);


    @Select("select * from user where id= #{id}")
    EncryptUser findEncryptUserById(Integer id);


    @Select({"<script>",
            "select * from user where         ",
            "<if test='es.plainText!=null'>name = #{es.plainText}</if>",
            "<if test='es.cipherText!=null'>encrypt_name = #{es.cipherText}</if>",
            "</script>"})
    EncryptUser findByName(@Param("es") EncryptString encryptString);


    @Insert({"INSERT INTO user (id, name, encrypt_name, age,encrypt_age,email) ",
            "VALUES (#{id}, #{name}, #{encryptName}, #{age}, #{encryptAge},#{email})"})
    int insert(EncryptUser encryptUser);

}