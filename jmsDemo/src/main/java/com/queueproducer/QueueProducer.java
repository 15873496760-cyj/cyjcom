package com.queueproducer;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class QueueProducer {

    public static void main(String[] args) {

        try {
            //创建connectionFactory对象
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //获取连接
            Connection connection = connectionFactory.createConnection();
            //启动连接
            connection.start();
            //获取session
            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            //创建队列对象
            Queue queue = session.createQueue("text-queue");
            //创建消息生产者
            MessageProducer producer = session.createProducer(queue);
            //创建消息
            TextMessage hello_activeMQ = session.createTextMessage("Hello ActiveMQ");
            //发送消息
            producer.send(hello_activeMQ);
            //关闭资源
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
