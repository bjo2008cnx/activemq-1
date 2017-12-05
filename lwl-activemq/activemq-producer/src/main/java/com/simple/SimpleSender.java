package com.simple;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class SimpleSender {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageProducer producer;
        //参数为用户名，密码，MQ的url
        connectionFactory = new ActiveMQConnectionFactory("smechina", "fulong", "tcp://localhost:61616");
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            //*******注意：此处需修改为topic才能支持1对多发信息
            //destination = session.createQueue("FirstQueue");
            destination = session.createTopic("FirstTopic");
            // 得到消息生成者【发送者】
            producer = session.createProducer(destination);
            // ********设置是否持久化
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            //uandp是需要发送的消息
            String uandp = "liyang:fulong";
            sendMessage(session, producer, uandp);
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

    public static void sendMessage(Session session, MessageProducer producer, String passport) throws Exception {
        // 发送10条消息
        for (int i = 0; i < 9; i++) {
            TextMessage message = session.createTextMessage("账号" + i + ":" + passport);
            //System.out.println("账号是:" + passport);
            //可设置属性过滤
            //message.setStringProperty("receiver", "A");
            producer.send(message);
        }
    }
}