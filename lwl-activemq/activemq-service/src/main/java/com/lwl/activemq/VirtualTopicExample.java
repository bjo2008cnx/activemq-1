package com.lwl.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualTopicExample {
    protected static String VIRTUAL_TOPIC_NAME = "VirtualTopic.TEST";

    //虚拟队列的名称以队列名结尾
    protected static String VIRTUAL_TOPIC_CONSUMER_NAMEA = "Consumer.A.VirtualTopic.TEST";

    protected static String VIRTUAL_TOPIC_CONSUMER_NAMEB = "Consumer.B.VirtualTopic.TEST";

    public static void main(String[] args) {
        try {
            ActiveMQConnectionFactory factoryA = new ActiveMQConnectionFactory("tcp://10.16.64.14:61617");
            ActiveMQConnection conn = (ActiveMQConnection) factoryA.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            handleQueueA(session);
            handleQueueB(session);

            MessageProducer producer = session.createProducer(new ActiveMQTopic(VIRTUAL_TOPIC_NAME));
            int index = 0;
            while (index++ < 10) {
                TextMessage message = session.createTextMessage(index + "消息.");
                producer.send(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleQueueB(Session session) throws JMSException {
        Queue queueB = new ActiveMQQueue(VIRTUAL_TOPIC_CONSUMER_NAMEB);
        MessageConsumer consumer3 = session.createConsumer(queueB);
        final AtomicInteger count = new AtomicInteger(0);
        MessageListener listenerB = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    System.out.println("B序号：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEB + ": 消息体：" + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        consumer3.setMessageListener(listenerB);
    }

    private static void handleQueueA(Session session) throws JMSException {
        Queue queueA = new ActiveMQQueue(VIRTUAL_TOPIC_CONSUMER_NAMEA);
        MessageConsumer consumer1 = session.createConsumer(queueA);
        MessageConsumer consumer2 = session.createConsumer(queueA);

        final AtomicInteger count = new AtomicInteger(0);
        MessageListener listenerA = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    System.out.println("A队列：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEA + "消息体：" + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        MessageListener listenerA_EX = new MessageListener() {
            public void onMessage(Message message) {
                try {
                    System.out.println("A____EX队列：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEA + "消息体：" + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        consumer1.setMessageListener(listenerA);
        consumer2.setMessageListener(listenerA_EX);
    }

}
