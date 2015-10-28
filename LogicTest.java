import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class LogicTest {

  @Test
  public void testSearchTasks() {
    StorageManager.openStorage();
    
    Task apple = new Task("apple", false);
    Task banana = new Task("banana", false);
    Task baby = new Task("baby", false);
    Task appleBanana = new Task("apple banana", false);
    
    StorageManager.writeTask(apple);
    StorageManager.writeTask(banana);
    StorageManager.writeTask(baby);
    StorageManager.writeTask(appleBanana);
    
    ArrayList<Task> expected = new ArrayList<Task>();
    ArrayList<Task> actual;
    
    // test searching "b"
    // equivalence partition for searching for one keyword
    actual = Logic.searchTasks(new String[] {"b"});
    expected.add(banana);
    expected.add(baby);
    expected.add(appleBanana);
    assertEquals(expected, actual);
    
    // test searching "apple b"
    // equivalence partition for searching with more than one keyword
    actual = Logic.searchTasks(new String[] {"apple", "b"});
    expected.clear();
    expected.add(appleBanana);
    assertEquals(expected, actual);
    
    // test searching "c"
    // equivalence partition for searching and not finding anything
    actual = Logic.searchTasks(new String[] {"c"});
    expected.clear();
    assertEquals(expected, actual);
    
    // reset the storage file
    try {
      StorageManager.removeTask(apple);
      StorageManager.removeTask(baby);
      StorageManager.removeTask(banana);
      StorageManager.removeTask(appleBanana);
      StorageManager.closeStorage();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
