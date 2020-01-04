package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTableCreation {

	public static void main(String[] args) {
		try {
			// Ensure the driver is imported.
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			// java.sql.Connection
			Connection conn = null;

			try {
				System.out.println("Connecting to \n" + MySQLDBUtil.URL);
				conn = DriverManager.getConnection(MySQLDBUtil.URL);
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}

			// Step 1 Drop tables in case they exist.
			Statement stmt = conn.createStatement();

			String sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS transactions";
			stmt.executeUpdate(sql);

			
			// Step 2. Create new tables.
			sql = "CREATE TABLE users " + "(user_id VARCHAR(255) NOT NULL, " + " password VARCHAR(255) NOT NULL, "
					+ " usd_asset INT NOT NULL, btc_asset INT NOT NULL, " + " PRIMARY KEY ( user_id ))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE transactions " + "(item_id VARCHAR(255) NOT NULL, " + " user_id VARCHAR(255) NOT NULL, "
					+ "target_price INT NOT NULL, " + "amount INT NOT NULL, " + "buy_sell VARCHAR(255) NOT NULL, "
					+ " PRIMARY KEY ( item_id, user_id )," + " FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);


			
			// Step 3: insert data
			// Create a fake user
			sql = "INSERT INTO users " + "VALUES (\"1111\", \"3229c1097c00d497a0fd282d586be050\", 123, 321)";

			System.out.println("Executing query:\n" + sql);
			stmt.executeUpdate(sql);
			
			System.out.println("Import is done successfully.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}

