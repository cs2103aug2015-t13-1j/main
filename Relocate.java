
public class Relocate extends Command {
	private Task task;
	
	public Relocate(Task task) {
		this.task = task;
	}
	
	public void execute() throws Exception {
		StorageManager.changeStorageLocation(task.getName());
	}

	@Override
	public String getSuccessMessage() {
		// TODO Auto-generated method stub
		return "Directory was successfully changed to " + task.getName();
	}
}
