import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class SupplyChainManager {

	public final String DBURL; //store the database url information
	public final String USERNAME; //store the user's account username
	public final String PASSWORD; //store the user's account password

	private Connection dbConnect;
	private ResultSet results;

	//constructor I guess
	public SupplyChainManager(String databaseURL, String username, String password){
		this.DBURL =  databaseURL; 
		this.USERNAME = username;
		this.PASSWORD = password;
	}

	private void initializeConnection() {
		try{
			dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this is chaos
	 * @param name name of the item
	 * @param tableName name of the type of item
	 * @param quantity how many items
	 * @return boolean of whether the item was found or not (not used)
	 */
	public boolean run(String name, String tableName, String quantity) {
		int quant = Integer.valueOf(quantity);
		try {
			//selects the best combos and deletes the items out of the database
			//throws NoValidCombinationException if there is no way to make a full item
			ArrayList<ArrayList<Item>> allCombos = new ArrayList<ArrayList<Item>>();
			for(int i = 0; i < quant; i++) {
				ArrayList<Item> a = selectBestCombination(selectItem(name, tableName));//throws NoValidCombinationException
				allCombos.add(a);
				for(Item e : a) {
					deleteID(e.getId(), tableName);
				}
			}

				
			//prints out a form for all of the items required to make the desired amount of items
			ArrayList<String> ids = new ArrayList<String>();
			int sum = 0;
			for(ArrayList<Item> a : allCombos) {
				for(Item e : a) {
					sum += Integer.valueOf(e.getPrice());
					ids.add(e.getId());
					System.out.println(e.getId());
				}
				OutFile f = new OutFile(name + " " + tableName, quantity, ids, sum);

				f.writeOutFile();
			}
			

		} catch (NoValidCombinationsException e2) {
			try {
				//prints out a form saying that no Item could be created
				OutFile f = new OutFile(tableName, quantity, selectManufacturers());
				f.writeNoneAvailable(name);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {

		} catch (Exception e3) {

		}
		return true;



	}

	/**
	 * deletes an Item with ID id out of the database
	 * @param id ID of the item to be deleted
	 * @param tableName table to delete the item from
	 */
	private void deleteID(String id, String tableName) {
		String query = "DELETE FROM "+ tableName + " WHERE ID = \'" + id + "\'" ;
		try {
			Statement newStmt = dbConnect.createStatement();
			newStmt.executeUpdate(query);
			newStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * selects all the desired items from the table
	 * 
	 * @param name name of the item
	 * @param tableName name of the table the item belongs to
	 * @return returns an array with all of the results of the query
	 */
	public ArrayList<Item> selectItem(String name, String tableName){
		String query = "SELECT * FROM "+ tableName + " WHERE Type = \'" + name + "\'" ;
		ArrayList<Item> outputArray = new ArrayList<Item>();
		try {
			Statement newStmt = dbConnect.createStatement();
			//put results into an array to output
			results = newStmt.executeQuery(query);
			while(results.next()) {
				outputArray.add(newItem(results, tableName));
			}

			newStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return outputArray;
	}
	
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

			newStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return outputArray;
	}
	

	/**
	 * creates a new Item based on what the tableName is.
	 * Using a switch statement to select the name, then creating an array of 
	 * the items parts data (Y/N) then creating a new item based on the data
	 * @param result result set from a query
	 * @param tableName table that the query was performed on
	 * @return new Item object
	 */
	private Item newItem(ResultSet result, String tableName) {
		try {
			switch(tableName) {
			case "chair":
				String[] tableVars = {results.getString("Legs"), results.getString("Arms"), results.getString("Seat"), results.getString("Cushion")};
				return new Item(results.getString("ID"), results.getString("Type"),tableVars ,results.getString("Price"), results.getString("ManuID"));

			case "desk":
				String[] deskVars = {results.getString("Legs"), results.getString("Top"), results.getString("Drawer")};
				return new Item(results.getString("ID"), results.getString("Type"),deskVars ,results.getString("Price"), results.getString("ManuID"));

			case "filing":
				String[] filingVars = {results.getString("Rails"), results.getString("Drawers"), results.getString("Cabinet")};
				return new Item(results.getString("ID"), results.getString("Type"),filingVars ,results.getString("Price"), results.getString("ManuID"));

			case "lamp":
				String[] lampVars = {results.getString("Base"), results.getString("Bulb")};
				return new Item(results.getString("ID"), results.getString("Type"),lampVars ,results.getString("Price"), results.getString("ManuID"));

			default: 
				return new Item();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;

	}

	/**
	 * finds the best combination of items out of the desired list
	 * @param items a set of items
	 * @return the combination of items that fulfills the requirements the cheapest
	 * @throws Exception if there are no valid combos
	 */
	private ArrayList<Item> selectBestCombination(ArrayList<Item> items) throws Exception{

		int varsLength = items.get(0).getTypeVariables().length;
		// parts holds an array of arrays of items that have each part
		//		example
		//partA: item1, item2, item4
		//partB: item2, item3
		//partC: item5
		ArrayList<ArrayList<Item>> parts = new ArrayList<ArrayList<Item>>();

		//puts items into sub-arrays based on whether they have parts or not
		for(int i = 0; i < varsLength; i++) {
			parts.add(new ArrayList<Item>());
			for(Item a : items) {
				if(a.getTypeVariables()[i].equals("Y")) {// if the variable = 'Y'
					parts.get(i).add(a);
				}
			}
		}
		//check if it is possible to create a full item
		for(ArrayList<Item> i : parts) {
			if(i.size() < 1) {
				//if not, throw ...
				throw new NoValidCombinationsException("No valid combinations");
			}
		}

		ArrayList<ArrayList<Item>> combinations = createCombinations(parts);

		combinations = removeDuplicates(combinations);

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
	 * adds the prices up for each combination and adds it to an array 
	 * @param arr array of combinations
	 * @return an array of prices
	 */
	private  ArrayList<Integer> getPriceForCombinations(ArrayList<ArrayList<Item>> arr){

		ArrayList<Integer> prices = new ArrayList<Integer>();
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
	 * deletes all duplicate items from combinations
	 * @param toChange array of combinations
	 * @return returns teh edited combinaitions
	 */
	private ArrayList<ArrayList<Item>> removeDuplicates(ArrayList<ArrayList<Item>> toChange){

		for(int i = 0; i < toChange.size(); i++) {
			for(int j = 0; j < toChange.get(i).size(); j++) {
				for(int k = j + 1; k < toChange.get(i).size(); k++) {
					if(toChange.get(i).get(k) == toChange.get(i).get(j)) {
						toChange.get(i).remove(k);
					}
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
	private ArrayList<ArrayList<Item>> createCombinations(ArrayList<ArrayList<Item>> parts) {
		if (parts.size() < 2)
			throw new IllegalArgumentException(
					"Can't have a product of fewer than two sets (got " +
							parts.size() + ")");

		return createCombinations(0, parts);
	}

	/**
	 * recursively solves for the Cartesian product of the sets
	 * @param index index of the first set being multiplied
	 * @param combos array of sets
	 * @return the Cartesian product of all of two sets
	 */
	private ArrayList<ArrayList<Item>>createCombinations(int index, ArrayList<ArrayList<Item>> combos) {
		ArrayList<ArrayList<Item>> returnArray = new ArrayList<ArrayList<Item>>();
		if (index == combos.size()) {
			returnArray.add(new ArrayList<Item>());
		} else {
			for (Item element : combos.get(index)) {
				for (ArrayList<Item> set : createCombinations(index + 1, combos)) {
					set.add(element);
					returnArray.add(set);
				}
			}
		}
		return returnArray;
	}

	/**
	 * given three command line arguments : ItemName, TableName, quantity : 
	 * it will run the supply chain manager to give the cheapest combination of 
	 * items to build a full one or give recommendations for manufacturers that 
	 * sell the items you are looking for if the program can't build a full item
	 * from the current stock.
	 * @param args uses three arguments (ItemName, TableName, quantity)
	 */
	public static void main(String[] args) {

		if(args.length > 3) {
			System.err.print("Please supply three arguments (ItemName, TableName, quantity)");
			return;
		}
		//change this to your MySQL account stuff
		SupplyChainManager myJDBC = new SupplyChainManager("jdbc:mysql://localhost/inventory","scm","ensf409");
		myJDBC.initializeConnection();

		myJDBC.run(args[0], args[1], args[2]);

	}	
}

class NoValidCombinationsException extends Exception{

	private static final long serialVersionUID = 1L;

	public NoValidCombinationsException(String message) {
		super(message);
	}
}
