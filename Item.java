//This class is used for recording all the information of the items in the inventory.
//which can help to contribute the different items with different requirements.
public class Item {

	private String id;
	private String type;
	private String[] typeVariables;
	private String price;
	private String manuID;
	
	//constructor of Item with no argument
	public Item() {
		this.id = "";
		this.type = "";
		this.typeVariables = new String[0];
		this.price = "";
		this.manuID = "";
	}
	//constructor of Item with five arguments
	public Item(String id, String type,String[] variables ,String price, String manuID) {
		this.id = id;
		this.type = type;
		this.typeVariables = variables;
		this.price = price;
		this.manuID = manuID;
	}

	//getters for all private variables
	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String[] getTypeVariables() {
		return typeVariables;
	}

	public String getPrice() {
		return price;
	}

	public String getManuID() {
		return manuID;
	}
	
}
