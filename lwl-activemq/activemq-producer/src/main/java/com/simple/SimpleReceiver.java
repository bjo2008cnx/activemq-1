package com.simple;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class SimpleReceiver {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer consumer;
        connectionFactory = new ActiveMQConnectionFactory("", "", SimpleConstant.URL);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("FirstQueue"); //此处需修改为topic才能支持1对多发信息
            consumer = session.createConsumer(destination,"receiver = 'A'");  //创建普通消费者【接收者】，使用属性过滤

            MessageListener ml = new MessageListener() {
                @Override
                //设置监听器
                public void onMessage(Message m) {
                    TextMessage textMsg = (TextMessage) m;
                    try {
                        System.out.println("收到队列消息:" + textMsg.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };
            consumer.setMessageListener(ml);
            while (true) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) connection.close();
            } catch (Throwable ignore) {
            }
        }
    }
}