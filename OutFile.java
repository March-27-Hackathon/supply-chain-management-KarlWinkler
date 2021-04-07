//This class is used for writing the result from SupplyChainManagement class on 
//a new text file in the specified format
import java.io.*;
import java.util.ArrayList;
public class OutFile {
	//all the information need to be written in the file
	private String Request;
	private String Quantity;
	private ArrayList<String> IDs;
	private ArrayList<String> companies;
	private String TotalPrice;
	private static String filename ="OutputFile.txt";
	
	//constructor with four parameters, creates a new object with the specified request, quantity,
	//ID, and price
	/**
	 * Constructor for the OutFile object with four parameters
	 * @param requested furniture
	 * @param furniture quantity
	 * @param list of Item IDs
	 * @param furniture Price
	 */
	public OutFile (String req, String qua, ArrayList<String> ids, int prc){
		this.Request=req;
		this.Quantity=qua;
		this.IDs=ids;
		this.TotalPrice=Integer.toString(prc);
	}
	
	//constructor with three parameters, creates a new object with the specified request, quantity, and companies
	/**
	 * Constructor for the OutFile object with three parameters
	 * @param requested furniture
	 * @param furniture quantity
	 * @param list of Companies
	 */
	public OutFile (String req, String qua, ArrayList<String> companies){
		this.Request=req;
		this.Quantity=qua;
		this.companies=companies;
	}

	//method writeOutFile, which is used for write all information in the file
	//if there are the results fulfilled.
	/**
	 * Method to write the file when requested furnitures are
	 * available. The file generated has the items ordered with their ids and 
	 * the total price
	 */
	public void writeOutFile () throws IOException{
		FileWriter filewrite = new FileWriter (filename, false);
		filewrite.write("Furniture Order Form"+"\n");
		filewrite.write("\n"+"Faculty Name:"+"\n");
		filewrite.write("Contact:"+"\n");
		filewrite.write("Date:"+"\n");
		filewrite.write("\n"+"Original Request: "+ Request +", " +Quantity +"\n");
		filewrite.write("\n"+"Items Ordered"+"\n");
		for(String id : IDs){
			filewrite.write("ID: "+ id + "\n");
		}
		filewrite.write("\n"+"Total Price: $"+TotalPrice);
		filewrite.close();
	}
	
	//method writeNoneAvailable with one argument (requried furniture's name), which 
	//is used for writing in the file if there is no fulfilled result in the database
	/**
	 * Method to write the file when requested furnitures are not
	 * available. The file generated has the companies' name which produce
	 * the requested furniture
	 * @param furniture name
	 */
	public void writeNoneAvailable(String name) throws IOException {
		FileWriter filewrite = new FileWriter (filename, false);
		filewrite.write("Furniture Order Form"+"\n");
		filewrite.write("\n"+"Faculty Name:"+"\n");
		filewrite.write("Contact:"+"\n");
		filewrite.write("Date:"+"\n");
		filewrite.write("\n"+"Original Request: "+ name + " " + Request +", " + Quantity +"\n");
		filewrite.write("\n"+"Order cannot be fulfilled based on current inventory. Suggested manufacturers are ");
		
		//If there is no fulfilled result, the system will suggest some other available manufacturers
		String ManNotIncluded1="";
		String ManNotIncluded2="";
		if(Request.equals("chair")||Request.equals("Chair")){
			ManNotIncluded1="Academic Desks";
		}
		else if(Request.equals("desk")||Request.equals("Desk")){
			ManNotIncluded1="Chairs R Us";
		}
		else if(Request.equals("lamp")||Request.equals("Lamp")){
			ManNotIncluded1="Academic Desks";
			ManNotIncluded2="Chairs R Us";
		}
		else if(Request.equals("filing")||Request.equals("Filing")){
			ManNotIncluded1="Academic Desks";
			ManNotIncluded2="Chairs R Us";
		}
		ArrayList <String> contactMan = new ArrayList <String> ();
		for(int i = 0; i < companies.size(); i++){
			if(companies.size() > 0 && !(companies.get(i).equals(ManNotIncluded1)) && !(companies.get(i).equals(ManNotIncluded2) )){
				contactMan.add(companies.get(i));
				contactMan.add(", ");
			}
		}
		contactMan.set(contactMan.size()-1,".");
		for(int j=0; j< contactMan.size();j++){
			filewrite.write(contactMan.get(j));
		}
		filewrite.close();
	}
	
	/* test
	public static void main (String [] args) throws IOException{
		ArrayList <String> ts=new ArrayList <String> ();
		ts.add("Academic Desks");
		ts.add("Office Furnishing");
		ts.add("Chairs R Us");
		ts.add("Furniture Goods");
		ts.add("Fine Office Supplies");

		OutFile test =new OutFile ("desk","2",ts);
		test.writeNoneAvailable();
	}*/
	
}
