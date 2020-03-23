package es.ozona.ignite.poc.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import es.ozona.ignite.poc.mq.math.RandomUtils;

public class Worker implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
	private final ConnectionFactory factory = new ConnectionFactory();
	private static final int MAX_WAIT_IN_MILLIS = 1000;
	private static final int MIN_WAIT_IN_MILLIS = 500;
	private String queueName;
	private String hostName;
	private String name;
	private int preFetch;

	public Worker(String name, String queueName, String hostName, int preFetch) {
		this.queueName = queueName;
		this.hostName = hostName;
		this.preFetch = preFetch;
		this.name = name;
	}

	@Override
	public void run() {
		
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {

			System.out.println(String.format("-> Worker, channel join [%s->%s]", hostName, queueName));

			final boolean durable = true; // persist queue against RabbitMQ shutdown or crash
			channel.queueDeclare(queueName, durable, false, false, null);
			channel.basicQos(preFetch); // messages sent until ACK.

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
					
				try {
					doWork(message);			       				
				} catch (InterruptedException e) {
					// finish internal thread.
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					// log error.
					LOG.error("{}: Task processing failed.", name, e);
				} finally {
					// send ACK
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					System.out.println(String.format("%s: [x] %s Done",name, message));
				}

			};

			channel.basicConsume(queueName, /*autoACK*/ false, deliverCallback, consumerTag -> {
			});

			while (true) {
				Thread.sleep(MAX_WAIT_IN_MILLIS);
			}

		} catch (InterruptedException e) {
			LOG.info("{}: thread interrupted.", name);
		} catch (Exception e) {
			LOG.error("{}: connection failed.", name, e);
		}

	}

	public void doWork(String task) throws InterruptedException {
		
		LOG.info(" [*] {}: received Task {}", name, task);

		final int waitFor = RandomUtils.randomInt(MIN_WAIT_IN_MILLIS, MAX_WAIT_IN_MILLIS);		
		LOG.debug("{}: waiting for {} seconds.", name, waitFor);
		
		Thread.sleep(waitFor);        		
	}

}
