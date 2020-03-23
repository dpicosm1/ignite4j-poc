package es.ozona.ignite.poc.mq;

public class FanoutPubsub {
	private static final String EXCHANGE_NAME = "FanoutExchange";
	private static final String HOST_NAME = "localhost";
	private static final int taskCount = 50;
	private static final int subscriberCount = 5;

	public static void main(String[] argv) throws InterruptedException {
		final Thread publisher = new Thread(new Publisher(EXCHANGE_NAME, HOST_NAME, taskCount));
		Thread[] subscribers = initSubscribers(subscriberCount);

		// Starting threads
		publisher.start();
		Thread.sleep(200);

		startsubscribers(subscribers);

		// Wait for thread finish
		publisher.join();

		// Wait 5 seconds for subscribers;
		for (int i = 0; i < 5; i++) {
			System.out.println(String.format(" ... Waiting %d second(s).", i + 1));
			Thread.sleep(1000);
		}

		forceStopsubscribers(subscribers);

		System.out.println("- TaksQueue reading finish -");
	}

	private static Thread[] initSubscribers(int subscriberCount) {
		final Thread[] subscribers = new Thread[subscriberCount];

		for (int i = 0; i < subscriberCount; i++) {
			subscribers[i] = new Thread(new Subscriber("Subscriber_"+i, EXCHANGE_NAME, HOST_NAME));
		}
		return subscribers;
	}

	private static void startsubscribers(Thread[] subscribers) {
		for (Thread subscriber : subscribers) {
			subscriber.start();
		}
	}

	private static void forceStopsubscribers(Thread[] subscribers) throws InterruptedException {
		for (Thread subscriber : subscribers) {
			if (subscriber.isAlive()) {
				subscriber.interrupt();
			}
			subscriber.join();
		}
	}
}
