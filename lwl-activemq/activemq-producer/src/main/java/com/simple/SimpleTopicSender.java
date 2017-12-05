package com.simple;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class SimpleTopicSender {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageProducer producer;
        connectionFactory = new ActiveMQConnectionFactory("", "", SimpleConstant.URL);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic("FirstTopic");
            producer = session.createProducer(destination);   // 得到消息生成者【发送者】

            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);  //设置是否持久化

            sendMessage(session, producer, "product message");
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) connection.close();
            } catch (Throwable ignore) {
            }
        }
    }

    public static void sendMessage(Session session, MessageProducer producer, String content) throws Exception {
        // 发送10条消息
        for (int i = 0; i < 9; i++) {
            TextMessage textMessage = session.createTextMessage("账号" + i + ":" + content);
            //content.setStringProperty("receiver", "A");  //可设置属性过滤
            producer.send(textMessage);
        }
    }
}