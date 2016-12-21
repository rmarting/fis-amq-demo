package com.redhat.fis.amq.client.producer;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;

public class Producer implements Runnable {

	private String connectionString = null;

	private String usr = null;

	private String pwd = null;

	private String queue = null;

	public Producer(String connectionString, String queue) {
		this.connectionString = connectionString;
		this.queue = queue;
	}

	public Producer(String connectionString, String queue, String usr, String pwd) {
		this.connectionString = connectionString;
		this.queue = queue;
		this.usr = usr;
		this.pwd = pwd;
	}

	@Override
	public void run() {
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
			
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(this.queue);

			// Create a MessageProducer from the Session to the Topic or Queue
			try (MessageProducer producer = session.createProducer(destination);) {
				// producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				producer.setTimeToLive(60 * 60 * 1000);

				// Create a messages
				String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
				TextMessage message = session.createTextMessage(text);

				// Tell the producer to send the message
				System.out.println("Sent Msg: '" + message.getText() + "'");
				producer.send(message);
			} catch (JMSException e) {
				// TODO Manage logs
				e.printStackTrace();
			}
		} catch (JMSException e) {
			// TODO Manage logs
			e.printStackTrace();
		}
	}

}
