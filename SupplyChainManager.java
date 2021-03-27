import java.sql.*;
import java.util.ArrayList;

public class SupplyChainManager {
	
	public final String DBURL; //store the database url information
	public final String USERNAME; //store the user's account username
	public final String PASSWORD; //store the user's account password

	private Connection dbConnect;
	private ResultSet results;
	
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

	
	public ArrayList<Item> SelectItem(String name, String tableName){
		String query = "SELECT "+ name +" FROM "+ tableName;
		ArrayList<Item> outputArray = new ArrayList<Item>();
		try {
			Statement newStmt = dbConnect.createStatement();

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
	
	private ArrayList<Item> selectBestCombination(ArrayList<Item> items){
		
		
		
		return null;
	}

	public static void main(String[] args) {

		SupplyChainManager myJDBC = new SupplyChainManager("jdbc:mysql://localhost/competition","ensf409","ensf409");
		myJDBC.initializeConnection();
	}	
}
