/**
@author ******This needs to be completed
@version 5.2
@since 1.0
*/
/*
In this program, we need to design an application to calculate the cheapest combination of 
available inventory items that can be used to fill a specific order. For example, if someone
need a new chair, the application should look through the database and find all the 
This application should connect with a database, which records all the information of the 
need a new chair, the application should look through the database and find all the components
which can be combined and build a new chair.
It should connect with a database, which records all the information of the 
inventories such as ID, type, price, manufacturer, and condition. The application should find 
the best plan to combine a new furniture we need. 
The best plan must have the good condition and lowest price.
It should accept the users' input for 1) a furniture category, 2) its type, and 3) the number of 
items requested and calculate and output the cheapest option for creating the requested pieces of
furniture or specify if the request is not possible to fill.
*/

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class SupplyChainManager {
	/**Database url*/
	public final String DBURL; 
	/**User username*/
	public final String USERNAME; 
	/**User password*/
	public final String PASSWORD; 

	private Connection dbConnect;
	private ResultSet results;
	
	int quantity = 0;

	/**
	 * Constructor for the SupplyChainManager object
	 * @param databaseURL database URL
	 * @param username MySQL user username
	 * @param password MySQL user password
	 */
	//constructor with three arguments, which are the URL of the database, the username
	//and the password of the local host
	public SupplyChainManager(String databaseURL, String username, String password){
		this.DBURL =  databaseURL; 
		this.USERNAME = username;
		this.PASSWORD = password;
		
		initializeConnection();
	}

	//method initializeConnection is used for connect to the database driver
	public void initializeConnection() {
		try{
			dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			//if failed, throw an SQLException
			e.printStackTrace();
		}
	}

	/**
	 * Takes in the name of the desired item and the table it is located in,
	 * then it will use the OutFile class to write the best combination of
	 * items to an output file. returns a Boolean of whether a combination was found.
	 * @param name name of the item
	 * @param tableName name of the type of item
	 * @param quantity how many items
	 * @return boolean of whether the item was found or not
	 */
	
	public boolean run(String name, String tableName, String quantity) {
		this.quantity = Integer.valueOf(quantity);
		try {
			//selects the best combo and deletes the items out of the database
			//throws NoValidCombinationException if there is no way to make a full item
			ArrayList<ArrayList<Item>> allCombos = new ArrayList<ArrayList<Item>>();
			
			ArrayList<Item> a = selectBestCombination(selectItems(name, tableName));//throws NoValidCombinationException
			allCombos.add(a);
			for(Item e : a) {
				deleteID(e.getId(), tableName); //TODO uncomment
			}
			

				
			//prints out a form for all of the items required to make the desired amount of items
			ArrayList<String> ids = new ArrayList<String>();
			int sum = 0;
			for(ArrayList<Item> ac : allCombos) {
				for(Item e : ac) {
					sum += Integer.valueOf(e.getPrice());
					ids.add(e.getId());
					System.out.println(e.getId());
				}
				OutFile f = new OutFile(name + " " + tableName, quantity, ids, sum);

				f.writeOutFile();
			}
		} catch (Exception e) {
			try {
				//prints out a form saying that no Item could be created
				OutFile f = new OutFile(tableName, quantity, selectManufacturers());
				f.writeNoneAvailable(name);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return false;
		}
		close();
		return true;
	}

	/**
	 * Deletes the Item with the ID id out of the table with tableName in the 
	 * database.
	 * method deleteID is used for deleting an Item with a specified ID id out of the database
	   There are two arguments, id and tableName
	 * @param id ID of the item to be deleted
	 * @param tableName Table to delete the item from
	 */
	public void deleteID(String id, String tableName) {
		String query = "DELETE FROM "+ tableName + " WHERE ID = \'" + id + "\'" ;
		try {
			Statement newStmt = dbConnect.createStatement();
			//execute the statement
			newStmt.executeUpdate(query);
			//release the data
			newStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a table name and an item name and returns an ArrayList of all the of items
	 * from the table.
	 * @param name Name of the item to look for
	 * @param tableName Name of the table the item belongs to
	 * @return Returns an ArrayList with all of the results of the query
	 */
	public ArrayList<Item> selectItems(String name, String tableName) throws Exception{
		String query = "SELECT * FROM "+ tableName + " WHERE Type = \'" + name + "\'" ;
		ArrayList<Item> outputArray = new ArrayList<Item>();
		try {
			Statement newStmt = dbConnect.createStatement();
			//put results into an array to output
			results = newStmt.executeQuery(query);
			while(results.next()) {
				outputArray.add(newItem(tableName));
			}
			//release the data
			newStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// makes sure there are items found in the table
		if(outputArray.size() < 1) { 
			// prints error message "No 'name' tabelName's found" 
			System.err.println("No " + name + " " + tableName + "s found");
			throw new Exception();
		}
		
		return outputArray;
		
		
	}
	
	/**
	 * Returns an array of all manufacturers names from the manufacturer table.
	 * @return ArrayList of the names of the manufacturers
	 * method selectManufacturers, which is used for selecting the items with the 
	   specified manufactures from the table. There is no argument.
	 */
	public ArrayList<String> selectManufacturers(){
		String query = "SELECT * FROM manufacturer";
		ArrayList<String> outputArray = new ArrayList<String>();
		try {
			Statement newStmt = dbConnect.createStatement();
			//put results into an array to output
			results = newStmt.executeQuery(query);
			while(results.next()) {
				outputArray.add(results.getString("Name"));
			}
			//release the data
			newStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return outputArray;
	}
	

	/**
	 * method newItem, which is used for creating a new Item based on what the tableName is.
	 * It uses a switch statement to select the name, then creating an array of 
	 * the items parts data (Y/N) then creating a new item based on the data
	 * @param result result set from a query
	 * @param tableName table that the query was performed on
	 * @return new Item object
	 */
	public Item newItem(String tableName) {
		try {
			switch(tableName) {
			//if chairs are needed
			case "chair":
				//array of the variable columns for the table name
				//each String will be either 'Y' or 'N'
				String[] tableVars = {
						results.getString("Legs"), 
						results.getString("Arms"), results.getString("Seat"), 
						results.getString("Cushion")
						};
				//creating the Item object with the data from results
				return new Item(
						results.getString("ID"), 
						results.getString("Type"),tableVars, 
						results.getString("Price"), 
						results.getString("ManuID")
						);

			case "desk":
				String[] deskVars = {
						results.getString("Legs"), 
						results.getString("Top"), 
						results.getString("Drawer")
						};
				return new Item(
						results.getString("ID"), 
						results.getString("Type"),deskVars,
						results.getString("Price"), 
						results.getString("ManuID")
						);

			case "filing":
				String[] filingVars = {
						results.getString("Rails"), 
						results.getString("Drawers"), 
						results.getString("Cabinet")
						};
				return new Item(
						results.getString("ID"), 
						results.getString("Type"),filingVars, 
						results.getString("Price"), 
						results.getString("ManuID")
						);

			case "lamp":
				String[] lampVars = {
						results.getString("Base"), 
						results.getString("Bulb")
						};
				return new Item(
						results.getString("ID"), 
						results.getString("Type"), lampVars, 
						results.getString("Price"), 
						results.getString("ManuID")
						);
			default: 
				return new Item();
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return null;

	}

	/**
	 * Finds the best combination of items out of the given ArrayList
	 * @param items an ArrayList of Items
	 * @return The ArrayList of Items that fulfills the requirements for the  
	 * lowest price
	 * @throws Exception if there are no valid combinations
	 */
	public ArrayList<Item> selectBestCombination(ArrayList<Item> items) throws Exception{

		int varsLength = items.get(0).getTypeVariables().length;
		// parts holds an array of arrays of items that have each part
		//	example
		//partA: item1, item2, item4
		//partB: item2, item3
		//partC: item5
		ArrayList<ArrayList<Item>> parts = new ArrayList<ArrayList<Item>>();

		//puts items into sub-arrays based on whether they have parts or not
		for(int i = 0; i < varsLength; i++) {
			parts.add(new ArrayList<Item>());
			for(Item a : items) {
				if(a.getTypeVariables()[i].equals("Y")) { // if the variable = 'Y'
					parts.get(i).add(a);
				}
			}
		}
		//check if it is possible to create a full item
		for(ArrayList<Item> i : parts) {
			if(i.size() < quantity) {
				//if not, throw ...
				throw new NoValidCombinationsException("No valid combinations");
			}
		}

		ArrayList<ArrayList<Item>> combinations = createCombinations(parts);

		combinations = removeDuplicates(combinations);
		combinations = findUnique(combinations);
		combinations = powerSet(quantity, combinations);
		
		combinations = removeInvalid(combinations);
		
		ArrayList<Integer> priceArray = getPriceForCombinations(combinations);

		int min = priceArray.get(0); //finding the minimum combination
		int index = 0;
		for(int i = 0; i < priceArray.size(); i++) {
			if(priceArray.get(i) < min) {
				min = priceArray.get(i);
				index = i;
			}
		}

		return combinations.get(index);
	}
	
	/**
	 * Returns an array of all the combinations prices with the same indices as 
	 * the combinations array
	 * @param arr Combinations array
	 * @return The prices of each combination as an array
	 */
	public ArrayList<ArrayList<Item>> combinationsByPrice(ArrayList<Item> arr){
		
		ArrayList<ArrayList<Item>> combinations = new ArrayList<ArrayList<Item>>();
		
		for(int i = 0; i < quantity; i++) {
			combinations.add(arr);
		}
		combinations = createCombinations(combinations);
		
		
		return createCombinations(combinations);
	}

	/**
	 * Adds the prices up for each ArrayList in the given ArrayList and adds it 
	 * to an ArrayList 
	 * @param arr ArrayList of ArrayLists of type Item
	 * @return An ArrayList of prices as integers
	 */
	public  ArrayList<Integer> getPriceForCombinations(ArrayList<ArrayList<Item>> arr){

		//arrayList that will be returned
		ArrayList<Integer> prices = new ArrayList<Integer>(); 
		
		//Iterate through arr to calculate price and put it into prices
		for(int i = 0; i < arr.size(); i++) {
			int sum = 0;
			for(int j = 0; j < arr.get(i).size(); j++) {
				sum += Integer.valueOf(arr.get(i).get(j).getPrice());
			}
			prices.add(sum);
		}

		return prices;
	}

	/**
	 * Deletes all duplicate items from the ArrayLists in ArrayList toChange
	 * @param toChange array of combinations
	 * @return returns the edited ArrayList
	 */
	public ArrayList<ArrayList<Item>> removeDuplicates(ArrayList<ArrayList<Item>> toChange){
		boolean removed = false;
		for(int i = 0; i < toChange.size(); i++) {
			for(int j = 0; j < toChange.get(i).size(); j++) {
				for(int k = j + 1; k < toChange.get(i).size(); k++) {
					if(compareItems(toChange.get(i).get(k), toChange.get(i).get(j))) {
						toChange.get(i).remove(k);
						i--;
						removed = true;
						break;
					}
				}
				if(removed) {
					removed = false;
					break;
				}
			}
		}

		return toChange;
	}
	
	/**
	 * Removes any combinations that already exist, irrespective of order 
	 * @param array Array to clean
	 * @return Cleaned array
	 */
	public ArrayList<ArrayList<Item>> findUnique(ArrayList<ArrayList<Item>> array){
		ArrayList<ArrayList<Item>> unique = new ArrayList<ArrayList<Item>>();
		boolean matched = false;
		unique.add(array.get(0));
		for(int i = 1; i < array.size(); i++) {
			for(int j = 0; j < unique.size(); j++) {
				if(matches(array.get(i), (unique.get(j)))) {
					matched = true;
				}
			}
			if(!matched) {
				unique.add(array.get(i));
			}
			else {
				matched = false;
			}
		}
		
		return unique;
	}
	
	//checks if two combinations match
	private boolean matches(ArrayList<Item> arrOne, ArrayList<Item> arrTwo) {
		if(sort(arrOne).equals(sort(arrTwo))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Removes any combinations that use an item twice
	 * @param toChange array that will have combinations removed
	 * @return The cleaned array
	 */
	public ArrayList<ArrayList<Item>> removeInvalid(ArrayList<ArrayList<Item>> toChange){
		boolean removed = false;
		for(int i = 0; i < toChange.size(); i++) {
			for(int j = 0; j < toChange.get(i).size(); j++) {
				for(int k = j + 1; k < toChange.get(i).size(); k++) {
					if(compareItems(toChange.get(i).get(k), toChange.get(i).get(j))) {
						toChange.remove(i);
						i--;
						removed = true;
						break;
					}
				}
				if(removed) {
					removed = false;
					break;
				}
			}
		}
		
		return toChange;
	}
	
	/**
	 * entrance to recursive Cartesian product creator 
	 * @param parts array of sets
	 * @return the Cartesian product of all of the sets
	 */
	public ArrayList<ArrayList<Item>> createCombinations(ArrayList<ArrayList<Item>> parts) {
		if (parts.size() < 2)
			throw new IllegalArgumentException(
					"Can't have a product of fewer than two sets (got " +
							parts.size() + ")");

		return createCombinations(0, parts);
	}

	/**
	 * Returns the ArrayList of ArrayLists that contain the Cartesian product of 
	 * the given ArrayLists in the ArrayList combinations.
	 * @param index index of the first set being multiplied
	 * @param combinations ArrayList of ArrayLists that contain sets
	 * @return the Cartesian product of the set at index with the result of 
	 * createCombinations with index + 1
	 */
	public ArrayList<ArrayList<Item>>createCombinations(int index, 
			ArrayList<ArrayList<Item>> combinations) {
		ArrayList<ArrayList<Item>> returnArray = 
				new ArrayList<ArrayList<Item>>();
		if (index == combinations.size()) {
			returnArray.add(new ArrayList<Item>());
		} else {
			for (Item element : combinations.get(index)) {
				for (ArrayList<Item> set : createCombinations(index + 1, 
						combinations)) {
					set.add(element);
					returnArray.add(set);
				}
			}
		}
		return returnArray;
	}
	
	/**
	 * Entrance into the recursion to make the power set
	 * @param Power the times it will combine with the original array 
	 * combinations
	 * @param Combinations the base set
	 * @return The power set to the given power 
	 */
	public ArrayList<ArrayList<Item>>powerSet(int power, 
			ArrayList<ArrayList<Item>> combinations){
		
		return powerSet(power, combinations, combinations);
	}
	
	/**
	 * Returns the power set of the given array, you can set it to any power > 0 
	 * @param Power the times it will combine with the original array 
	 * combinations
	 * @param Combinations the base set
	 * @param PowerSet the current iteration of the powerSet
	 * @return The power set with the power reduced by 1 until power <= 1 then 
	 * returns the power set 
	 */
	public ArrayList<ArrayList<Item>>powerSet(int power, 
			ArrayList<ArrayList<Item>> combinations,
			ArrayList<ArrayList<Item>> powerSet){
		
		ArrayList<ArrayList<Item>> sets = new ArrayList<ArrayList<Item>>();
	    if (combinations.isEmpty()) {
	        sets.add(new ArrayList<Item>());
	        return sets;
	    }
	    if(power <= 1) {
//	    	if(powerSet.isEmpty()) {
//	    		return combinations;
//	    	}
	    	return powerSet;
	    }
	    ArrayList<ArrayList<Item>> list = new ArrayList<ArrayList<Item>>(powerSet);
	    for (int i = 0; i < list.size(); i++) {
	    	for(int j = 0; j < combinations.size(); j++) {
	    		ArrayList<Item> set = new ArrayList<Item>(list.get(i));
	    		for(int k = 0; k < combinations.get(j).size(); k++) {
	    			set.add(combinations.get(j).get(k));
	    		}
	    		sets.add(set);
	    	}
	    } 
	    return powerSet(power - 1, combinations, sets);
	}
	
	/**
	 * compares two Items to see if they are equal
	 * @param one first item to compare
	 * @param two second item to compare
	 * @return True if they are equal false otherwise
	 */
	private boolean compareItems(Item one, Item two){
		if(!one.getId().equals(two.getId())){
			return false;
		}
		if(!one.getType().equals(two.getType())){
			return false;
		}
		if(!one.getManuID().equals(two.getManuID())){
			return false;
		}
		if(!one.getPrice().equals(two.getPrice())){
			return false;
		}
		if(!(one.getTypeVariables().length == two.getTypeVariables().length)){
			return false;
		}
		for(int i = 0; i<one.getTypeVariables().length; i++){
			if(!one.getTypeVariables()[i].equalsIgnoreCase(two.getTypeVariables()[i])){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Ascending Insertion Sort 
	 * @param toSort array that is being sorted
	 * @return the sorted array
	 */
	public ArrayList<Item> sort(ArrayList<Item> toSort){
		int j;
		for (int i = 1; i < toSort.size(); i++) {
			if (Integer.valueOf(toSort.get(i).getId().substring(1)) < Integer.valueOf(toSort.get(i-1).getId().substring(1))) {
				j = i;
				Item  toInsert = toSort.get(i) ;
				while (Integer.valueOf(toSort.get(j).getId().substring(1)) > Integer.valueOf(toInsert.getId().substring(1)) && j >= 1) {
					toSort.set(j+1,toSort.get(j)) ; //move list elements to the right to make room for toInsert
					j-- ;
				} //end while
				toSort.set(j,toInsert) ; //insert toInsert at correct place in sorted part of list
			} //end if
		} //end for
		return toSort;
	}
		 
	/**
	 * releases the database resources
	 */
	public void close() {
		 try {
	            dbConnect.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	}

	/**
	 * Takes three command line arguments ; ItemName, TableName, quantity ; 
	 * It will run the supply chain manager to give the cheapest combination of 
	 * items to build a full one or give recommendations for manufacturers that 
	 * sell the items you are looking for if the program can't build a full item
	 * from the current stock.
	 * @param args Main uses the first three arguments (ItemName, TableName, 
	 * quantity)
	 */
	public static void main(String[] args) {

		if(args.length < 3) {
			System.err.print("Please supply three arguments (ItemName, "
					+ "TableName, quantity)");
			return;
		}

		//change this to your MySQL account stuff
		SupplyChainManager myJDBC = new SupplyChainManager(
				"jdbc:mysql://localhost/inventory","ensf409","ensf409");

		myJDBC.run(args[0], args[1], args[2]);

	}
}
	


class NoValidCombinationsException extends Exception{

	private static final long serialVersionUID = 1L;

	public NoValidCombinationsException(String message) {
		super(message);
	}
}
