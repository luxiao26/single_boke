package com.huanf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.domain.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * @author 35238
 * @date 2023/8/2 0002 21:13
 */

public interface TagMapper extends BaseMapper<Tag> {
    //删除标签，注意是逻辑删除，所以不能使用mybatisplus提供的，我们要自己使用mybatis写SQL语句
    int myUpdateById(@Param("id") Long id,@Param("flag") int flag);
}
