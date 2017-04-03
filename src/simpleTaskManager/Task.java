package simpleTaskManager;

public class Task implements Comparable<Task> {
   
   private String name;

   private TaskStatus status;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public TaskStatus getStatus() {
      return status;
   }

   public void setStatus(TaskStatus status) {
      this.status = status;
   }

   public Task(String name, TaskStatus status) {
      this.name = name;
      this.status = status;
   }
   @Override
   public int compareTo(Task o) {
      return this.name.compareTo(o.name);
   }
}
