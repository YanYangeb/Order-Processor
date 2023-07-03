package processor;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class helps every Thread access a singular list of items. This class
 * will be instantiated once for the purposes of this particular program. But,
 * for purposes of flexibility in the future, it will be left to have the
 * ability to be instantiated more than once, which is why it is not a Singleton
 * or static class.
 * 
 * @author yanitgeb
 *
 */
public class Items {
	private ArrayList<Item> listOfItems; // the ArrayList to be accessed, read,
	// and manipulated by all Threads

	/**
	 * This constructor will instantiate the list of items from the Items Data
	 * file
	 */
	public Items() {
		listOfItems = new ArrayList<Item>();
	}

	/**
	 * This method processes the items in the ItemsData file and creates a new
	 * Item with its name and cost and adds it to the ArrayList.
	 * 
	 * @param fileName
	 */
	public void processItemsFile(String fileName) {

		// reads the item Name and item costs with two different set of
		// BufferedReaders
		try {
			BufferedReader itemsFile = new BufferedReader(
					new FileReader(new File(fileName)));
			BufferedReader itemsCostValues = new BufferedReader(
					new FileReader(new File(fileName)));
			try {
				while (itemsFile.ready()) {
					String itemName = itemsFile.readLine().split(" ")[0];
					String itemCosts = itemsCostValues.readLine().split(" ")[1];

					// parses a double value from the item Costs string
					double itemCost = Double.parseDouble(itemCosts);
					Item newItem = new Item(itemName, itemCost);

					listOfItems.add(newItem);
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
	 * This method looks through the items within the ArrayList and processes
	 * the order summary of all of the bought items. This will also include the
	 * total number of each item bought from all the clients combined.
	 * 
	 * @return orderSummary
	 */
	public String processOrdersSummary() {

		double summaryTotal = 0.0;
		StringBuilder report = new StringBuilder("");
		Collections.sort(listOfItems);

		// goes through the list of items in alphabetical order and creates a
		// summary for each one
		for (int i = 0; i < listOfItems.size(); i++) {
			int numSold = listOfItems.get(i).getNumSold();
			if (numSold != 0) {
				report.append("Summary - ");
				report.append("Item's name: " + listOfItems.get(i).getItemName()
						+ ", ");
				double cost = listOfItems.get(i).getCost();
				report.append("Cost per item: "
						+ NumberFormat.getCurrencyInstance().format(cost)
						+ ", ");
				report.append("Number sold: " + numSold + ", ");
				double totalCost = listOfItems.get(i).getTotalCost(numSold);
				report.append("Item's Total: "
						+ NumberFormat.getCurrencyInstance().format(totalCost)
						+ "\n");

				summaryTotal += totalCost;
			}
		}

		report.append("Summary Grand Total: "
				+ NumberFormat.getCurrencyInstance().format(summaryTotal)
				+ "\n");

		return report.toString();
	}

	/**
	 * This returns the item from the ArrayList just by putting the name of the
	 * item down
	 * 
	 * @param item
	 * @return if item exists, it will return the item, otherwise it will return
	 *         null
	 */
	public Item getItem(String item) {
		for (Item item1 : listOfItems) {
			if (item1.getItemName().equals(item)) {
				return item1;
			}
		}

		return null;
	}

	/**
	 * This method is important just in case there is an item that was
	 * instantiated without a price since the Item class has two constructors
	 * 
	 * @param item
	 */
	public void setCostOfItem(Item item) {
		for (Item item1 : listOfItems) {
			if (item1.getItemName().equals(item.getItemName())) {
				item.setPrice(item1.getCost());
			}
		}
	}

	/**
	 * This method returns the ArrayList of items read from the ItemData file in
	 * alphabetical order
	 * 
	 * @return
	 */
	public ArrayList<Item> getAllItems() {
		Collections.sort(listOfItems);
		return listOfItems;
	}

}
