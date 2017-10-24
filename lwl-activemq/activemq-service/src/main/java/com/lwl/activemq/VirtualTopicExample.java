package com.lwl.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualTopicExample {

    public static void main(String[] args) {
        try {
            ActiveMQConnectionFactory factoryA = new ActiveMQConnectionFactory("tcp://10.16.64.14:61617");
            ActiveMQConnection conn = (ActiveMQConnection) factoryA.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            handleQueueA(session);
            handleQueueB(session);

            MessageProducer producer = session.createProducer(new ActiveMQTopic(getVirtualTopicName()));
            int index = 0;
            while (index++ < 10) {
                TextMessage message = session.createTextMessage(index + " message.");
                producer.send(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleQueueB(Session session) throws JMSException {
        Queue queueB = new ActiveMQQueue(getVirtualTopicConsumerNameB());
        MessageConsumer consumer3 = session.createConsumer(queueB);
        final AtomicInteger aint2 = new AtomicInteger(0);
        MessageListener listenerB = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    System.out.println("序号："+aint2.incrementAndGet() + " => 接收自" + getVirtualTopicConsumerNameB() + ": 消息体：" + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        consumer3.setMessageListener(listenerB);
    }

    private static void handleQueueA(Session session) throws JMSException {
        Queue queueA = new ActiveMQQueue(getVirtualTopicConsumerNameA());
        MessageConsumer consumer1 = session.createConsumer(queueA);
        MessageConsumer consumer2 = session.createConsumer(queueA);

        final AtomicInteger count = new AtomicInteger(0);
        MessageListener listenerA = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    System.out.println(count.incrementAndGet() + " => 接收自 " + getVirtualTopicConsumerNameA() + "消息体：" + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        consumer1.setMessageListener(listenerA);
        consumer2.setMessageListener(listenerA);
    }

    protected static String getVirtualTopicName() {
        return "VirtualTopic.TEST";
    }

    protected static String getVirtualTopicConsumerNameA() {
        return "Consumer.A.VirtualTopic.TEST";
    }

    protected static String getVirtualTopicConsumerNameB() {
        return "Consumer.B.VirtualTopic.TEST";
    }

}
