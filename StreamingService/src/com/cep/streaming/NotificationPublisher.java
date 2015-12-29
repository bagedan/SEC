package com.cep.streaming;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class NotificationPublisher {

    public  String queueId = "USER";
    public  String BORKE_URL = "tcp://localhost:61616";
    private Session session;
    private MessageProducer producer;


    public NotificationPublisher() {
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

    public synchronized void sendMessage(String text){
        try {
        	
            TextMessage message = session.createTextMessage(text);
            // Tell the producer to send the message
            System.out.println("Sent message: " + message);
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
