package db;

import java.util.List;

import entity.Transaction;

public interface DBConnection {
	/**
	 * Close the connection.
	 */
	public void close();

	/**
	 * Delete the transactions for a user.
	 * 
	 * @param userId
	 * @param itemIds
	 */
	public void deleteTransaction(String userId, List<String> itemIds);

	/**
	 * Search transactions by userId.
	 * 
	 * @param userId
	 * @return list of transactions
	 */
	public List<Transaction> searchTransactions(String userId);

	/**
	 * create transaction into db.
	 * 
	 * @param item
	 */
	public void createTransaction(Transaction item);
}
