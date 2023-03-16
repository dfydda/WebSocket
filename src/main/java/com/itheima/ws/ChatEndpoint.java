package com.itheima.ws;


import com.alibaba.fastjson.JSON;
import com.itheima.config.WebsocketConfig;
import com.itheima.pojo.UserMessage;
import com.itheima.service.UserMessageService;
import com.itheima.utils.MessageUtils;
import com.itheima.ws.pojo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat",configurator = WebsocketConfig.class)
@Component
@Slf4j
public class ChatEndpoint {

    private static final Map<String,Session> onlineUsers = new ConcurrentHashMap<>();

    //新建list集合存储数据
    private static ArrayList<UserMessage> MessageList = new ArrayList<>();
    //设置一次性存储数据的list的长度为固定值，每当list的长度达到固定值时，向数据库存储一次
    private static final Integer LIST_SIZE =3;

    //静态成员变量，全局
    public static UserMessageService userMessageService;
    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
       ChatEndpoint.userMessageService = userMessageService;
    }

    private HttpSession httpSession;

    /**
     * 建立websocket连接后，被调用
     * @param session
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config){
        //1.将session进行保存
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String user = (String) this.httpSession.getAttribute("user");
        onlineUsers.put(user,session);
        //2.广播消息，需要将登录的所有的用户推送给所有的用户
        String message = MessageUtils.getMessage(true, null, getFriends());
        broadcastAllUsers(message);
    }

    /**
     * 获取用户好友信息
     * @return
     */
    public Set getFriends(){
        Set<String> set = onlineUsers.keySet();
        return set;
    }

    /**
     * 发送广播消息消息
     * @param message
     */
    private void broadcastAllUsers(String message){
        try {
            //遍历map集合
            Set<Map.Entry<String, Session>> entries = onlineUsers.entrySet();
            for (Map.Entry<String, Session> entry : entries) {
                //获取所有用户对应Session对象
                Session session = entry.getValue();
                //发送消息(同步消息)
                session.getBasicRemote().sendText(message);

            }
        }catch (Exception e){
            //记录日志
        }
    }

    /**
     * 浏览器发送消息到服务器，该方法被调用
     *
     * 张三 ---》 李四
     * @param message
     */
    @OnMessage
    public void onMessage(String message){
        try {
            //将消息推送至指定用户
            Message msg = JSON.parseObject(message, Message.class);
            //获取 消息接收方
            String toName = msg.getToName();
            String mess = msg.getMessage();
            //获取消息接收方用户对象的session对象
            Session session = onlineUsers.get(toName);
            String user = (String) this.httpSession.getAttribute("user");
            String msg1 = MessageUtils.getMessage(false, user, mess);
            session.getBasicRemote().sendText(msg1);
            //新建message对象，存储交流信息
            UserMessage message1 = new UserMessage();
            message1.setUsername(user);
            message1.setToname(toName);
            message1.setMessage(mess);
            message1.setCreatetime(String.valueOf(LocalDateTime.now()));
            //批量保存信息
            //将每条记录添加到list集合中
            MessageList.add(message1);
            //判断list集合长度
            if(MessageList.size() == LIST_SIZE){
                userMessageService.saveBatch(MessageList);
                //清空集合
                MessageList.clear();
            }

        }catch (Exception e){
          //记录日志
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        log.info("webSocket发生错误！");
        error.printStackTrace();
    }

    /**
     * 断开websocket 连接时被调用
     * @param session
     */
    @OnClose
    public void onClose(Session session){
        //判断list集合长度
        if(MessageList.size() <= LIST_SIZE){
            userMessageService.saveBatch(MessageList);
            //清空集合
            MessageList.clear();
        }
        //1.从onlineUsers中剔除当前用户的session对象
        String user = (String) this.httpSession.getAttribute("user");
        onlineUsers.remove(user);
        //2.通知其他所有的用户，当前用户下线了
        String message = MessageUtils.getMessage(true,null,getFriends());
        broadcastAllUsers(message);

    }
}
