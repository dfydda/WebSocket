package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.pojo.Result;
import com.itheima.pojo.User;
import com.itheima.pojo.UserMessage;
import com.itheima.service.UserMessageService;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMessageService userMessageService;


    /**
     * 登陆
     * @param user 提交的用户数据，包含用户名和密码
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody User user, HttpSession session) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        queryWrapper.eq(User::getPassword,user.getPassword());
        User one = userService.getOne(queryWrapper);
        if(one == null) {
            return R.error("查无此账号");
        } else {
            //将数据存储到session对象中
            session.setAttribute("user",user.getUserName());
            return R.success(one);
        }
    }

    /**
     * 获取用户名
     * @param session
     * @return
     */
    @GetMapping("/getusername")
    public String getUsername(HttpSession session) {

        String username = (String) session.getAttribute("user");
        return username;
    }
    @GetMapping("/historyshow")
    public R<List<UserMessage>> historyShow(HttpSession session){
        String username = (String) session.getAttribute("user");
        //新建list集合存储数据
        LambdaQueryWrapper<UserMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserMessage::getUsername,username);
        List<UserMessage> historyList =userMessageService.list(queryWrapper);
        LambdaQueryWrapper<UserMessage> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(UserMessage::getToname,username);
        List<UserMessage> historyList1 = userMessageService.list(queryWrapper1);
        historyList.addAll(historyList1);
        return R.success(historyList);
    }
}
