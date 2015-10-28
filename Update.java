import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Update command to handle updating the fields of a task.
 * @author Katherine Coronado
 *
 */

public class Update extends Command implements Undoable {
  private static final String SUCCESS_UPDATE = "\"%s\" was updated to \"%s\".";
  private static final String SUCCESS_UPDATE_UNDO = "Update undone";
  private static final String ERROR_INDEX_INVALID = "The task number specified is not valid.";
  private static final String ERROR_UPDATED_TASK_IS_INVALID = "The update failed because performing these changes would have resulted in an invalid task."; 
  private Task oldTask;
  private Task newTask;
  private DeltaTask changes;
  private int index;
  private boolean wasExecuted;
  
  public Update(int taskNumber, DeltaTask changes) {
    this.oldTask = null;
    this.newTask = null;
    this.changes = changes;
    this.index = taskNumber - 1;
    this.wasExecuted = false;
  }

  @Override
  /**
   * Update the task.
   */
  public void execute() throws Exception {
    ArrayList<Task> taskList = Ui.getCurrentTaskList();
    if (index >= 0 && index < taskList.size()) {
      oldTask = taskList.get(index);
      createUpdatedTask();
      StorageManager.updateTask(oldTask, newTask);
    } else {
      throw new Exception(ERROR_INDEX_INVALID);
    }
    wasExecuted = true;
  }

  private void createUpdatedTask() throws Exception {
    String newName = null;
    
    switch(changes.getNameAction()) {
      case UPDATE :
        newName = changes.getNewName();
        break;
        
      case NONE :
        newName = oldTask.getName();
        break;
        
      case REMOVE :
        // command parser should not allow name's action to be initialized to REMOVE
        assert("A request to remove task name slipped through command parser's "
            + "defences, execution should not reach here" == null);
    }
    
    assert(newName != null);
    
    // for requests to remove non-existent fields, like removing the start date 
    // off a floating task, forgive and ignore the error
    LocalDateTime newStart = null;
    switch(changes.getStartAction()) {
      case UPDATE :
        newStart = changes.getNewStart();
        break;
        
      case NONE :
        newStart = oldTask.getStartDateTime();
        break;
        
      case REMOVE :
        newStart = null;
    }
  
    LocalDateTime newEnd = null;
    switch(changes.getEndAction()) {
      case UPDATE :
        newEnd = changes.getNewEnd();
        break;
        
      case NONE :
        newEnd = oldTask.getEndDateTime();
        break;
        
      case REMOVE :
        newEnd = null;
    }
  
    // now check that the combination of newName, newStart and newEnd is valid e.g forms one of the 3 task types
    if (isTaskParametersValid(newName, newStart, newEnd) == false) {
      throw new Exception(ERROR_UPDATED_TASK_IS_INVALID);
    }
    
    newTask = new Task(newName, newStart, newEnd, oldTask.isDone());
  }
  
  private static boolean isTaskParametersValid(String name, LocalDateTime start, LocalDateTime end) {
    assert(name != null);
    
    boolean isFloating = false, isDeadline = false, isEvent = false;
    
    if (start == null && end == null) {
      isFloating = true;
    }
    
    if (start == null && end != null) {
      isDeadline = true;
    }
    
    if (start != null && end != null) {
      isEvent = true;
    }
    
    return isFloating || isDeadline || isEvent;
  }
  
  @Override
  /**
   * Restore the task to the old state prior to executing the update command.
   */
  public void undo() throws Exception {
    StorageManager.updateTask(newTask, oldTask);
  }

  @Override
  public String getSuccessMessage() {
    assertTrue(wasExecuted);
    // TODO check which fields were modified and display only those fields in message
    return String.format(SUCCESS_UPDATE, oldTask.getName(), newTask.getName());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) { 
      return false; 
    }
    
    Update other = (Update)obj;   
    if (!this.getOldTask().equals(other.getOldTask())) {
      return false;
    } else if (!this.getNewTask().equals(other.getNewTask())) {
      return false;
    } else {
      return true;
    }
  }
  
  public Task getOldTask() {
    return this.oldTask;
  }
  
  public Task getNewTask() {
    return this.newTask;
  }

  @Override
  public String getUndoMessage() {
    // TODO update message with which fields were modified
    return SUCCESS_UPDATE_UNDO;
  }
}
