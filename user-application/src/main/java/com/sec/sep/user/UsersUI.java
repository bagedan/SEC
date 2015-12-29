package com.sec.sep.user;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by Tkachi on 2015/12/29.
 */
public class UsersUI implements ExceptionListener {

    private static String queue = "USER";
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

        System.out.println("Receiver started");
        UsersUI usersUI = new UsersUI();
        usersUI.startListening();
    }

    private void startListening() {
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
                    System.out.println("Notification received: " + textMessage.getText());
                } else {
                    System.out.println("Received some strange message: " + message);
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

}
