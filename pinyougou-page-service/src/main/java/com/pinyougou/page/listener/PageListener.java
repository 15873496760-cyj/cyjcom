package com.pinyougou.page.listener;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService pageService;

    @Override
    public void onMessage(Message message) {
        System.out.println("接收到信息。。。page");
        try {
            //获取消息
            ObjectMessage objectMessage = (ObjectMessage) message;
            //将获取的内容转换成Long数组
            Long[] ids = (Long[])objectMessage.getObject();
            //循环遍历
            for (Long id : ids) {
                pageService.genItemHtml(id);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
