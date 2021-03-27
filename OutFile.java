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
		if(companies.size() > 0) {
			filewrite.write(companies.get(0));
		}
		
		for(int i = 1; i < companies.size(); i++){
			filewrite.write(", " + companies.get(i));
		}
		filewrite.write(".");
		filewrite.close();
	}
	/*
	public static void main (String [] args) throws IOException{
		String [] idt ={"c1234","c5678"};
		OutFile test =new OutFile ("chair","2",idt,123);
		test.outFile();
	}
	*/
}