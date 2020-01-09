package rpc;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Transaction;

/**
 * Servlet implementation class Buy
 */
@WebServlet("/trade")
public class Trade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBConnection conn = DBConnectionFactory.getDBConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Trade() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String userId = request.getParameter("user_id");
		String password = request.getParameter("password");
		String[] res = conn.getUser(userId, password);
		Set<Transaction> transactions;
		if (res == null) {
			return;
		} else {
			transactions = conn.searchTransactions(userId);
		}
		JSONArray array = new JSONArray();
		for (Transaction transaction : transactions) {
			JSONObject obj = transaction.toJSONObject();
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			String itemId = input.getString("item_id");
			String targetPriceStr = input.getString("target_price");
			String amountStr = input.getString("amount");
			String buySell = input.getString("buy_sell");
			String password = input.getString("password");
			
			if (targetPriceStr == null || targetPriceStr.length() == 0 || amountStr == null || amountStr.length() == 0) {
				return;
			}

			Integer targetPrice = (new BigDecimal(targetPriceStr).multiply(new BigDecimal(1000))).toBigInteger()
					.intValue();
			Integer amount = (new BigDecimal(amountStr).multiply(new BigDecimal(10000000))).toBigInteger().intValue();
			if (amount == 0 || targetPrice == 0) {
				return;
			}

			String[] user = conn.getUser(userId, password);
			if (user == null) {
				return;
			}

			if (buySell.equals("sell")) {
				if (Integer.parseInt(user[3]) < amount) {
					return;
				} else {
					conn.updateUser(userId, 0, -amount);
				}
			}
			if (buySell.equals("buy")) {
				Integer usd = new BigDecimal(targetPriceStr).multiply(new BigDecimal(amountStr))
						.multiply(new BigDecimal(1000)).toBigInteger().intValue();
				if (Integer.parseInt(user[2]) < usd) {
					return;
				} else {
					conn.updateUser(userId, -usd, 0);
				}
			}

			Transaction transaction = new Transaction.Builder().setUserId(userId).setItemId(itemId).setAmount(amount)
					.setBuySell(buySell).setTargetPrice(targetPrice).build();

			conn.createTransaction(transaction);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			String itemId = input.getString("item_id");
			conn.deleteTransaction(userId, itemId, false);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
