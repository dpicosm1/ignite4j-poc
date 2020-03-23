package es.ozona.ignite.poc.mq;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import es.ozona.ignite.poc.mq.math.RandomUtils;

public class TaskProducer implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(TaskProducer.class);
	private static final int MAX_WAIT_IN_MILLIS = 3000;
	private static final int MIN_WAIT_IN_MILLIS = 1000;

	private final ConnectionFactory factory = new ConnectionFactory();
	private String queueName;
	private String hostName;
	private int messageCount;

	public TaskProducer(String queueName, String hostName, int messageCount) {
		this.queueName = queueName;
		this.hostName = hostName;
		this.messageCount = messageCount;
	}

	@Override
	public void run() {

		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

			System.out.println(String.format("-> Producer, channel created [%s->%s]", hostName, queueName));

			final boolean durable = true; // persist queue against RabbitMQ shutdown or crash
			channel.queueDeclare(queueName, durable, false, false, null);
			String message;

			int i = messageCount;
			while (i-- > 0) {
				message = String.format("Task [%d/%d].", messageCount - i, messageCount);
				channel.basicPublish("", queueName, /* message type to persist */ MessageProperties.PERSISTENT_TEXT_PLAIN
						, message.getBytes(StandardCharsets.UTF_8));

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
