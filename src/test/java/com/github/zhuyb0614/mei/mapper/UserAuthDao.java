package com.github.zhuyb0614.mei.mapper;

import com.github.zhuyb0614.mei.entity.UserAuth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author yunbo.zhu
 * @version 1.0
 * @date 2022/3/30 10:56 上午
 */
@Repository
@Mapper
public interface UserAuthDao {
    @Select("select * from user_auth where user_id = #{id}")
    UserAuth findById(Integer id);

    @Select({"<script>",
            "select * from user_auth where         " +
                    "<if test='ua.identityNo!=null'>identity_no = #{ua.identityNo}</if>",
            "<if test='ua.encryptIdentityNo!=null'>encrypt_identity_no = #{ua.encryptIdentityNo}</if>",
            "</script>"})
    UserAuth findByIdentityNo(@Param("ua") UserAuth userAuth);

    @Insert("INSERT INTO user_auth (user_id, identity_no, encrypt_identity_no) VALUES (#{ua.userId}, #{ua.identityNo},#{ua.encryptIdentityNo})")
    int insert(@Param("ua") UserAuth userAuth);
}
