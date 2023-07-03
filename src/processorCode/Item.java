package processor;

import java.util.Comparator;

/**
 * This class contains information that a particular Item would hold, including
 * its name, cost, and how much of it has been sold.
 * 
 * @author yanitgeb
 *
 */
public class Item implements Comparable<Item> {

	private double itemCost;
	private String name;
	private int numberSold;

	/**
	 * This constructor instantiates an item with a name and cost. It also sets
	 * up the number sold as 0.
	 * 
	 * @param itemName
	 * @param cost
	 */
	public Item(String itemName, double cost) {
		name = itemName;
		itemCost = cost;
		numberSold = 0;

	}

	/**
	 * This constructor instantiates an item with just a name and sets up the
	 * number sold as 0.
	 * 
	 * @param itemName
	 */
	public Item(String itemName) {
		name = itemName;
		numberSold = 0;
	}

	/**
	 * This method will set the price of this particular item if it was not
	 * instantiated with a price
	 * 
	 * @param price
	 */
	public void setPrice(double price) {
		itemCost = price;
	}

	/**
	 * This method will update the number of this particular item sold
	 * 
	 * @param num
	 */
	public void updateNumberSold(int num) {
		numberSold += num;
	}

	/**
	 * This method will return the number of times this item has been sold
	 * 
	 * @return numberSold
	 */
	public int getNumSold() {
		return numberSold;
	}

	/**
	 * This method will return the total cost based on how many times this item
	 * was bought
	 * 
	 * @param quantity
	 * @return totalCost
	 */
	public double getTotalCost(int quantity) {
		return itemCost * quantity;
	}

	/**
	 * This method returns the item name
	 * 
	 * @return name
	 */
	public String getItemName() {
		return name;
	}

	/**
	 * This method returns the item cost
	 * 
	 * @return itemCost
	 */
	public double getCost() {
		return itemCost;
	}

	/**
	 * This method overrides the equals method so that an Item Object can
	 * compared with another using the equals method.
	 * 
	 * @param item
	 */
	@Override
	public boolean equals(Object item) {
		if (item == this) {
			return true;
		}

		if (!(item instanceof Item)) {
			return false;
		}

		Item newItem = (Item) item;

		return this.name.equals(newItem.name);
	}

	/**
	 * This method overrides the compareTo method so that the Item can be sorted
	 * into alphabetical order as necessary.
	 * 
	 * @param item
	 */
	@Override
	public int compareTo(Item item) {
		Comparator<String> comparator = String.CASE_INSENSITIVE_ORDER;
		return comparator.compare(this.name, item.name);
	}

}
