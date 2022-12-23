package com.ssz.client.dto;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class UserDTO implements Serializable {

    private String userName;

    private Integer userSex;

    private Integer userAge;

    private String productName;
}
