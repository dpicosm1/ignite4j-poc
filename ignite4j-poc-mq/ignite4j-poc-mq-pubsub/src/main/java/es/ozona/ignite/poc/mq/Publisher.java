package es.ozona.ignite.poc.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import es.ozona.ignite.poc.mq.math.RandomUtils;

public class Publisher implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(Publisher.class);
	private static final int MAX_WAIT_IN_MILLIS = 3000;
	private static final int MIN_WAIT_IN_MILLIS = 1000;

	private final ConnectionFactory factory = new ConnectionFactory();
	private String exchangeName;
	private String hostName;
	private int messageCount;

	public Publisher(String exchangeName, String hostName, int messageCount) {
		this.exchangeName = exchangeName;
		this.hostName = hostName;
		this.messageCount = messageCount;
	}

	@Override
	public void run() {

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			final boolean durable = true;
			channel.exchangeDeclare(exchangeName, "fanout", durable);

			System.out.println(String.format("-> Publisher, channel created [%s-(E)>%s]", hostName, exchangeName));

			String message;
			int i = messageCount;
			while (i-- > 0) {
				message = String.format("Task [%d/%d].", messageCount - i, messageCount);
				channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));

				System.out.println(String.format("  [x] Sent: %s", message));

				final int waitFor = RandomUtils.randomInt(MIN_WAIT_IN_MILLIS, MAX_WAIT_IN_MILLIS);
				LOG.debug("Waiting for {} seconds.", waitFor);

				Thread.sleep(waitFor);
			}

		} catch (Exception e) {
			LOG.error("Connection failed.", e);
		}

	}

}
