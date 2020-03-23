package es.ozona.poc.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import es.ozona.ignite.poc.mq.math.RandomUtils;

public class Subscriber implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(Subscriber.class);
	private static final String EXCHANGE_TYPE_FANOUT = "fanout";
	private static final int MAX_WAIT_IN_MILLIS = 1000;
	private static final int MIN_WAIT_IN_MILLIS = 500;

	private final ConnectionFactory factory = new ConnectionFactory();

	private String name;
	private String exchangeName;
	private String hostName;

	public Subscriber(String name, String exchangeName, String hostName) {
		this.name = name;
		this.exchangeName = exchangeName;
		this.hostName = hostName;
	}

	@Override
	public void run() {

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel();) {

			final boolean durable = true; // persist queue against RabbitMQ shutdown or crash
			channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE_FANOUT, durable);

			final String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exchangeName, "");

			System.out.println(String.format("-> Subscriber, channel join [%s->%s]", hostName, queueName));

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
					System.out.println(String.format("%s: [x] %s Done", name, message));
				}

			};

			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
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
