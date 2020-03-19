package es.ozona.ignite.poc.mq;

public class SimpleMQ {

	private static final String QUEUE_NAME = "SimpleMQ";
	private static final String HOST_NAME = "localhost";

	public static void main(String[] args) throws InterruptedException {

		final Thread producer = new Thread(new Producer(QUEUE_NAME, HOST_NAME, 5));
		final Thread consumer = new Thread(new Consumer(QUEUE_NAME, HOST_NAME));

		// Starting threads
		producer.start();
		Thread.sleep(200);
		consumer.start();

		// Wait for thread finish
		producer.join();

		// Wait for thread consumer;
		for (int i = 0; i < 5; i++) {
			System.out.println(String.format(" ... Waiting %d second(s).", i + 1));
			Thread.sleep(1000);
		}

		// Force thread consumer to finish
		if (consumer.isAlive()) {
			consumer.interrupt();
		}

		consumer.join();

		System.out.println("- Queue reading finish -");
	}

}
