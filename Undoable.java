/**
 * This interface should be implemented by any Command that can be undone.
 * @author Katherine Coronado
 *
 */
public interface Undoable {
  public void undo() throws Exception;
  public String getUndoMessage();
}
