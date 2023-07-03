package processor;

import java.io.BufferedReader;

import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;

/**
 * This class processes the order of each client. Every client has their own
 * list of bought items, client report of their orders, a record of how many of
 * each item they have bought. Every client has an ID of their own as well and
 * file to read from the many order files to process. Because of this, the file
 * reading will occur within this class and it will extend Thread so that each
 * client acts as their own Thread to read each individual file.
 * 
 * @author yanitgeb
 *
 */
public class Client extends Thread {

	private int clientId;
	private ArrayList<Item> listOfBoughtItems;
	private TreeMap<Integer, Item> quantityOfItems = 
												new TreeMap<Integer, Item>();
	private ArrayList<Item> listOfSingularItems;
	private String currentFileName;
	private Items setOfItems; // list of items to be processed for buying
	private String clientReport;
	private Object lock; // lock for synchronization

	/**
	 * This constructor is used when instantiating a client for multi-threading.
	 * It makes sure that the Object "lock" is directly pointing to the one
	 * passed in so that every synchronization can lock in on the same thing. An
	 * instance of Items is also passed in, which also has a shallow reference
	 * so that the list of items' data is shared by all clients.
	 * 
	 * @param id
	 * @param lock
	 * @param fileName
	 * @param setOfItems
	 */
	public Client(int id, Object lock, String fileName, Items setOfItems) {
		// every client has a set of items they have bought
		clientId = 1000 + id;
		listOfBoughtItems = new ArrayList<Item>();
		listOfSingularItems = new ArrayList<Item>();
		currentFileName = fileName + id + ".txt";

		this.setOfItems = setOfItems;

		clientReport = new String("");

		this.lock = lock;

	}

	/**
	 * This simple constructor is used to instantiate a client for single
	 * threading since the id is the only information to work with for now. The
	 * two ArrayLists are instantiated appropriately. The list of bought items
	 * is a bit different from list of singular items. The bought items contains
	 * duplicates to be used for counting the quantity bought. The list of
	 * singular items bought is to allow access to the list of bought items
	 * without any duplicates.
	 * 
	 * @param id
	 */
	public Client(int id) {
		// every client has a set of items they have bought
		clientId = 1000 + id;
		listOfBoughtItems = new ArrayList<Item>();
		listOfSingularItems = new ArrayList<Item>();
	}

	/**
	 * This method will be used for multi-threading processing. It will set the
	 * clientID from the current file of the client just in case the ID is not
	 * 1000 + a number. It will process the purchased Items of the client and
	 * create the Array of quantities all outside of synchronization since that
	 * is not required to prevent data race. The only one that is synchronized
	 * with the lock is the method where the program writes the client report
	 * since it has access to "setOfItems" which is shared by every client.
	 */
	public void run() {
		this.setClientID(currentFileName);
		System.out.println("Reading order for client with id: " + clientId);
		this.processPurchasedItems(currentFileName);
		int[] itemQuantity = this.createArrayOfQuantities();

		// the threads will access an ArrayList in Items, so synchronization is
		// required
		synchronized (lock) {
			this.writeClientReport(setOfItems, itemQuantity);

		}

	}

	/**
	 * This method writes the report of the client's order. It uses the items
	 * ArrayList to access the price of each item bought by the client. It also
	 * uses the ArrayList inside the Items class to update the number sold of an
	 * item within an instance of it. This will be important later on when we
	 * write the summary of all the bought items by every single client
	 * together.
	 * 
	 * @param setOfItems
	 * @param itemQuantity
	 */
	public void writeClientReport(Items setOfItems, int[] itemQuantity) {
		StringBuilder report = new StringBuilder("");
		ArrayList<Item> items;
		String itemName = "";
		double costOfItem = 0.0;
		int quantity = 0;
		double totalCost = 0.0;

		double orderTotal = 0.0;
		report.append("----- Order details for client with Id: " + clientId
				+ " -----" + "\n");

		// runs through list of singular items to process the item name, price,
		// quantity, and total cost
		items = setOfItems.getAllItems();
		for (int index = 0; index < listOfSingularItems.size(); index++) {
			itemName = listOfSingularItems.get(index).getItemName();
			// in case the Item was instantiated without a cost
			setOfItems.setCostOfItem(listOfSingularItems.get(index));
			costOfItem = listOfSingularItems.get(index).getCost(); // gets cost
			// of a particular item
			quantity = itemQuantity[index]; // obtains quantity bought
			totalCost = listOfSingularItems.get(index).getTotalCost(quantity);

			// updates total number sold of each particular item
			for (Item item : items) {
				if (item.getItemName().equals(itemName)) {
					item.updateNumberSold(quantity);
				}
			}

			orderTotal += totalCost; // obtains the total cost of the order

			report.append("Item's name: " + itemName + ", ");
			report.append("Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(costOfItem)
					+ ", ");
			report.append("Quantity: " + quantity + ", ");
			report.append("Cost: "
					+ NumberFormat.getCurrencyInstance().format(totalCost)
					+ "\n");

		}

		report.append("Order Total: "
				+ NumberFormat.getCurrencyInstance().format(orderTotal) + "\n");

		clientReport = report.toString(); // updates client report

	}

