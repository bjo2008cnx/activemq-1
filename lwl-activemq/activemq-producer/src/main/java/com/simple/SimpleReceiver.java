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
        //参数为用户名，密码，MQ的url
        connectionFactory = new ActiveMQConnectionFactory("smeguangdong", "fulong", "tcp://localhost:61616");
        try {
            connection = connectionFactory.createConnection();
            //---持久化订阅才使用下语句！！！！参数是clientid，设置该参数后MQ会记住该ID---
            connection.setClientID("guangdong");
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            //*******注意：此处需修改为topic才能支持1对多发信息
            //destination = session.createQueue("FirstQueue");
            destination = session.createTopic("FirstTopic");
            //创建普通消费者【接收者】，使用属性过滤
            //consumer = session.createConsumer(destination,"receiver = 'A'");
            //--------持久化订阅！！！！------第二个参数是client名
            consumer = session.createDurableSubscriber((Topic) destination, "guangdong");

            MessageListener ml = new MessageListener() {
                @Override
                //设置监听器
                public void onMessage(Message m) {
                    TextMessage textMsg = (TextMessage) m;
                    try {
                        System.out.println("收到消息:" + textMsg.getText());
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