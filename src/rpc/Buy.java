package rpc;

import java.io.IOException;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Transaction;

/**
 * Servlet implementation class Buy
 */
@WebServlet("/buy")
public class Buy extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBConnection conn = DBConnectionFactory.getDBConnection();
	

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buy() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		 if (request.getParameter("username") != null) {
	   		 String username = request.getParameter("username");
	   		 out.print("Hello " + username);
	   	 }
	   	 out.flush();
	   	 out.close(); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			String itemId = input.getString("item_id");
			Integer targetPrice = input.getInt("target_price");
			Integer amount = input.getInt("amount");
			String buySell = input.getString("buy_sell");
			
			Transaction transaction = new Transaction.Builder()
					.setUserId(userId)
					.setItemId(itemId)
					.setAmount(amount)
					.setBuySell(buySell)
					.setTargetPrice(targetPrice)
					.build();

			conn.createTransaction(transaction);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
