//This class is used for recording all the information of items in the inventory.
public class Item {
	//all the information of items in the database
	private String id;
	private String type;
	private String[] typeVariables;
	private String price;
	private String manuID;
	
	//constructor without arguments, just create and empty object
	public Item() {
		this.id = "";
		this.type = "";
		this.typeVariables = new String[0];
		this.price = "";
		this.manuID = "";
	}
	//constructor with five specified arguments, create a specified item
	public Item(String id, String type,String[] variables ,String price, String manuID) {
		this.id = id;
		this.type = type;
		this.typeVariables = variables;
		this.price = price;
		this.manuID = manuID;
	}

	//getter of all private variables
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
