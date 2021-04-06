import static org.junit.Assert.*;
import org.junit.*;
import java.util.Arrays;
import jdk.jfr.Timestamp;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

//import java.sql.*;
import java.util.ArrayList;

/*
FUNCTIONS TO TEST:

COMPLETED:
selectBestCombinatiosTest
initializeConnectionTest
closeConnectionTest
selectItems
selectManufacturers
SupplyChainManager
getPriceForCombination
removeDuplicates
createCombinations
run


deleteID - ? tested thoroughly in run...

make sure initialize and close is working as expected
*/

public class SupplyChainManagerTest {
  public static String USERNAME = "max";
  public static String PASSWORD = "ensf409";
  public static String DBURL = "jdbc:mysql://localhost/INVENTORY";
  public static String CONSTRUCTOR_MESSAGE = "The constructor of SupplyChainManager failed to initialize constants.";
  public static String selectItems_MESSAGE = "The function selectItems of SupplyChainManager failed to return the correct ArrayList<Item>.";
  public static String SELECTMANU_MESSAGE = "The function selectManufacturers of SupplyChainManager failed to return the correct ArrayList<String>.";
  public static String CREATECOMBO_MESSAGE = "The function createCombinations of SupplyChainManager failed to return the correct Arraylist<Arraylist<Item>>";
  public static String REMOVEDUP_MESSAGE = "The function removeDuplicates of SupplyChainManager failed to remove duplicates.";
  public static String GETPRICE_MESSAGE = "The function getPriceForCombinations of SupplyChainManager failed to get the correct price for the combinations calculated.";
  public static String BESTCOMBO_MESSAGE = "The function selectBestCombination of SupplyChainManager failed to retrieve the correct best combination.";
  public static String POWERSET_MESSAGE = "The function powSet of SupplyChainManager failed to be combined in power times.";
  public static String NICESORT_MESSAGE = "The function sort of SupplyChainManager failed to be sorted.";
  public static String INITIALIZE_MESSAGE = "The function initializeConnection of SupplyChainManager failed to initialize the connection.";
  public static String CLOSE_MESSAGE = "The function close of SupplyChainManager failed to initialize the connection.";
  public static String RUN_MESSAGE = "The function run of SupplyChainManager failed to produce the expected output file.";
  private SupplyChainManager manager;
  private OutFile outfile;
  @Test
  public void constructorTest() {
    SupplyChainManager constructed = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    boolean didSetConstants = false;
    if(constructed.DBURL.equals(DBURL)&&constructed.USERNAME.equals(USERNAME)
        &&constructed.PASSWORD.equals(PASSWORD)){
        didSetConstants = true;
    }
    assertTrue(CONSTRUCTOR_MESSAGE, didSetConstants);
  }
  
