package db.mysql;

public class MySQLDBUtil {
  private static final String HOSTNAME = "127.0.0.1";
  private static final String PORT_NUM = "3306";
  public static final String DB_NAME = "eventsadvisor";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "root";
  public static final String URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
      + "?user=" + USERNAME + "&password=" + PASSWORD + "&autoReconnect=true" + "&useSSL=false"
		  + "&testOnBorrow=true";
}