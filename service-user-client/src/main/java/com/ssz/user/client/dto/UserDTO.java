package com.ssz.user.client.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDTO {

    private Long id;

    private String userName;

    private Integer userSex;

    private Integer userAge;

    private String productName;
}