  @Test
  public void selectItemsDeskTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItems("Traditional", "desk");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(4);
    String [] firstItem = {"N", "N", "Y"};
    String [] secondItem = {"N", "Y", "Y"};
    String [] thirdItem = {"Y", "Y", "N"};
    String [] fourthItem = {"Y", "N", "Y"};
    shouldEqual.add(new Item("D0890", "Traditional", firstItem, "25", "002"));
    shouldEqual.add(new Item("D4231", "Traditional", secondItem, "50", "005"));
    shouldEqual.add(new Item("D8675", "Traditional", thirdItem, "75", "001"));
    shouldEqual.add(new Item("D9352", "Traditional", fourthItem, "75", "002"));
    manager.close();
    assertTrue(selectItems_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemsChairTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItems("Mesh", "chair");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(4);
    String [] firstItem = {"Y", "N", "Y", "Y"};
    String [] secondItem = {"Y", "N", "N", "N"};
    String [] thirdItem = {"N", "N", "Y", "N"};
    String [] fourthItem = {"N", "Y", "N", "Y"};
    shouldEqual.add(new Item("C0942", "Mesh", firstItem, "100", "005"));
    shouldEqual.add(new Item("C6748", "Mesh", secondItem, "75", "003"));
    shouldEqual.add(new Item("C8138", "Mesh", thirdItem, "75", "005"));
    shouldEqual.add(new Item("C9890", "Mesh", fourthItem, "50", "003"));
    manager.close();
    assertTrue(selectItems_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemsFilingTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItems("Large", "filing");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(5);
    String [] firstItem = {"N", "N", "Y"};
    String [] secondItem = {"Y", "N", "Y"};
    String [] thirdItem = {"N", "Y", "Y"};
    String [] fourthItem = {"N", "Y", "N"};
    String [] fifthItem = {"Y", "N", "N"};
    shouldEqual.add(new Item("F003", "Large", firstItem, "150", "002"));
    shouldEqual.add(new Item("F010", "Large", secondItem, "225", "002"));
    shouldEqual.add(new Item("F011", "Large", thirdItem, "225", "005"));
    shouldEqual.add(new Item("F012", "Large", fourthItem, "75", "005"));
    shouldEqual.add(new Item("F015", "Large", fifthItem, "75", "004"));
    manager.close();
    assertTrue(selectItems_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemsLampTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItems("Swing Arm", "lamp");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(4);
    String [] firstItem = {"Y", "N"};
    String [] secondItem = {"N", "Y"};
    String [] thirdItem = {"Y", "N"};
    String [] fourthItem = {"N", "Y"};
    shouldEqual.add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    shouldEqual.add(new Item("L096", "Swing Arm", secondItem, "3", "002"));
    shouldEqual.add(new Item("L487", "Swing Arm", thirdItem, "27", "002"));
    shouldEqual.add(new Item("L879", "Swing Arm", fourthItem, "3", "005"));
    manager.close();
    assertTrue(selectItems_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test(expected = Exception.class)
  public void selectItemsFakeTypeTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItems("Does Not Exist", "lamp");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(0);
    manager.close();
  }

  @Test
  public void selectManufacturersTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<String> returned = manager.selectManufacturers();
    ArrayList<String> shouldEqual = new ArrayList<String>(5);
    shouldEqual.add("Academic Desks");
    shouldEqual.add("Office Furnishings");
    shouldEqual.add("Chairs R Us");
    shouldEqual.add("Furniture Goods");
    shouldEqual.add("Fine Office Supplies");
    manager.close();
    assertTrue(SELECTMANU_MESSAGE, shouldEqual.equals(returned));
  }
  
  @Test
  public void createCombinationsTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    ArrayList<ArrayList<Item>> shouldEqual = new ArrayList<ArrayList<Item>>();
    for(int i = 0; i<4; i++){
        shouldEqual.add(new ArrayList<Item>(2));
        shouldEqual.get(i).add(new Item());
        shouldEqual.get(i).add(new Item());
    }
    shouldEqual.get(0).get(0).setId("L096");
    shouldEqual.get(0).get(1).setId("L053");
    shouldEqual.get(1).get(0).setId("L879");
    shouldEqual.get(1).get(1).setId("L053");
    shouldEqual.get(2).get(0).setId("L096");
    shouldEqual.get(2).get(1).setId("L487");
    shouldEqual.get(3).get(0).setId("L879");
    shouldEqual.get(3).get(1).setId("L487");

    ArrayList<Item> items = new ArrayList<Item>(4);
    String [] firstItem = {"Y", "N"};
    String [] secondItem = {"N", "Y"};
    String [] thirdItem = {"Y", "N"};
    String [] fourthItem = {"N", "Y"};
    items.add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    items.add(new Item("L096", "Swing Arm", secondItem, "3", "002"));
    items.add(new Item("L487", "Swing Arm", thirdItem, "27", "002"));
    items.add(new Item("L879", "Swing Arm", fourthItem, "3", "005"));
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
    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(parts);

    //Check if the combinations array is equal to the hardcoded array.
    boolean isSame = true;
    if(combinations.size()==shouldEqual.size()){
        for(int i = 0; i<combinations.size(); i++){
            if(combinations.get(i).size()==shouldEqual.get(i).size()){
                for(int j = 0; j<combinations.get(i).size(); j++){
                    if(!shouldEqual.get(i).get(j).getId().equals(combinations.get(i).get(j).getId())){
                        isSame = false;
                    }
                }
            }
            else{
                isSame = false;
            }
        }
    }
    else{
        isSame = false;
    }
    //Assert that they are the same array.
    assertTrue(CREATECOMBO_MESSAGE, isSame);

  }
  
  @Test
  public void createCombinationsShouldBeEmptyTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    ArrayList<Item> items = new ArrayList<Item>();
    String [] firstItem = {"Y", "N"};
    String [] secondItem = {"Y", "N"};
    items.add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    items.add(new Item("L487", "Swing Arm", secondItem, "27", "002"));
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
    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(parts);
    boolean isEmpty = combinations.size()==0;
    assertTrue(CREATECOMBO_MESSAGE, isEmpty);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCombinationsExceptionTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    ArrayList<ArrayList<Item>> sizeOne = new ArrayList<ArrayList<Item>>();
    sizeOne.add(new ArrayList<Item>());
    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(sizeOne);
  }
 
