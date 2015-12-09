package com.cep.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import sun.misc.IOUtils;

import javax.jms.*;

/**
 * Created by Tkachi on 2015/12/9.
 */
public class Receiver implements ExceptionListener, MessageListener {

    private static String queue = "EVENTS";
    private static String brokerUrl = "tcp://localhost:61616";
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public static void main(String[] args) {
        if(args.length == 2){
            System.out.println("Arguments provided: [" + args[0] + "] : [" + args[1] + "]");
            brokerUrl = args[0];
            queue = args[1];
        }else{
            System.out.println("Arguments are not provided. Using defaults: [" + brokerUrl + "] : [" + queue + "]");
        }
        Receiver receiver = new Receiver();
        System.out.println("Receiver started");
        receiver.recieveForever();
    }

    public void recieveForever() {
        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(this.queue);

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

            while(true) {
                // Wait for a message
                Message message = consumer.receive(10000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: " + text);
                } else {
                    System.out.println("Received: " + message);
                }

            }

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }finally{
            try {
                consumer.close();
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client." + ex);
    }

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String text = null;
            try {
                text = textMessage.getText();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            System.out.println("Received: " + text);
        } else {
            System.out.println("Received: " + message);
        }
    }
}
