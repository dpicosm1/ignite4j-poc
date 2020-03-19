package es.ozona.ignite.poc.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import es.ozona.ignite.poc.mq.math.RandomUtils;

public class Consumer implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);
	private final ConnectionFactory factory = new ConnectionFactory();
	private static final int MAX_WAIT_IN_MILLIS = 3000;
	private static final int MIN_WAIT_IN_MILLIS = 1000;
	private String queueName;
	private String hostName;

	public Consumer(String queueName, String hostName) {
		this.queueName = queueName;
		this.factory.setHost(hostName);
		this.hostName = hostName;
	}

	@Override
	public void run() {

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {

			System.out.println(String.format("-> Consumer, channel created [%s->%s]", hostName, queueName));

			channel.queueDeclare(queueName, false, false, false, null);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				LOG.info(" [*] Received {}", message);

				final int waitFor = RandomUtils.randomInt(MIN_WAIT_IN_MILLIS, MAX_WAIT_IN_MILLIS);
				LOG.debug("Waiting for {} seconds.", waitFor);

			};

			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
			});

			while (true) {
				Thread.sleep(MAX_WAIT_IN_MILLIS);
			}

		} catch (InterruptedException e) {
			LOG.info("Thread interrupted.");
		} catch (Exception e) {
			LOG.error("Connection failed.", e);
		}

	}

}
