package com.cep.event.generators;

import com.cep.event.Event;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class EventPublisher {

    final static int ARTICLES_COUNT = 10;
    final static int USERS_COUNT = 10;

    private static final String queue = "EVENTS";
    private static final String brokerUrl = "tcp://localhost:61616";
    private Session session;
    private MessageProducer producer;


    public EventPublisher() {
        try {
            initSession();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void initSession() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue(queue);

        // Create a MessageProducer from the Session to the Topic or Queue
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    }

    public void sendMessage(Event event){
        try {
            ObjectMessage message = session.createObjectMessage(event);
            // Tell the producer to send the message
            System.out.println("Sent message: " + message.getJMSMessageID());
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
