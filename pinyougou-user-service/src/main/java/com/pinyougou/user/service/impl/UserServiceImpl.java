package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbUserMapper userMapper;
    //1.发送验证码及相关信息给springBootDemo这个微服务
    @Override
    public void sendValidCode(final String phone) {
        //获取六位数的随机数
        final String code = (long) (Math.random() * 1000000) + "";
        //子一个参数是手机号码，第二个参数是随机数
        redisTemplate.boundHashOps("myValidCode").put(phone, code);
        jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone",phone);
                mapMessage.setString("sign_name","品优购");
                mapMessage.setString("template_code","SMS_173405073");
                Map map = new HashMap<>();
                map.put("code",code);
                //mapMessage.setString("param","{'code':'" + code + "'}");
                String param = JSON.toJSONString(map);
                mapMessage.setString("param",param);
                return mapMessage;
            }
        });
    }
    //验证输入的验证码是否正确
    @Override
    public boolean checkValidCode(String phone,String validCode) {
        //从redis中得到验证码
        String myValidCode = (String) redisTemplate.boundHashOps("myValidCode").get(phone);
        //判断两次的验证码输入的是否一致
        if(myValidCode.equals(validCode)){
            return true;
        }
        return false;
    }

    @Override
    public void add(TbUser user) {
        userMapper.insert(user);
    }

}
