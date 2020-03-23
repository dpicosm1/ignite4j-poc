package es.ozona.ignite.poc.mq;

public class TaskQueue {

	private static final String QUEUE_NAME = "MyTaskQueue";
	private static final String HOST_NAME = "localhost";
	private static final int preFetch = 1;
	private static final int taskCount = 50;
	private static final int workerCount = 5;

	public static void main(String[] argv) throws InterruptedException {
		final Thread producer = new Thread(new TaskProducer(QUEUE_NAME, HOST_NAME, taskCount));
		Thread[] workers = initWokers(workerCount);

		// Starting threads
		producer.start();
		Thread.sleep(200);

		startWorkers(workers);

		// Wait for thread finish
		producer.join();

		// Wait 5 seconds for workers;
		for (int i = 0; i < 5; i++) {
			System.out.println(String.format(" ... Waiting %d second(s).", i + 1));
			Thread.sleep(1000);
		}

		forceStopWorkers(workers);

		System.out.println("- TaksQueue reading finish -");
	}

	private static Thread[] initWokers(int workerCount) {
		final Thread[] workers = new Thread[workerCount];

		for (int i = 0; i < workerCount; i++) {
			workers[i] = new Thread(new Worker("Worker_"+i, QUEUE_NAME, HOST_NAME, preFetch));
		}
		return workers;
	}

	private static void startWorkers(Thread[] workers) {
		for (Thread worker : workers) {
			worker.start();
		}
	}

	private static void forceStopWorkers(Thread[] workers) throws InterruptedException {
		for (Thread worker : workers) {
			if (worker.isAlive()) {
				worker.interrupt();
			}
			worker.join();
		}
	}

}
