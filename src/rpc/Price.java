package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Price
 */
@WebServlet("/price")
public class Price extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private listener.Price price = new listener.Price();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Price() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String spot = ((Integer)price.spotPrice(0)).toString();
			String buy = ((Integer)price.buyPrice(0)).toString();
			String sell = ((Integer)price.sellPrice(0)).toString();
		      RpcHelper.writeJsonObject(response, new JSONObject()
		    		  .put("spot", spot.substring(0, spot.length()-3)+'.'+spot.substring(spot.length()-3, spot.length()))
		    		  .put("sell", sell.substring(0, sell.length()-3)+'.'+sell.substring(sell.length()-3, sell.length()))
		    		  .put("buy", buy.substring(0, buy.length()-3)+'.'+buy.substring(buy.length()-3, buy.length())));
		    } catch (JSONException e) {
		      e.printStackTrace();
		    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
