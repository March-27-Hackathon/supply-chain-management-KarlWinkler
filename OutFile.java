import java.io.*;
import java.util.ArrayList;
public class OutFile {
	private String Request;
	private String Quantity;
	private ArrayList<String> IDs;
	private ArrayList<String> companies;
	private String TotalPrice;
	private static String filename ="OutputFile";
	
	public OutFile (String req, String qua, ArrayList<String> ids, int prc){
		this.Request=req;
		this.Quantity=qua;
		this.IDs=ids;
		this.TotalPrice=Integer.toString(prc);
	}
	
	public OutFile (String req, String qua, ArrayList<String> companies){
		this.Request=req;
		this.Quantity=qua;
		this.companies=companies;
	}

	public void writeOutFile () throws IOException{
		FileWriter filewrite = new FileWriter (filename, false);
		filewrite.write("Furniture Order Form"+"\n");
		filewrite.write("\n"+"Faculty Name:"+"\n");
		filewrite.write("Contact:"+"\n");
		filewrite.write("Date:"+"\n");
		filewrite.write("\n"+"Original Request:"+ Request +", " +Quantity +"\n");
		filewrite.write("\n"+"Items Ordered"+"\n");
		for(String id : IDs){
			filewrite.write("ID: "+ id + "\n");
		}
		filewrite.write("\n"+"Total Price: $"+TotalPrice);
		filewrite.close();
	}
	
	public void writeNoneAvailable() throws IOException {
		FileWriter filewrite = new FileWriter (filename, false);
		filewrite.write("Furniture Order Form"+"\n");
		filewrite.write("\n"+"Faculty Name:"+"\n");
		filewrite.write("Contact:"+"\n");
		filewrite.write("Date:"+"\n");
		filewrite.write("\n"+"Original Request:"+ Request +", " + Quantity +"\n");
		filewrite.write("\n"+"Order cannot be fulfilled based on current inventory. Suggested manufacturers are ");
		
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