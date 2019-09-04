package com.cyj.consumer;


import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.cyj.util.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Consumer {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "item")
    public void readMessage(String text) {
        System.out.println("text = " + text);
    }

    @JmsListener(destination = "userJms")
    public void sendValidCode(Map map) {
        try {
            //得到请求的参数
            String phone = (String) map.get("phone");
            String param = (String) map.get("param");
            String template_code = (String) map.get("template_code");
            String sign_name = (String) map.get("sign_name");
            System.out.println("param = " + param);
            System.out.println("map.get(param) ==> " + map.get("param"));
            System.out.println("phone = " + phone);
            System.out.println("template_code = " + template_code);
            System.out.println("sign_name = " + sign_name);
            //发送信息给微服务后台
            SendSmsResponse sendSmsResponse = smsUtil.sendSms(phone,sign_name , template_code, param);
            System.out.println("sendSmsResponse = " + sendSmsResponse.getCode());
            System.out.println("发送成功！");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