	/**
	 * This method returns the client report after it has been created
	 * 
	 * @return clientReport
	 */
	public String getClientReport() {
		return clientReport;
	}

	/**
	 * It will return the clentID
	 * 
	 * @return clientId
	 */
	public int getClientID() {
		return clientId;
	}

	/**
	 * This method returns the list of items with all the duplicates
	 * 
	 * @return listOfBoughtItems
	 */
	public ArrayList<Item> getListOfItems() {
		return listOfBoughtItems;
	}

	/**
	 * This method returns the list of bought items without any duplicated
	 * 
	 * @return
	 */
	public ArrayList<Item> getListOfBoughtItems() {
		Collections.sort(listOfSingularItems);
		return listOfSingularItems;
	}

	/**
	 * This method adds an item to the list of bought items as necessary
	 * 
	 * @param newItem
	 */
	public void addItem(Item newItem) {
		listOfBoughtItems.add(newItem);
	}

	/**
	 * This method returns the current client
	 * 
	 * @return
	 */
	public Client getClient() {
		return this;
	}

	/**
	 * This method reads the file associated with the client and processes all
	 * of the items they have bought. It also updates the TreeMap for quantity
	 * of items since I want each item to be associated with an index value in
	 * order to calculate the quantity integer array later on.
	 * 
	 * @param receiptFile
	 */
	public void processPurchasedItems(String receiptFile) {
		// reads through the whole file and adds each item to the ArrayList
		try {
			BufferedReader itemsFile = new BufferedReader(
					new FileReader(new File(receiptFile)));
			try {
				itemsFile.readLine(); // skips first line
				while (itemsFile.ready()) {
					String itemName = itemsFile.readLine().split(" ")[0];

					Item newItem = new Item(itemName); // creates new Item

					listOfBoughtItems.add(newItem);
				}

				// After it is done reading, close the file
				try {
					itemsFile.close();
				} catch (Exception e) {
					System.out.println("Error!");
					e.printStackTrace();
				}

			} catch (Exception e) {
				System.out.println("Error in Reading Files!");
			}
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
		}

		// sorts the list of Items in alphabetical order as Item is Comparable
		Collections.sort(listOfBoughtItems);

		int newItemCount = 0;

		// updates the list of Singular Items and Quantity of items TreeMap to
		// help get rid of any duplicates and to help create the Array of the
		// quantities of each item now that they are associated with an integer
		// value

		for (int i = 0; i < listOfBoughtItems.size(); i++) {
			if (!quantityOfItems.containsValue(listOfBoughtItems.get(i))) {
				quantityOfItems.put(newItemCount++, listOfBoughtItems.get(i));
				listOfSingularItems.add(listOfBoughtItems.get(i));

			}
		}

		Collections.sort(listOfSingularItems);
	}

	/**
	 * This method sets the ClientID in the case where we have a change in the
	 * numbering of client IDs and we need to get them directly from the order
	 * files themselves. This method is also helpful when we want to obtain the
	 * client ID without actually reading the whole purchase list first. For
	 * example, we want to print that we are reading the order for a client with
	 * an ID before actually reading that order.
	 * 
	 * @param receiptFile
	 */
	public void setClientID(String receiptFile) {
		String clientNum = "";
		try {
			BufferedReader clientFile = new BufferedReader(
					new FileReader(new File(receiptFile)));
			try {
				clientNum = clientFile.readLine().split(" ")[1];
				clientId = Integer.parseInt(clientNum);

				try {
					clientFile.close();
				} catch (Exception e) {
					System.out.println("Error!");
					e.printStackTrace();
				}

			} catch (Exception e) {
				System.out.println("No More Files!");
			}
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return array of quantities respective to the integer value of an Items
	 */
	public int[] createArrayOfQuantities() {
		int num = quantityOfItems.keySet().size(); // updates size of TreeMap
		int quantityValue = 0;
		int[] array = new int[num]; // instantiates an array with the size of
									// the TreeMap

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < listOfBoughtItems.size(); j++) {
				if (listOfBoughtItems.get(j).equals(quantityOfItems.get(i))) {
					quantityValue++; // counts how many of each Item there is
					array[i] = quantityValue;
				}
			}
			quantityValue = 0; // resets quantity counter before next iteration
			// of outer array
		}

		return array;
	}

	/**
	 * This method returns the Map of the items in case it is needed
	 * 
	 * @return quantityOfItems
	 */
	public TreeMap<Integer, Item> getMapOfItems() {
		return quantityOfItems;
	}

}
