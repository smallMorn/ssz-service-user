package com.ssz.service.user.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ssz.common.model.dto.UserQueryDTO;
import com.ssz.service.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ssz
 * @since 2021-01-11
 */
public interface UserMapper extends BaseMapper<User> {

    Page<User> selectList(Page page, @Param("queryDTO") UserQueryDTO queryDTO);

    List<Long> selectUserIdList(Page page);

}
