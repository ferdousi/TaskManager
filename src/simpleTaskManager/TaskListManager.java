package simpleTaskManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskListManager {
   private TreeMap<Task, Task> taskList;
   private String userName;
   private String fileName;
   private Connection connection;
   
   public String getUser() {
      return userName;
   }
   
   public void setUser(String userName) {
      this.userName = userName;
      if (connection != null) try {
         Statement statement = connection.createStatement();

         statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + userName + "Tasks(name STRING, status STRING);");
//         statement.executeUpdate("DROP TABLE IF EXISTS " + userName + "Tasks");
//         statement.executeUpdate("CREATE TABLE " + userName + "Tasks(name STRING, status STRING)");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something1', 'TODO')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something2', 'TODO')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something3', 'TODO')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something4', 'IN_PROGRESS')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something5', 'IN_PROGRESS')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something6', 'IN_PROGRESS')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something7', 'DONE')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something8', 'DONE')");
//         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('Something9', 'DONE')");
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.loadTaskList();
   }
   
   private void loadTaskList() {
      if (connection != null) try {
         Statement statement = connection.createStatement();
         ResultSet rs = statement.executeQuery("SELECT * FROM " + userName + "Tasks");
         this.taskList = new TreeMap();
         
         while(rs.next()) {
            Task task = new Task(rs.getString("name"), TaskStatus.valueOf(rs.getString("status")));
            this.taskList.put(task, task);
         }
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   public String[] getTaskList(TaskStatus status) {
      String[] toReturn = new String[taskList.size()];
      int i = 0;
      for (Map.Entry<Task, Task> entry : taskList.entrySet()) {
         if (entry.getValue().getStatus() == status) {
            toReturn[i] = entry.getValue().getName();
            ++i;
         }
      }
      toReturn = java.util.Arrays.copyOf(toReturn, i);
      return toReturn;
   }

   public void addTask(Task task) throws AddTaskException {
      if (taskList.containsKey(task) == true) throw new AddTaskException();

      if (connection != null) try {
         Statement statement = connection.createStatement();
         statement.executeUpdate("INSERT INTO " + userName + "Tasks VALUES('" + task.getName() + "', '" + task.getStatus().name() + "');");
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }

      taskList.put(task, task);
   }

   public void editTask(Task task) throws EditTaskException {
      if (taskList.containsKey(task) == false) throw new EditTaskException();
      
      if (connection != null) try {
         Statement statement = connection.createStatement();
         statement.executeUpdate("UPDATE " + userName + "Tasks SET status = '" + task.getStatus().name() + "' WHERE name = '" + task.getName() + "';");
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }

      taskList.put(task, task);
   }
   
   public TaskListManager (String fileName) {
      this.fileName = fileName;
      openConnection(fileName);
      
      try {
         Statement statement;
         statement = connection.createStatement();
         
         statement.executeUpdate("DROP TABLE IF EXISTS users");
         statement.executeUpdate("CREATE TABLE users(name STRING)");
         statement.executeUpdate("INSERT INTO users VALUES('Bob')");
         statement.executeUpdate("INSERT INTO users VALUES('Sally')");
         statement.executeUpdate("INSERT INTO users VALUES('Sue')");
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   private void openConnection(String fileName) {
      try {
         connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   public String[] getUserList() {
      String[] toReturn = null;
      
      if (connection != null) try {
         Statement statement;
         ResultSet rs;
         
         statement = connection.createStatement();
         
         rs = statement.executeQuery("SELECT COUNT(*) FROM users");
         int numberOfUsers = 0;
         if (rs.next()) numberOfUsers = rs.getInt(1);
         toReturn = new String[numberOfUsers];
         
         rs = statement.executeQuery("SELECT * FROM users");
         for (int i = 0; rs.next(); ++i)
            toReturn[i] = rs.getString("name");
         
      } catch (SQLException ex) {
         Logger.getLogger(TaskListManager.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return toReturn;
   }

   Task getTask(String string) {
      return taskList.get(new Task(string, TaskStatus.TODO));
   }
}
