package com.cep.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;

/**
 * Created by Tkachi on 2015/12/9.
 */
public class Publisher {

    private static String queue = "EVENTS";
    private static String brokerUrl = "tcp://localhost:61616";
    private Connection connection;
    private Session session;

    public static void main(String[] args) {
        if(args.length == 2){
            System.out.println("Arguments provided: [" + args[0] + "] : [" + args[1] + "]");
            brokerUrl = args[0];
            queue = args[1];
        }else{
            System.out.println("Arguments are not provided. Using defaults: [" + brokerUrl + "] : [" + queue + "]");
        }

        Publisher publisher = new Publisher();
        System.out.println("Publisher started");
        publisher.publishMessagesForever();
    }

    public void publishMessagesForever() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queue);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            int i = 0;
            while(true){
                // Create a messages
                String text = "Message No " + i + " From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);

                // Tell the producer to send the message
                System.out.println("Sent message No " + i);
                producer.send(message);
                i++;
                Thread.sleep(1000);
            }


        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }finally{
            if(connection!=null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if(session!=null){
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
