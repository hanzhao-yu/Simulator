package db;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.Transaction;
import listener.Price;

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
	public void deleteTransaction(String userId, String itemId, boolean complete) {
		String query = "DELETE FROM transactions WHERE user_id = ? and item_id = ?";
		PreparedStatement statement;
		try {
			if (!complete) {
				String sql = "SELECT * from transactions WHERE item_id = ? AND user_id = ?";
				statement = conn.prepareStatement(sql);
				statement.setString(1, itemId);
				statement.setString(2, userId);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					String buy_sell = rs.getString("buy_sell");
					if (buy_sell.equals("sell")) {
						updateUser(userId, 0, rs.getInt("amount"));
					} else {
						BigInteger usd = new BigInteger(((Integer) rs.getInt("amount")).toString())
								.multiply(new BigInteger(((Integer) rs.getInt("target_price")).toString()))
								.divide(new BigInteger("10000000"));
						updateUser(userId, usd.intValue(), 0);
					}
				}
			}
			statement = conn.prepareStatement(query);
			statement.setString(1, userId);
			statement.setString(2, itemId);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<Transaction> searchTransactions(String userId) {
		Set<String> itemIds = getTransactionsIds(userId);
		Set<Transaction> resultTransactions = new HashSet<>();
		try {

			for (String itemId : itemIds) {
				String sql = "SELECT * from transactions WHERE item_id = ? AND user_id = ?";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, itemId);
				statement.setString(2, userId);
				ResultSet rs = statement.executeQuery();
				Transaction.Builder builder = new Transaction.Builder();

				if (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setUserId(rs.getString("user_id"));
					builder.setAmount(rs.getInt("amount"));
					builder.setTargetPrice(rs.getInt("target_price"));
					builder.setBuySell(rs.getString("buy_sell"));
				}

				resultTransactions.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultTransactions;
	}

	@Override
	public void createTransaction(Transaction item) {
		try {
			// insert into transactions table
			String sql = "INSERT INTO transactions VALUES (?,?,?,?,?)";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getUserId());
			statement.setInt(3, item.getTargetPrice());
			statement.setInt(4, item.getAmount());
			statement.setString(5, item.getBuySell());
			statement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateUser(String userId, Integer usd, Integer btc) {
		String query = "UPDATE users SET usd_asset = usd_asset + ?, btc_asset = btc_asset + ? WHERE user_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(query);

			statement.setInt(1, usd);
			statement.setInt(2, btc);
			statement.setString(3, userId);
			statement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Set<String> getTransactionsIds(String userId) {
		Set<String> transactions = new HashSet<>();
		try {
			String sql = "SELECT item_id from transactions WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				transactions.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return transactions;

	}

	@Override
	public void updateTransactions(Integer buyPrice, Integer sellPrice) {
		try {
			String sql = "SELECT * from transactions WHERE buy_sell = \"buy\" AND target_price > ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, buyPrice);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Integer amount = rs.getInt("amount");
				String userId = rs.getString("user_id");
				String itemId = rs.getString("item_id");
				updateUser(userId, 0, amount);
				deleteTransaction(userId, itemId, true);
			}
			sql = "SELECT * from transactions WHERE buy_sell = \"sell\" AND target_price < ?";
			statement = conn.prepareStatement(sql);
			statement.setInt(1, sellPrice);
			rs = statement.executeQuery();
			while (rs.next()) {
				Integer amount = rs.getInt("amount");
				Integer target = rs.getInt("target_price");
				String userId = rs.getString("user_id");
				String itemId = rs.getString("item_id");

				BigInteger usd = new BigInteger(((Integer) amount).toString())
						.multiply(new BigInteger(((Integer) target).toString())).divide(new BigInteger("10000000"));
				updateUser(userId, usd.intValue(), 0);

				deleteTransaction(userId, itemId, true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addUser(String user_id, String password) {
		try {
			String sql = "INSERT IGNORE INTO users " + "VALUES (?, ?, 10000000, 10000000)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, password);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getUser(String user_id, String password) {
		Price spot = new Price();
		String[] res = new String[4];
		try {
			String sql = "SELECT * from users WHERE user_id = ? AND password = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user_id);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String userId = rs.getString("user_id");
				Integer usd = rs.getInt("usd_asset");
				Integer btc = rs.getInt("btc_asset");
				res[0] = userId;
				res[1] = Long.toString((long) btc * spot.spotPrice(0) / 10000000 + usd);
				res[2] = Long.toString(usd);
				res[3] = Long.toString(btc);
			}
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
