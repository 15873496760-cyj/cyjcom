package com.pinyougou.search.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class MyMessageListener2 implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到信息。");
        try {
            //获取消息
            ObjectMessage objectMessage = (ObjectMessage) message;
            //通过获取的消息家，将值得到
            Long[] ids = (Long[]) objectMessage.getObject();
            //从索引库中删除对应的sku商品
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
