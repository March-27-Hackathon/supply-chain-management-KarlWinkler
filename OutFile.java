import java.io.*;
public class OutFile {
	private String Request;
	private String Quantity;
	private String [] IDs;
	private String TotalPrice;
	private static String filename ="OutputFile";
	
	public OutFile (String req, String qua, String [] ids, int prc){
		this.Request=req;
		this.Quantity=qua;
		this.IDs=ids;
		this.TotalPrice=Integer.toString(prc);
	}

	public void outFile () throws IOException{
		FileWriter filewrite = new FileWriter (filename,true);
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
	
	/*
	public static void main (String [] args) throws IOException{
		String [] idt ={"c1234","c5678"};
		OutFile test =new OutFile ("chair","2",idt,123);
		test.outFile();
	}
	*/
}