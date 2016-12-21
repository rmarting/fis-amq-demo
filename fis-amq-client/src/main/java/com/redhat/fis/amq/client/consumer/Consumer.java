package com.redhat.fis.amq.client.consumer;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;

public class Consumer implements Runnable, ExceptionListener {

    private String connectionString = null;

    private String usr = null;

    private String pwd = null;

    private String queue = null;

    public Consumer(String connectionString, String queue) {
        this.connectionString = connectionString;
        this.queue = queue;
    }

    public Consumer(String connectionString, String queue, String usr, String pwd) {
        this.connectionString = connectionString;
        this.queue = queue;
        this.usr = usr;
        this.pwd = pwd;
    }

    @Override
    public void run() {
        // Create a ConnectionFactory
		// Create a ConnectionFactory
		ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory(this.connectionString);

		if (null != this.usr && null != this.pwd) {
			connectionFactory.setUserName(this.usr);
			connectionFactory.setPassword(this.pwd);
		}

		// SSL Security
		try {
			connectionFactory.setTrustStore("amq-consumer-client.ks");
			connectionFactory.setTrustStorePassword("123456");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable set up SSL crendentials", e);
		}

        try (
                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);) {

            connection.start();
            connection.setExceptionListener(this);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(this.queue);

            try (
                    // Create a MessageConsumer from the Session to the Topic or Queue
                    MessageConsumer consumer = session.createConsumer(destination);) {
                // Wait for a message
                Message message = consumer.receive(1000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: '" + text + "'");
                } else {
                    System.out.println("Received: " + message);
                }
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }

}
