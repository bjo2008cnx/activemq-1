package com.github.activemq.raw.tx;

import com.github.activemq.raw.MQConstant;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TxExReceiver {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageConsumer consumer;
        connectionFactory = new ActiveMQConnectionFactory("", "", MQConstant.URL);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE, Session.SESSION_TRANSACTED);
            destination = session.createQueue(TxSender.QUEUE_NAME); //此处需修改为topic才能支持1对多发信息
            consumer = session.createConsumer(destination,"receiver = 'A'");  //创建普通消费者【接收者】，使用属性过滤

            MessageListener ml = new MessageListener() {
                @Override
                //设置监听器
                public void onMessage(Message m) {
                    TextMessage textMsg = (TextMessage) m;
                    try {
                        System.out.println("收到tx队列消息:" + textMsg.getText());
                        if (true) throw new RuntimeException("未知星球的未知生物来袭");
                        System.out.println("已处理tx队列消息:" + textMsg.getText());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
            consumer.setMessageListener(ml);
            session.commit();
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