  @Test
  public void removeDuplicatesTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    //Initialize some data to put into create combos function 
    ArrayList<Item> items = new ArrayList<Item>();
    String [] firstItem = {"Y", "N"};
    String [] secondItem = {"N", "Y"};
    String [] thirdItem = {"Y", "N"};
    String [] fourthItem = {"N", "Y"};
    items.add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    items.add(new Item("L096", "Swing Arm", secondItem, "3", "002"));
    items.add(new Item("L487", "Swing Arm", thirdItem, "27", "002"));
    items.add(new Item("L879", "Swing Arm", fourthItem, "3", "005"));
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

    //Create combinations from parts
    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(parts);
    
    //Add duplicate items to combinations
    combinations.get(0).add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    combinations.get(3).add(new Item("L879", "Swing Arm", fourthItem, "3", "005"));

    //Remove duplicates in each combination
    ArrayList<ArrayList<Item>> removedDuplicates = manager.removeDuplicates(combinations); 

    //Build the expected return arraylist<arraylist<item>>.
    ArrayList<ArrayList<String>> shouldEqual = new ArrayList<ArrayList<String>>();
    for(int i = 0; i<4; i++){
        shouldEqual.add(new ArrayList<String>());
    }
    shouldEqual.get(0).add("L096");
    shouldEqual.get(0).add("L053");
    shouldEqual.get(1).add("L879");
    shouldEqual.get(1).add("L053");
    shouldEqual.get(2).add("L096");
    shouldEqual.get(2).add("L487");
    shouldEqual.get(3).add("L879");
    shouldEqual.get(3).add("L487");  
    //Check that removedDuplicates is the same as expected values.
    boolean isSame = true;
    if(removedDuplicates.size()==shouldEqual.size()){
        for(int i = 0; i<removedDuplicates.size(); i++){
            if(removedDuplicates.get(i).size()==shouldEqual.get(i).size()){
                for(int j = 0; j<removedDuplicates.get(i).size(); j++){
                    if(!shouldEqual.get(i).get(j).equals(removedDuplicates.get(i).get(j).getId())){
                        isSame = false;
                    }
                }
            }
            else{
                isSame = false;
            }
        }
    }
    else{
        isSame = false;
    }
    assertTrue(REMOVEDUP_MESSAGE, isSame);
  }

  @Test
  public void removeDuplicatesNoDuplicatesTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    //Initialize some data to put into create combos function 
    ArrayList<Item> items = new ArrayList<Item>();
    String [] firstItem = {"Y", "N"};
    String [] secondItem = {"N", "Y"};
    String [] thirdItem = {"Y", "N"};
    String [] fourthItem = {"N", "Y"};
    items.add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    items.add(new Item("L096", "Swing Arm", secondItem, "3", "002"));
    items.add(new Item("L487", "Swing Arm", thirdItem, "27", "002"));
    items.add(new Item("L879", "Swing Arm", fourthItem, "3", "005"));
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
    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(parts);
    ArrayList<ArrayList<Item>> removedDuplicates = manager.removeDuplicates(combinations);

    //Build the expected return arraylist.
    ArrayList<ArrayList<String>> shouldEqual = new ArrayList<ArrayList<String>>();
    for(int i = 0; i<4; i++){
        shouldEqual.add(new ArrayList<String>());
    }
    shouldEqual.get(0).add("L096");
    shouldEqual.get(0).add("L053");
    shouldEqual.get(1).add("L879");
    shouldEqual.get(1).add("L053");
    shouldEqual.get(2).add("L096");
    shouldEqual.get(2).add("L487");
    shouldEqual.get(3).add("L879");
    shouldEqual.get(3).add("L487");

    //Check that removedDuplicates is the same as expected values.
    boolean isSame = true;
    if(removedDuplicates.size()==shouldEqual.size()){
        for(int i = 0; i<removedDuplicates.size(); i++){
            if(removedDuplicates.get(i).size()==shouldEqual.get(i).size()){
                for(int j = 0; j<removedDuplicates.get(i).size(); j++){
                    if(!shouldEqual.get(i).get(j).equals(removedDuplicates.get(i).get(j).getId())){
                        isSame = false;
                    }
                }
            }
            else{
                isSame = false;
            }
        }
    }
    else{
        isSame = false;
    }
    assertTrue(REMOVEDUP_MESSAGE, isSame);
  }

  
 
  
  
  @Test
  public void getPriceForCombinationsTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    //Initialize some data to put into create combos function 
    ArrayList<Item> items = new ArrayList<Item>(5);
    String [] firstItem = {"N", "N", "Y"};
    String [] secondItem = {"Y", "N", "Y"};
    String [] thirdItem = {"N", "Y", "Y"};
    String [] fourthItem = {"N", "Y", "N"};
    String [] fifthItem = {"Y", "N", "N"};
    items.add(new Item("F003", "Large", firstItem, "150", "002"));
    items.add(new Item("F010", "Large", secondItem, "225", "002"));
    items.add(new Item("F011", "Large", thirdItem, "225", "005"));
    items.add(new Item("F012", "Large", fourthItem, "75", "005"));
    items.add(new Item("F015", "Large", fifthItem, "75", "004"));
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

    //Create combinations from parts
    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(parts);
    combinations = manager.removeDuplicates(combinations);

    //Find price array from data
    ArrayList<Integer> returnedPriceArray = manager.getPriceForCombinations(combinations);

    //Build expected output array shouldEqual
    ArrayList<Integer> shouldEqual = new ArrayList<Integer>();
    shouldEqual.add(600);
    shouldEqual.add(450);
    shouldEqual.add(450);
    shouldEqual.add(450);
    shouldEqual.add(300);
    shouldEqual.add(525);
    shouldEqual.add(450);
    shouldEqual.add(525);
    shouldEqual.add(300);
    shouldEqual.add(300);
    shouldEqual.add(375);
    shouldEqual.add(375);
    boolean isSame = true;
    if(returnedPriceArray.size()!=shouldEqual.size()){
        isSame = false;
    }
    else{
        for(int i = 0; i<returnedPriceArray.size(); i++){
            if(!returnedPriceArray.get(i).equals(shouldEqual.get(i))){
                isSame = false;
                break;
            }
        }
    }
    assertTrue(GETPRICE_MESSAGE, isSame);
  }
  
  @Test
  public void getPriceForCombinationsEmptyTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);

    //Create empty combinations arraylist
    ArrayList<ArrayList<Item>> combinations = new ArrayList<ArrayList<Item>>();

    //Find price array from empty data
    ArrayList<Integer> returnedPriceArray = manager.getPriceForCombinations(combinations);

    //Build expected output array shouldEqual
    ArrayList<Integer> shouldEqual = new ArrayList<Integer>();
    boolean isEmpty = false;
    if(returnedPriceArray.size()==shouldEqual.size()&&shouldEqual.size()==0){
        isEmpty = true;
    }
    assertTrue(GETPRICE_MESSAGE, isEmpty);
  }
 
  @Test
  public void selectBestCombinationsTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    //Initialize some data to put into create combos function 
    ArrayList<Item> items = new ArrayList<Item>();
    String [] firstItem = {"N", "N", "Y"};
    String [] secondItem = {"Y", "N", "Y"};
    String [] thirdItem = {"N", "Y", "Y"};
    String [] fourthItem = {"N", "Y", "N"};
    String [] fifthItem = {"Y", "N", "N"};
    items.add(new Item("F003", "Large", firstItem, "150", "002"));
    items.add(new Item("F010", "Large", secondItem, "225", "002"));
    items.add(new Item("F011", "Large", thirdItem, "225", "005"));
    items.add(new Item("F012", "Large", fourthItem, "75", "005"));
    items.add(new Item("F015", "Large", fifthItem, "75", "004"));

    ArrayList<Item> bestCombo = manager.selectBestCombination(items);

    ArrayList<String> shouldEqual = new ArrayList<String>();
    shouldEqual.add("F010");
    shouldEqual.add("F012");

    boolean isSame = true;
    if(bestCombo.size()!=shouldEqual.size()){
        isSame = false;
    }
    else{
        for(int i = 0; i<bestCombo.size(); i++){
            if(!bestCombo.get(i).getId().equals(shouldEqual.get(i))){
                isSame = false;
            }
        }
    }
    assertTrue(BESTCOMBO_MESSAGE, isSame);
  }
  
  @Test(expected = IndexOutOfBoundsException.class)
  public void selectBestCombinationsEmptyTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    ArrayList<Item> items = new ArrayList<Item>();
    String [] firstItem = {"N", "N", "Y"};
    String [] secondItem = {"N", "N", "Y"};
    String [] thirdItem = {"N", "Y", "Y"};
    String [] fourthItem = {"N", "Y", "N"};
    String [] fifthItem = {"N", "N", "N"};
    items.add(new Item("F003", "Large", firstItem, "150", "002"));
    items.add(new Item("F010", "Large", secondItem, "225", "002"));
    items.add(new Item("F011", "Large", thirdItem, "225", "005"));
    items.add(new Item("F012", "Large", fourthItem, "75", "005"));
    items.add(new Item("F015", "Large", fifthItem, "75", "004"));
    ArrayList<Item> bestCombo = manager.selectBestCombination(items);
  }

  @Test
  public void runDeskTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.run("Adjustable", "desk", "1");
    ArrayList<String> allData = new ArrayList<String>();
    try {
        File obj = new File("OutputFile.txt");
        Scanner reader = new Scanner(obj);
        while (reader.hasNextLine()) {
          String data = reader.nextLine();
          allData.add(data);
        }
        reader.close();
    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
    ArrayList<String> shouldEqual = new ArrayList<String>();

    //Initialize our expected results into a string array.
    shouldEqual.add("Furniture Order Form");
    shouldEqual.add("");
    shouldEqual.add("Faculty Name:");
    shouldEqual.add("Contact:");
    shouldEqual.add("Date:");
    shouldEqual.add("");
    shouldEqual.add("Original Request: Adjustable desk, 1");
    shouldEqual.add("");
    shouldEqual.add("Items Ordered");
    shouldEqual.add("ID: D3682");
    shouldEqual.add("ID: D1030");
    shouldEqual.add("ID: D5437");
    shouldEqual.add("");
    shouldEqual.add("Total Price: $250");

    boolean isEqual = true;
    if(allData.size() == shouldEqual.size()){
        for(int i = 0; i<allData.size(); i++){
            if(!shouldEqual.get(i).equals(allData.get(i))){
                isEqual = false;
            }
        }
    }
    else{
        isEqual = false;
    }
    if(isEqual){
        ArrayList<Item> toRestore = new ArrayList<Item>();
        String[] firstItem = {"N", "N", "Y"};
        toRestore.add(new Item("D3682", "Adjustable", firstItem, "50", "005"));
        String[] secondItem = {"N", "Y", "N"};
        toRestore.add(new Item("D1030", "Adjustable", secondItem, "150", "002"));
        String[] thirdItem = {"Y", "N", "N"};
        toRestore.add(new Item("D5437", "Adjustable", thirdItem, "50", "001"));
        manager.initializeConnection();
        manager.resetSQL(toRestore);
        manager.close();
    }
    assertTrue(RUN_MESSAGE, isEqual);

  }
  
  @Test
  public void runDeskTwoQuantityTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    manager.run("Adjustable", "desk", "2");
    ArrayList<String> allData = new ArrayList<String>();
    try {
        File obj = new File("OutputFile.txt");
        Scanner reader = new Scanner(obj);
        while (reader.hasNextLine()) {
          String data = reader.nextLine();
          allData.add(data);
        }
        reader.close();
    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
    ArrayList<String> shouldEqual = new ArrayList<String>();

    //Initialize our expected results into a string array.
    shouldEqual.add("Furniture Order Form");
    shouldEqual.add("");
    shouldEqual.add("Faculty Name:");
    shouldEqual.add("Contact:");
    shouldEqual.add("Date:");
    shouldEqual.add("");
    shouldEqual.add("Original Request: Adjustable desk, 2");
    shouldEqual.add("");
    shouldEqual.add("Items Ordered");
    shouldEqual.add("ID: D2746");
    shouldEqual.add("ID: D1030");
    shouldEqual.add("ID: D4475");
    shouldEqual.add("ID: D5437");
    shouldEqual.add("");
    shouldEqual.add("Total Price: $650");
    System.out.println(allData.get(8));
    boolean isEqual = true;
    if(allData.size() == shouldEqual.size()){
        for(int i = 0; i<allData.size(); i++){
            if(!shouldEqual.get(i).equals(allData.get(i))){
                isEqual = false;
            }
        }
    }
    else{
        isEqual = false;
    }
    if(isEqual){
        ArrayList<Item> toRestore = new ArrayList<Item>();
        String[] firstItem = {"Y", "N", "Y"};
        toRestore.add(new Item("D2746", "Adjustable", firstItem, "250", "004"));
        String[] secondItem = {"N", "Y", "N"};
        toRestore.add(new Item("D1030", "Adjustable", secondItem, "150", "002"));
        String[] thirdItem = {"N", "Y", "Y"};
        toRestore.add(new Item("D4475", "Adjustable", thirdItem, "200", "002"));
        String[] fourthItem = {"Y", "N", "N"};
        toRestore.add(new Item("D5437", "Adjustable", fourthItem, "50", "001"));
        manager.initializeConnection();
        manager.resetSQL(toRestore);    
        manager.close();
    }
    assertTrue(RUN_MESSAGE, isEqual);
  }

  @Test
  public void runDeskNotPossibleTest(){
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    manager.run("Adjustable", "desk", "4");
    ArrayList<String> allData = new ArrayList<String>();
    try {
        File obj = new File("OutputFile.txt");
        Scanner reader = new Scanner(obj);
        while (reader.hasNextLine()) {
          String data = reader.nextLine();
          allData.add(data);
        }
        reader.close();
    } catch (FileNotFoundException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
    ArrayList<String> shouldEqual = new ArrayList<String>();

    //Initialize our expected results into a string array.
    shouldEqual.add("Furniture Order Form");
    shouldEqual.add("");
    shouldEqual.add("Faculty Name:");
    shouldEqual.add("Contact:");
    shouldEqual.add("Date:");
    shouldEqual.add("");
    shouldEqual.add("Original Request: Adjustable desk, 4");
    shouldEqual.add("");
    shouldEqual.add("Order cannot be fulfilled based on current inventory. Suggested manufacturers are Academic Desks, Office Furnishings, Furniture Goods, Fine Office Supplies.");

    boolean isEqual = true;
    if(allData.size() == shouldEqual.size()){
        for(int i = 0; i<allData.size(); i++){
            if(!shouldEqual.get(i).equals(allData.get(i))){
                isEqual = false;
            }
        }
    }
    else{
        isEqual = false;
    }
    
    assertTrue(RUN_MESSAGE, isEqual);
  }
  private boolean compareArrayList(ArrayList<Item> one, ArrayList<Item> two){
    if(one.size()!=two.size()){
        return false;
    }
    for(int i = 0; i<one.size(); i++){
        if(!(one.get(i).getId().equals(two.get(i).getId()))){
            return false;
        }
        if(!(one.get(i).getType().equals(two.get(i).getType()))){
            return false;
        }
        if(!(Arrays.equals(one.get(i).getTypeVariables(), two.get(i).getTypeVariables()))){
            return false;
        }
        if(!(one.get(i).getManuID().equals(two.get(i).getManuID()))){
            return false;
        }
        if(!(one.get(i).getPrice().equals(two.get(i).getPrice()))){
            return false;
        }
    }
    return true;
  }
  
  
  @Test
  public void powerSetTest() {
	  manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
	    //Initialize some data to put into create combos function 
	    ArrayList<Item> items = new ArrayList<Item>(3);
	    String [] firstItem = {"N", "N", "Y"};
	    String [] secondItem = {"Y", "N", "Y"};
	    String [] thirdItem = {"N", "Y", "Y"};
	    items.add(new Item("F003", "Large", firstItem, "150", "002"));
	    items.add(new Item("F010", "Large", secondItem, "225", "002"));
	    items.add(new Item("F011", "Large", thirdItem, "225", "005"));
	    int varsLength = items.get(0).getTypeVariables().length;
	    System.out.println(varsLength);
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

	    //Create combinations from parts
	    ArrayList<ArrayList<Item>> combinations = manager.createCombinations(parts);
	    combinations = manager.removeDuplicates(combinations);
	    combinations=manager.powerSet(2, combinations);
	    ArrayList<ArrayList<String>> shouldEqual=new ArrayList<ArrayList<String>>();
	    for(int i=0;i<9;i++) {
	        shouldEqual.add(new ArrayList<String>());
	    }
	    
	    shouldEqual.get(0).add("F003");
	    shouldEqual.get(0).add("F011");
	    shouldEqual.get(0).add("F010");
	    shouldEqual.get(0).add("F003");
	    shouldEqual.get(0).add("F011");
	    shouldEqual.get(0).add("F010");
	    shouldEqual.get(1).add("F003");
	    shouldEqual.get(1).add("F011");
	    shouldEqual.get(1).add("F010");
	    shouldEqual.get(1).add("F010");
	    shouldEqual.get(1).add("F011");
	    shouldEqual.get(2).add("F003");
	    shouldEqual.get(2).add("F011");
	    shouldEqual.get(2).add("F010");
	    shouldEqual.get(2).add("F011");
	    shouldEqual.get(2).add("F010");
	    shouldEqual.get(3).add("F010");
	    shouldEqual.get(3).add("F011");
	    shouldEqual.get(3).add("F003");
	    shouldEqual.get(3).add("F011");
	    shouldEqual.get(3).add("F010");
	    shouldEqual.get(4).add("F010");
	    shouldEqual.get(4).add("F011");
	    shouldEqual.get(4).add("F010");
	    shouldEqual.get(4).add("F011");
	    shouldEqual.get(5).add("F010");
	    shouldEqual.get(5).add("F011");
	    shouldEqual.get(5).add("F011");
	    shouldEqual.get(5).add("F010");
	    shouldEqual.get(6).add("F011");
	    shouldEqual.get(6).add("F010");
	    shouldEqual.get(6).add("F003");
	    shouldEqual.get(6).add("F011");
	    shouldEqual.get(6).add("F010");
	    shouldEqual.get(7).add("F011");
	    shouldEqual.get(7).add("F010");
	    shouldEqual.get(7).add("F010");
	    shouldEqual.get(7).add("F011");
	    shouldEqual.get(8).add("F011");
	    shouldEqual.get(8).add("F010");
	    shouldEqual.get(8).add("F011");
	    shouldEqual.get(8).add("F010");
	    
	    boolean isSame = true;
	    if(combinations.size()==shouldEqual.size()){
	        for(int i = 0; i<combinations.size(); i++){
	            if(combinations.get(i).size()==shouldEqual.get(i).size()){
	                for(int j = 0; j<combinations.get(i).size(); j++){
	                    if(!shouldEqual.get(i).get(j).equals(combinations.get(i).get(j).getId())){
	                        isSame = false;
	                    }
	                }
	            }
	            else{
	                isSame = false;
	            }
	        }
	    }
	    else{
	        isSame = false;
	    }
	    assertTrue(REMOVEDUP_MESSAGE, isSame);
	    
  }
  
	
	
  @Test
  public void sortTest() {
	    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
	    ArrayList<Item> items = new ArrayList<Item>();
	    String [] firstItem = {"Y", "N"};
	    String [] secondItem = {"N", "Y"};
	    String [] thirdItem = {"Y", "N"};
	    String [] fourthItem = {"Y", "Y"};
	    String [] fifthItem = {"N", "Y"};
	    items.add(new Item("L132", "Desk", firstItem, "18", "005"));
	    items.add(new Item("L980", "Study", secondItem, "2", "004"));
	    items.add(new Item("L487", "Swing Arm", thirdItem, "27", "002"));
	    items.add(new Item("L564", "Desk", fourthItem, "20", "004"));
	    items.add(new Item("L342", "Desk", fifthItem, "2", "002"));
	    
	    
	    ArrayList<Item> sorter = manager.sort(items);
	    ArrayList<String> shouldEqual = new ArrayList<String>();
	    shouldEqual.add("L132");
	    shouldEqual.add("L342");
	    shouldEqual.add("L487");
	    shouldEqual.add("L564");
	    shouldEqual.add("L980");
	    
	    boolean isSame = true;
	    if(sorter.size()!=shouldEqual.size()){
	        isSame = false;
	    }
	    else{
	        for(int i = 0; i<sorter.size(); i++){
	            if(!sorter.get(i).getId().equals(shouldEqual.get(i))){
	                isSame = false;
	            }
	        }
	    }
	    assertTrue(NICESORT_MESSAGE, isSame);
  }
	

  @Test
  public void initializeConnectionTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    assertTrue(INITIALIZE_MESSAGE, !manager.getDBConnect().isClosed());
  }
  
  @Test
  public void closeConnectionTest() throws Exception{
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    manager.close();
    assertTrue(CLOSE_MESSAGE, manager.getDBConnect().isClosed());
  }
  //public void runTest("name", 
}
