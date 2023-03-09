package com.itheima.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 接收登录请求的数据
 */
@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String userName;
    private Integer password;

}
