package com.simple;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualTopicExample {
    protected static String VIRTUAL_TOPIC_NAME = "DD_VTopic";

    //虚拟队列的名称以队列名结尾
    protected static String VIRTUAL_TOPIC_CONSUMER_NAMEA = "DD_VTopic_A";

    protected static String VIRTUAL_TOPIC_CONSUMER_NAMEB = "DD_VTopic_B";

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        try {
            Session session = createSession();

            handleQueueA(session); //启动消费者
            Thread.sleep(900);
            //handleQueueB(session); //启动消费者
            //Thread.sleep(9000);

            produce(session,VIRTUAL_TOPIC_CONSUMER_NAMEA);  //启动生产者
            //produce(session,VIRTUAL_TOPIC_CONSUMER_NAMEB);  //启动生产者
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void produce(Session session,String topicName) throws JMSException {
        MessageProducer producer = session.createProducer(new ActiveMQTopic(topicName)); //虚拟队列生产者
        int index = 0;
        while (index++ < 10) {
            TextMessage message = session.createTextMessage("序号为：" + index + "的消息.");
            System.out.println("已生产的消息：" + message);
            producer.send(message);
        }
    }

    private static Session createSession() throws JMSException {
        ActiveMQConnectionFactory factoryA = new ActiveMQConnectionFactory(SimpleConstant.URL);
        ActiveMQConnection conn = (ActiveMQConnection) factoryA.createConnection();
        conn.start();
        return conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    /**
     * 虚拟队列B只有1个consumer,它消费原始队列全部消息
     *
     * @param session
     * @throws JMSException
     */
    private static void handleQueueB(Session session) throws JMSException {
        Queue queue = new ActiveMQQueue(VIRTUAL_TOPIC_CONSUMER_NAMEB);
        MessageConsumer consumer = session.createConsumer(queue);
        final AtomicInteger count = new AtomicInteger(0);
        MessageListener listener = new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("B序号：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEB + ": 消息体：" + message);
            }
        };
        consumer.setMessageListener(listener);
    }

    /**
     * 虚拟队列A有3个consumer,各消费原始队列1/3的消息.（实际情况：分别处理消息数为:4.3.3）
     *
     * @param session
     * @throws JMSException
     */
    private static void handleQueueA(Session session) throws JMSException {
        Queue queue = new ActiveMQQueue(VIRTUAL_TOPIC_CONSUMER_NAMEA);
        MessageConsumer consumer1 = session.createConsumer(queue);
        MessageConsumer consumer2 = session.createConsumer(queue);
        MessageConsumer consumer3 = session.createConsumer(queue);

        final AtomicInteger count = new AtomicInteger(0);
        MessageListener listenerA = new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("队列1：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEA + "消息体：" + message);
            }
        };
        MessageListener listenerA_EX = new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("Queue2：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEA + "消息体：" + message);
            }
        };
        MessageListener listenerA_EX2 = new MessageListener() {
            public void onMessage(Message message) {
                System.out.println("虚拟队列3：" + count.incrementAndGet() + " => 接收自 " + VIRTUAL_TOPIC_CONSUMER_NAMEA + "消息体：" + message);
            }
        };
        consumer1.setMessageListener(listenerA);
        consumer2.setMessageListener(listenerA_EX);
        consumer3.setMessageListener(listenerA_EX2);
    }

}
