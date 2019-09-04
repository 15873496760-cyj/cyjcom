package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class MyMessageListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到信息。");
        try {
            //得到消息
            TextMessage textMessage = (TextMessage) message;
            //将得到的内容转换为集合对象
            List<TbItem> tbItems = JSON.parseArray(textMessage.getText(), TbItem.class);
            //循环遍历
            for (TbItem tbItem : tbItems) {
                //获取对象中的spec，将其转换为map集合
                Map specMap = JSON.parseObject(tbItem.getSpec(),Map.class);
                tbItem.setSpecMap(specMap);
            }
            itemSearchService.importList(tbItems);
            System.out.println("成功导入索引库中");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
