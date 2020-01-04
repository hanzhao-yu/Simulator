package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import entity.Transaction;

public class MySQLConnection implements DBConnection {
	private static MySQLConnection instance;

	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new MySQLConnection();
		}
		return instance;
	}

	private Connection conn = null;

	private MySQLConnection() {
		try {
			// Forcing the class representing the MySQL driver to load and
			// initialize.
			// The newInstance() call is a work around for some broken Java
			// implementations.
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void deleteTransaction(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Transaction> searchTransactions(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createTransaction(Transaction item) {
		// TODO Auto-generated method stub
		
	}
}

