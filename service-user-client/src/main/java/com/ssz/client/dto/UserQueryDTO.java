package com.ssz.client.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Date 2021/1/12 16:31
 * @Author ssz
 */
@Data
@Accessors(chain = true)
public class UserQueryDTO implements Serializable {

    private Integer pageCurrent;
    private Integer pageSize;
}
