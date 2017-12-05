package com.simple;

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
            ActiveMQConnectionFactory factoryA = new ActiveMQConnectionFactory(SimpleConstant.URL);
            ActiveMQConnection conn = (ActiveMQConnection) factoryA.createConnection();
            conn.start();
            Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            handleQueueA(session);
            Thread.sleep(900);
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

    /**
     * 虚拟队列B只有1个consumer,它消费原始队列全部消息
     *
     * @param session
     * @throws JMSException
     */
    private static void handleQueueB(Session session) throws JMSException {
        final AtomicInteger count = new AtomicInteger(0);
        MessageListener listener = new MessageListener() {
            public void onMessage(Message message) {
                if (true) throw new RuntimeException("exception occured");
                System.out.println("B序号：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEB + ": 消息体：" + message);
            }
        };
        Queue queue = new ActiveMQQueue(VIRTUAL_TOPIC_CONSUMER_NAMEB);
        session.createConsumer(queue).setMessageListener(listener);
    }

    /**
     * 虚拟队列A有3个consumer,各消费原始队列1/3的消息.（实际情况：分别处理消息数为:4.3.3）
     *
     * @param session
     * @throws JMSException
     */
    private static void handleQueueA(Session session) throws JMSException {
        Queue queue = new ActiveMQQueue(VIRTUAL_TOPIC_CONSUMER_NAMEA);

        final AtomicInteger count = new AtomicInteger(0);
        session.createConsumer(queue).setMessageListener(createListener(count, "A1"));
        session.createConsumer(queue).setMessageListener(createListener(count, "a2"));
        session.createConsumer(queue).setMessageListener(createListener(count, "A3"));
    }

    private static MessageListener createListener(final AtomicInteger count, final String name) {
        return new MessageListener() {
            public void onMessage(Message message) {
                System.out.println(name + "队列：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEA + "消息体：" + message);
            }
        };
    }
}
