package com.itheima.pojo;

import lombok.Data;

/**
 * @Description: 接收登录请求的数据
 */
@Data
public class User {

    private String userId;
    private String username;
    private String password;
}
