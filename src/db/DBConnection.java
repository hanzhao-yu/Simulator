package db;

import java.util.List;
import java.util.Set;

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
	public Set<Transaction> searchTransactions(String userId);
	
	/**
	 * Search transactions ids by userId.
	 * 
	 * @param userId
	 * @return list of String
	 */
	public Set<String> getTransactionsIds(String userId);

	/**
	 * create transaction into db.
	 * 
	 * @param item
	 */
	public void createTransaction(Transaction item);
	
	/**
	 * update user account.
	 * 
	 * @param userId
	 * @param usd
	 * @param btc
	 */
	public void updateUser(String userId, Integer usd, Integer btc);
}
