/**
@author Karl Winkler, Zheng Chen, Maxwell Botham, and Rui Guan
@version 5.1
@since 1.0
*/
/*
In this program, we need to design an application to calculate the 
cheapest combination of available inventory items that can be used to fill a 
specific order. For example, if someone needs a new chair, the application 
should look through the database and find all the components, which can be 
combined and build a new chair. It connects with a database, which holds all the
information of the inventory such as ID, type, price, manufacturer, and 
condition of the items. The application finds the best combination of the new 
furniture we need. The best combination will have all of the required parts at
the lowest price. It accepts the users' input for 1) its type, 2) a furniture 
category, and 3) the number of items requested, and calculate and output the 
cheapest option for creating the requested pieces of furniture or specify if the 
request is not possible to fill.
*/

import java.io.IOException;
import java.sql.*;
import java.util.*;

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
	//constructor with three arguments, which are the URL of the database, the 
	//username
	//and the password of the local host
	public SupplyChainManager(String databaseURL, String username, 
			String password){
		this.DBURL =  databaseURL; 
		this.USERNAME = username;
		this.PASSWORD = password;
	}

	//method initializeConnection is used for connect to the database driver
	public void initializeConnection() {
		try{
			dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			//if failed, throw an SQLException
			System.err.print(e);
		}
	}

	/**
	 * Takes in the name of the desired item and the table it is located in,
	 * then it will use the OutFile class to write the best combination of
	 * items to an output file. returns a Boolean of whether a combination was 
	 * found.
	 * @param name name of the item
	 * @param tableName name of the type of item
	 * @param quantity how many items
	 * @return boolean of whether the item was found or not
	 */
	
	public boolean run(String name, String tableName, String quantity) {
		initializeConnection();
		try {
			this.quantity = Integer.valueOf(quantity);
			//selects the best combo and deletes the items out of the database
			//throws NoValidCombinationException if there is no way to make a 
			//full item
			ArrayList<ArrayList<Item>> allCombos = 
					new ArrayList<ArrayList<Item>>();
			
			ArrayList<Item> a = selectBestCombination(selectItems(name, 
					tableName));//throws NoValidCombinationException
			allCombos.add(a);
			for(Item e : a) {
				deleteID(e.getId(), tableName); //TODO uncomment
			}
			

				
			//prints out a form for all of the items required to make the desired 
			//amount of items
			ArrayList<String> ids = new ArrayList<String>();
			int sum = 0;
			for(ArrayList<Item> ac : allCombos) {
				for(Item e : ac) {
					sum += Integer.valueOf(e.getPrice());
					ids.add(e.getId());
				}
				OutFile f = new OutFile(name + " " + tableName, 
						quantity, ids, sum);

				f.writeOutFile();
			}
		} catch (Exception e) {
			try {
				//prints out a form saying that no Item could be created
				OutFile f = new OutFile(tableName, quantity, 
						selectManufacturers());
				f.writeNoneAvailable(tableName);
			} catch (IOException e1) {
				System.err.print(e);
			}
			return false;
		}
		close();
		return true;
	}
	/**
	 * Getter method for DBConnect
	 * @return dbConnect (Connection object)
	 */
	public Connection getDBConnect(){
		return this.dbConnect;
	}

	/**
	 * Method used by test class to reset SQL after deleting specific items
	 * @param list of items to be added back into SQL database
	 * No return.
	 */
	public void resetSQL(ArrayList<Item> toRestore){
		String typeVars = "";
		String object = "";
		String y = "\', \'";
		switch(toRestore.get(0).getId().charAt(0)) {
			//if the items are chairs
			case 'C':
				typeVars += "Legs, Arms, Seat, Cushion";
				object = "Chair";
				break;
			//if the items are desks
			case 'D':
				typeVars += "Legs, Top, Drawer";
				object = "Desk";
				break;
			//if the items are filings
			case 'F':
				typeVars += "Rails, Drawers, Cabinet";
				object = "Filing";
				break;
			//if the items are lamps
			case 'L':
				typeVars += "Base, Bulb";
				object = "Lamp";
				break;
			default: 
				break;
		}
		String insert = ("INSERT INTO "+object+" (ID, Type, "+typeVars+", Price, "
				+ "ManuID)\n");
		for(int i = 0; i<toRestore.size(); i++){
			typeVars = "";
			for(int j = 0; j<toRestore.get(i).getTypeVariables().length; j++){
				typeVars+=toRestore.get(i).getTypeVariables()[j]+y;
			}
			String values = "VALUES (\'"+toRestore.get(i).getId()+y+toRestore.
					get(i).getType()+y+typeVars+toRestore.get(i).getPrice()+ y + 
					toRestore.get(i).getManuID()+"\');";
			try {
				Statement newStmt = dbConnect.createStatement();
				String finalStatement = insert+values;
				int rows = newStmt.executeUpdate(finalStatement);
				//release the data
				newStmt.close();
			} catch (SQLException e) {
				System.err.print(e);
			}
		}
	}
	/**
	 * method deleteID is used for deleting an Item with a specified ID id out 
	 * of the database.
	 * @param id ID of the item to be deleted
	 * @param tableName Table to delete the item from
	 */
	public void deleteID(String id, String tableName) {
		String query = "DELETE FROM "+ tableName + " WHERE ID = \'" + id + "\'";
		try {
			Statement newStmt = dbConnect.createStatement();
			//execute the statement
			newStmt.executeUpdate(query);
			//release the data
			newStmt.close();
		} catch (SQLException e) {
			System.err.print(e);
		}
	}

	/**
	 * Takes a table name and an item name and returns an ArrayList of all the 
	 * of items from the table.
	 * @param name Name of the item to look for
	 * @param tableName Name of the table the item belongs to
	 * @return Returns an ArrayList with all of the results of the query
	 */
	public ArrayList<Item> selectItems(String name, String tableName) 
			throws Exception{
		String query = "SELECT * FROM "+ tableName + " WHERE Type = \'" + name 
				+ "\'" ;
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
			System.err.print(e);
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
	 * method selectManufacturers, which is used for selecting the items with 
	 * the specified manufactures from the table. There is no argument.
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
			System.err.print(e);
		}
		return outputArray;
	}
	

	/**
	 * method newItem, which is used for creating a new Item based on what the 
	 * tableName is.
	 * It uses a switch statement to select the name, then creating an array of 
	 * the items parts data (Y/N) then creating a new item based on the data
	 * @param result result set from a query
	 * @param tableName table that the query was performed on
	 * @return new Item object
	 */
	public Item newItem(String tableName) {
		try {
			switch(tableName) {
			//if the table is chair
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
			//if the table is desk
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
			//if the table is filing
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
			//if the table is desk
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
			//if the table is none of the above
			default: 
				return new Item();
			}
		} catch (SQLException e) {
			System.err.print(e);

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
	public ArrayList<Item> selectBestCombination(ArrayList<Item> items) 
			throws Exception{

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
				if(a.getTypeVariables()[i].equals("Y")) {//if the variable = 'Y'
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
		//removes duplicate items from each combination
		combinations = removeDuplicates(combinations);
		//finds all of the unique combinations
		combinations = findUnique(combinations);
		//creates all of the possible sets that can be used to fulfill the 
		//request
		combinations = powerSet(quantity, combinations);
		//removes any combinations that use an item more than once
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
		
		ArrayList<ArrayList<Item>> combinations = 
				new ArrayList<ArrayList<Item>>();
		
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
	public  ArrayList<Integer> getPriceForCombinations(
			ArrayList<ArrayList<Item>> arr){

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
	public ArrayList<ArrayList<Item>> removeDuplicates(
			ArrayList<ArrayList<Item>> toChange){
		boolean removed = false;
		for(int i = 0; i < toChange.size(); i++) {
			for(int j = 0; j < toChange.get(i).size(); j++) {
				for(int k = j + 1; k < toChange.get(i).size(); k++) {
					if(compareItems(toChange.get(i).get(k), 
							toChange.get(i).get(j))) {
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
	 * Removes any combinations that already exist, irrespective of order and 
	 * returns an arraylist of all of the unique combinations
	 * @param array Array to clean
	 * @return Cleaned array
	 */
	public ArrayList<ArrayList<Item>> findUnique(
			ArrayList<ArrayList<Item>> array){
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
		if(arrOne.size()!=arrTwo.size()){
			return false;
		}
		arrOne = sort(arrOne);
		arrTwo = sort(arrTwo);
		for(int i = 0; i<arrOne.size(); i++){
			if(!arrOne.get(i).getId().equals(arrTwo.get(i).getId())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Removes any combinations that use an item twice
	 * @param toChange array that will have combinations removed
	 * @return The cleaned array
	 */
	public ArrayList<ArrayList<Item>> removeInvalid(
			ArrayList<ArrayList<Item>> toChange){
		boolean removed = false;
		//itterates through the array to change
		for(int i = 0; i < toChange.size(); i++) {
			for(int j = 0; j < toChange.get(i).size(); j++) {
				//itterates through the recorded combinations
				for(int k = j + 1; k < toChange.get(i).size(); k++) {
					//checks to see if the sorted arrays match
					if(compareItems(toChange.get(i).get(k), 
							toChange.get(i).get(j))) {
						//removes the duplicate if it matches an existing 
						//combination
						toChange.remove(i);
						i--;
						removed = true;
						break;
					}
				}
			//moves onto the next combination if the the last one was removed
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
	public ArrayList<ArrayList<Item>> createCombinations(
			ArrayList<ArrayList<Item>> parts) {
		if (parts.size() < 2)
			throw new IllegalArgumentException(
					"Can't have a product of fewer than two sets (got " +
							parts.size() + ")");
		//calls the recursive method with starting values
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
			//builds the Cartesian product by looping through the array and
			//adding all the elements to the return array
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
	    	return powerSet;
	    }
	    //for every set in powerSet it will add a set to sets with the 
	    //values of a combination from combinations to powerSet[i] for each
	    //set in combinations
	    ArrayList<ArrayList<Item>> list = 
	    		new ArrayList<ArrayList<Item>>(powerSet);
	    for (int i = 0; i < list.size(); i++) {
	    	for(int j = 0; j < combinations.size(); j++) {
	    		ArrayList<Item> set = new ArrayList<Item>(list.get(i));
	    		for(int k = 0; k < combinations.get(j).size(); k++) {
	    			set.add(combinations.get(j).get(k));
	    		}
	    		sets.add(set);
	    	}
	    } 
	    //repeats the process until the order == 0.
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
			if(!one.getTypeVariables()[i]
					.equalsIgnoreCase(two.getTypeVariables()[i])){
				return false;
			}
		}
		return true;
	}
	/**
	 * Returns an array sorted in increasing order using an insertion sort
	 * @param toSort Array that is being sorted
	 * @return The sorted array
	 */
	public ArrayList<Item> sort(ArrayList<Item> toSort){
		int j;
		for (int i = 1; i < toSort.size(); i++) {
			if (Integer.valueOf(toSort.get(i).getId().substring(1))
					< Integer.valueOf(toSort.get(i-1).getId().substring(1))) {
				j = i - 1;
				Item  toInsert = toSort.get(i) ;
				while (j >= 0 && Integer.valueOf(toSort.get(j).getId()
						.substring(1)) > Integer.valueOf(toInsert.getId()
								.substring(1))) {
					toSort.set(j+1,toSort.get(j)); //move list elements to the 
					 						 //right to make room for toInsert
					j-- ;
				} //end while
				toSort.set(j+1,toInsert) ; //insert toInsert at correct place 
										  //in sorted part of list
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
	        	System.err.print(e);
	        }
	}

	/**
	 * Running the program will prompt the user to input a type, a furniture
	 * category and quantity of items requested in one line or type exit to 
	 * quit the program.
	 * If the user types a valid command the program will run and create a text
	 * file according to whether the order succeeded or failed
	 * It will run the supply chain manager to give the cheapest combination of 
	 * items to build a full one or give recommendations for manufacturers that 
	 * sell the items you are looking for if the program can't build a full item
	 * from the current stock.
	 * @param args not used
	 */
	public static void main(String[] args) {

				//
		//Change the following vairiables if necessary
		//
		String url = "jdbc:mysql://localhost/inventory";
		String usrname = "ensf409";
		String psswrd = "ensf409";

		SupplyChainManager myJDBC = new SupplyChainManager(url, usrname, psswrd);

		Scanner myObj = new Scanner(System.in);
		String input = "";
		do {
			//input message
			System.out.println("Please enter the type of object "
					+ "(starting with a capital letter), followed by the name of"
					+ " the table the objects belong to (no capital letter), "
					+ "then finally the quantity of items desired (a number) ");
			System.out.println("Please enter \"exit\" if you wish to terminate "
					+ "the program:");
			input = myObj.nextLine();

			// testing if the input is valid by counting spaces
			int spaces = 0;
			if(!input.equals("exit")) {
				for(int i = 0; i < input.length(); i++) {
					if(input.charAt(i) == ' ') {
						spaces++;
					}
				}

				if(spaces != 2) {
					//print error message
					System.err.println("Illegal Argument");
				}
				else {
					//parsing the input
					int i = 0;
					while(input.charAt(i) != ' ') {
						i++;
					}

					int i1 = i++;

					while(input.charAt(i) != ' ') {
						i++;
					}
					//running the program and giving the correct output message
					boolean runBool = myJDBC.run(input.substring(0, i1), 
							input.substring(i1 + 1, i), input.substring(i+1));

					System.out.println();
					if(runBool) {
						System.out.println("Output file generated: Order Fulfilled");
					}
					else {
						System.out.println("Output file generated: Order Failed");
					}
					System.out.println();
				}
			}
		} while(!input.equalsIgnoreCase("exit"));
		myObj.close();
	}
}
	


class NoValidCombinationsException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	//uses Exception class constructor to create an exception object
	public NoValidCombinationsException(String message) {
		super(message);
	}
}
