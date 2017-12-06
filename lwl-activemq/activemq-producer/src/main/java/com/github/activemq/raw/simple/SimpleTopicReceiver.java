package com.github.activemq.raw.simple;

import com.github.activemq.raw.MQConstant;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class SimpleTopicReceiver {

    public static final String CLIENT_ID = "guangdong";
    public static final String TOPIC_NAME = "FirstTopic";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer consumer;
        //参数为用户名，密码，MQ的url
        connectionFactory = new ActiveMQConnectionFactory("", "", MQConstant.URL);
        try {
            connection = connectionFactory.createConnection();
            connection.setClientID(CLIENT_ID); //持久化订阅才使用下语句！参数是clientid，设置该参数后MQ会记住该ID---
            connection.start();
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic(TOPIC_NAME);
            //持久化订阅！第二个参数是client名
            consumer = session.createDurableSubscriber((Topic) destination, CLIENT_ID);

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
                //do nothing.
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