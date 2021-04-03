import static org.junit.Assert.*;
import org.junit.*;
import java.util.Arrays;
import jdk.jfr.Timestamp;

//import java.sql.*;
import java.util.ArrayList;

/*
FUNCTIONS TO TEST:

SupplyChainManager - done
initializeConnection - todo
run - ?
deleteID - todo
selectItem - done
selectManufacturers - done
newItem - tested in selectItem...
selectBestCombination - todo
getPriceForCombination - todo
removeDuplicates - in progress
createCombinations - done

make sure initialize and close is working as expected
*/

public class SupplyChainManagerTest {
  public static String USERNAME = "max";
  public static String PASSWORD = "ensf409";
  public static String DBURL = "jdbc:mysql://localhost/INVENTORY";
  public static String CONSTRUCTOR_MESSAGE = "The constructor of SupplyChainManager failed to initialize constants.";
  public static String SELECTITEM_MESSAGE = "The function selectItem of SupplyChainManager failed to return the correct ArrayList<Item>.";
  public static String SELECTMANU_MESSAGE = "The function selectManufacturers of SupplyChainManager failed to return the correct ArrayList<String>.";
  public static String CREATECOMBO_MESSAGE = "The function createCombinations of SupplyChainManager failed to return the correct Arraylist<Arraylist<Item>>";
  public static String REMOVEDUP_MESSAGE = "The function removeDuplicates of SupplyChainManager failed to remove duplicates.";
  public static String GETPRICE_MESSAGE = "The function getPriceForCombinations of SUpplyCHainManager failed to get the correct price for the combinations calculated.";
  private SupplyChainManager manager;
  @BeforeClass
  public static void initializeConnection(){
    
    /*try{
        dbConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    } catch (SQLException e) {
        //if failed, throw an SQLException
        e.printStackTrace();
    }*/
  }
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
  public void selectItemDeskTest() {
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItem("Traditional", "desk");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(4);
    String [] firstItem = {"N", "N", "Y"};
    String [] secondItem = {"N", "Y", "Y"};
    String [] thirdItem = {"Y", "Y", "N"};
    String [] fourthItem = {"Y", "N", "Y"};
    shouldEqual.add(new Item("D0890", "Traditional", firstItem, "25", "002"));
    shouldEqual.add(new Item("D4231", "Traditional", secondItem, "50", "005"));
    shouldEqual.add(new Item("D8675", "Traditional", thirdItem, "75", "001"));
    shouldEqual.add(new Item("D9352", "Traditional", fourthItem, "75", "002"));
    assertTrue(SELECTITEM_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemChairTest() {
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItem("Mesh", "chair");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(4);
    String [] firstItem = {"Y", "N", "Y", "Y"};
    String [] secondItem = {"Y", "N", "N", "N"};
    String [] thirdItem = {"N", "N", "Y", "N"};
    String [] fourthItem = {"N", "Y", "N", "Y"};
    shouldEqual.add(new Item("C0942", "Mesh", firstItem, "100", "005"));
    shouldEqual.add(new Item("C6748", "Mesh", secondItem, "75", "003"));
    shouldEqual.add(new Item("C8138", "Mesh", thirdItem, "75", "005"));
    shouldEqual.add(new Item("C9890", "Mesh", fourthItem, "50", "003"));
    assertTrue(SELECTITEM_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemFilingTest() {
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItem("Large", "filing");
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
    assertTrue(SELECTITEM_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemLampTest() {
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItem("Swing Arm", "lamp");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(4);
    String [] firstItem = {"Y", "N"};
    String [] secondItem = {"N", "Y"};
    String [] thirdItem = {"Y", "N"};
    String [] fourthItem = {"N", "Y"};
    shouldEqual.add(new Item("L053", "Swing Arm", firstItem, "27", "002"));
    shouldEqual.add(new Item("L096", "Swing Arm", secondItem, "3", "002"));
    shouldEqual.add(new Item("L487", "Swing Arm", thirdItem, "27", "002"));
    shouldEqual.add(new Item("L879", "Swing Arm", fourthItem, "3", "005"));
    assertTrue(SELECTITEM_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectItemFakeTypeTest() {
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<Item> returned = manager.selectItem("Does Not Exist", "lamp");
    ArrayList<Item> shouldEqual = new ArrayList<Item>(0);
    assertTrue(SELECTITEM_MESSAGE, compareArrayList(returned, shouldEqual));
  }

  @Test
  public void selectManufacturersTest() {
    manager = new SupplyChainManager(DBURL, USERNAME, PASSWORD);
    manager.initializeConnection();
    ArrayList<String> returned = manager.selectManufacturers();
    ArrayList<String> shouldEqual = new ArrayList<String>(5);
    shouldEqual.add("Academic Desks");
    shouldEqual.add("Office Furnishings");
    shouldEqual.add("Chairs R Us");
    shouldEqual.add("Furniture Goods");
    shouldEqual.add("Fine Office Supplies");

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
 
  private boolean compareArrayList(ArrayList<Item> one, 
  
  ArrayList<Item> two){
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

  //public void runTest("name", 
}