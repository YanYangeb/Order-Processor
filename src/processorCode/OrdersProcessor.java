package processor;

import java.util.*;

import java.io.*;

public class OrdersProcessor {

	public static void main(String[] args) {

		Scanner newScanner = new Scanner(System.in); // scanner to scan user
														// input for processing
														// orders

		// Scanning information

		String fileName = newScanner.nextLine(); // reads file name for items

		String multiOrNot = newScanner.next(); // reads whether we are using
												// multi-thread or not

		int num = newScanner.nextInt(); // reads how many orders to process

		String baseName = newScanner.next(); // reads the order files for each
												// client

		String resultName = newScanner.next(); // file where results are written

		long startTime = System.currentTimeMillis();

		newScanner.close();

		// Processing and printing available information

		Items setOfItems = new Items(); // An instance of Items to create a new
										// ArrayList to be shared

		System.out.println("Enter item's data file name: " + fileName);
		System.out.println(
				"Enter 'y' for multiple threads, any other character otherwise: "
						+ multiOrNot);
		System.out.println("Enter number of orders to process: " + num);
		System.out.println("Enter order's base filename: " + baseName);
		System.out.println("Enter result's filename: " + resultName);

		setOfItems.processItemsFile(fileName); // reads items file

		// if the program calls for multi-Thread

		if (multiOrNot.equals("y")) {
			Object lock = new Object(); // a lock to help with synchronization
			StringBuilder multireport = new StringBuilder("");

			// ArrayList of clients to begin reading the orders of each one
			ArrayList<Client> allClients = new ArrayList<Client>();

			// all Clients have access to the same ArrayList data structure
			// inside the class, Items which is passed into their constructor
			// The items class helps other classes manipulate and access a
			// single ArrayList of items
			for (int i = 1; i <= num; i++) {
				allClients.add(new Client(i, lock, baseName, setOfItems));
			}

			for (Client client : allClients) {
				client.start(); // start the thread for each Client
			}

			for (Client client : allClients) {
				try {
					client.join(); // join the threads
				} catch (InterruptedException e) {
					System.out.println("Interrupted!");
					e.printStackTrace();
				}

				multireport.append(client.getClientReport()); // update
																// multi-report
																// with the
																// client report

			}

			// when all client reports are finished, create a summary report

			multireport.append("***** Summary of all orders *****" + "\n");
			multireport.append(setOfItems.processOrdersSummary());

			// write to the result file once everything has been appended
			try {
				FileWriter newWrite = new FileWriter(resultName);
				newWrite.write(multireport.toString());
				newWrite.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// process the time it took to process and write everything

			long endTime = System.currentTimeMillis();

			System.out.println(
					"Processing time (msec): " + (endTime - startTime));

			System.out
					.println("Results can be found in the file: " + resultName);

		}

		// if the scanner detected a letter other than y, process with single
		// thread
		if (!multiOrNot.equals("y")) {
			StringBuilder report = new StringBuilder("");

			// process client orders

			for (int i = 1; i <= num; i++) {
				String orderFileName = baseName + i + ".txt";
				Client newClient = new Client(i);
				newClient.setClientID(orderFileName); // obtain clientID from
														// order File
				int clientID = newClient.getClientID();
				System.out.println(
						"Reading order for client with id: " + clientID);

				newClient.processPurchasedItems(orderFileName);

				// how much of each item, the client has bought
				int[] itemQuantity = newClient.createArrayOfQuantities();

				newClient.writeClientReport(setOfItems, itemQuantity);

				report.append(newClient.getClientReport());

			}

			// process orders Summary and complete the report String
			report.append("***** Summary of all orders *****" + "\n");

			report.append(setOfItems.processOrdersSummary());

			// write the complete report to the results File
			try {
				FileWriter newWrite = new FileWriter(resultName);
				newWrite.write(report.toString());
				newWrite.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// process the timing of the whole process
			long endTime = System.currentTimeMillis();

			System.out.println(
					"Processing time (msec): " + (endTime - startTime));

			System.out
					.println("Results can be found in the file: " + resultName);

		}

	}

}
