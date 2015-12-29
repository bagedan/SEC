package com.sec.sep.user;

import com.cep.event.Event;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import static org.junit.Assert.*;

/**
 * Created by Tkachi on 2015/12/29.
 */
public class UsersUITest {

    public  String queueId = "USER";
    public static final String BORKE_URL = "tcp://localhost:61616";
    private Session session;
    private MessageProducer producer;

    public static void main(String[] args) {
        UsersUITest test = new UsersUITest();
        test.sendMessage();
    }

    public UsersUITest() {
        try {
            initSession();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void initSession() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BORKE_URL);

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue(queueId);

        // Create a MessageProducer from the Session to the Topic or Queue
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    }

    public synchronized void sendMessage(){
        try {
            TextMessage message = session.createTextMessage("Test user message");
            // Tell the producer to send the message
            System.out.println("Sent message: " + message.getText()));
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


}