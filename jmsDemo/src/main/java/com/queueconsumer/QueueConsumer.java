package com.queueconsumer;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

public class QueueConsumer {

    public static void main(String[] args) {
        try {
            //创建connectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //获取连接
            Connection connection = connectionFactory.createConnection();
            //开启连接
            connection.start();
            //获取session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建队列对象
            Queue queue = session.createQueue("text-queue");
            //创建消费者
            MessageConsumer consumer = session.createConsumer(queue);
            //监听消息
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("textMessage = " + textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            //等待输入
            System.in.read();
            //关闭资源
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
