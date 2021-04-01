import static org.junit.Assert.*;
import org.junit.*;
import java.util.Arrays;
import jdk.jfr.Timestamp;

//import java.sql.*;
import java.util.ArrayList;

/*
FUNCTIONS TO TEST:

SupplyChainManager - done
initializeConnection - ?
run - ?
deleteID - todo
selectItem - in progress
selectManufacturers - todo
newItem - tested in selectItem...
selectBestCombination - todo
getPriceForCombination - todo
removeDuplicates - todo
createCombinations - todo
*/

public class SupplyChainManagerTest {
  public static String USERNAME = "max";
  public static String PASSWORD = "ensf409";
  public static String DBURL = "jdbc:mysql://localhost/INVENTORY";
  public static String CONSTRUCTOR_MESSAGE = "The constructor of SupplyChainManager failed to initialize constants.";
  public static String SELECTITEM_MESSAGE = "The function selectItem() of SupplyChainManager failed to return the correct ArrayList.";
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

  /*@Test
  public void selectItemTest() {
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
    boolean doesMatch = compareArrayList(returned, shouldEqual);
    assertTrue(SELECTITEM_MESSAGE, doesMatch);
  }*/

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

  //public void runTest("name", 
}