//This class is used for recording all the information of the items in the inventory.
//which can help to contribute the different items with different requirements.
//This class is used for recording all the information of items in the inventory.

public class Item {
	//all the information of items in the database
	private String id;
	private String type;
	private String[] typeVariables;
	private String price;
	private String manuID;
	
	//constructor of Item with no argument
	//constructor without arguments, just create and empty object
	/**
	 * Constructor for the Item object without parameters
	 */
	public Item() {
		this.id = "";
		this.type = "";
		this.typeVariables = new String[0];
		this.price = "";
		this.manuID = "";
	}

	/**
	 * Constructor for the Item object
	 * @param Item ID
	 * @param Item Type
	 * @param Array of Item's variables : Legs, Arms, Seat, Cushion
	 * @param Item Price
	 * @param Item Manufacturer ID
	 */
	//constructor of Item with five arguments
	//constructor with five specified arguments, create a specified item
	public Item(String id, String type,String[] variables ,String price, String manuID) {
		this.id = id;
		this.type = type;
		this.typeVariables = variables;
		this.price = price;
		this.manuID = manuID;
	}
	
	//setters for all private variables
	/**
	 * Setter for the item ID
	 * @param Item ID
	 */
	public void setId(String id){
		this.id = id;
	}
	/**
	 * Getter method for item ID
	 * @return id of item
	 */
	//getter of all private variables
	public String getId() {
		return id;
	}
	
	/**
	 * Getter method for item Type
	 * @return type of item
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Getter method for item TypeVariables
	 * @return variables array of item
	 */
	public String[] getTypeVariables() {
		return typeVariables;
	}
	/**
	 * Getter method for item Price
	 * @return price of item
	 */
	public String getPrice() {
		return price;
	}
	/**
	 * Getter method for item Manufacturer ID
	 * @return manuID of item
	 */
	public String getManuID() {
		return manuID;
	}
	
}
