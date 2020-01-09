package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class UserInit
 */
@WebServlet("/user")
public class User extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBConnection conn = DBConnectionFactory.getDBConnection();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public User() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		      String userId = request.getParameter("user_id");
		      String password = request.getParameter("password");
		      String[] res = conn.getUser(userId, password);
		      if (res == null) return;
		      while (res[1].length() < 4) {
		    	  res[1] = '0'+res[1];
		      }
		      while (res[2].length() < 4) {
		    	  res[2] = '0'+res[2];
		      }
		      while (res[3].length() < 8) {
		    	  res[3] = '0'+res[3];
		      }
		      RpcHelper.writeJsonObject(response, new JSONObject().put("userId", userId)
		    		  .put("total", res[1].substring(0, res[1].length()-3)+'.'+res[1].substring(res[1].length()-3, res[1].length()))
		    		  .put("usd", res[2].substring(0, res[2].length()-3)+'.'+res[2].substring(res[2].length()-3, res[2].length()))
		    		  .put("btc", res[3].substring(0, res[3].length()-7)+'.'+res[3].substring(res[3].length()-7, res[3].length())));
		    } catch (JSONException e) {
		      e.printStackTrace();
		    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		      JSONObject input = RpcHelper.readJsonObject(request);
		      String userId = input.getString("user_id");
		      String password = input.getString("password");
		      conn.addUser(userId, password);
		      RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		    } catch (JSONException e) {
		      e.printStackTrace();
		    }
	}

}
