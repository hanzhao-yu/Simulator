package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class CoinbaseAPI {
	private static final String API_HOST = "api.coinbase.com";
	private static final String PRICE_PATH = "/v2/prices";
	private static final String DEFAULT_CURRENCY = "/BTC-USD";
	private static final String DEFAULT_TERM = ""; //optional

	/**
	 * Creates and sends a request to the Coinbase API for current price.
	 */
	public JSONObject buyPrice(String currency) {
		
		if (currency == null) {
			currency = DEFAULT_CURRENCY;
		}
		
		return helper(String.format("%s/buy", currency));
	}
	
	public JSONObject buyPrice() {	
		return helper(String.format("%s/buy", DEFAULT_CURRENCY));
	}
	
	public JSONObject sellPrice(String currency) {
		
		if (currency == null) {
			currency = DEFAULT_CURRENCY;
		}
		
		return helper(String.format("%s/sell", currency));
	}
	
	public JSONObject sellPrice() {
		return helper(String.format("%s/sell", DEFAULT_CURRENCY));
	}
	
	public JSONObject spotPrice(String currency, String date) {
		
		if (currency == null) {
			currency = DEFAULT_CURRENCY;
		}
		
		if (date == null) {
			date = DEFAULT_TERM;
		}
		
		return helper(String.format("%s/spot/%s", currency, date));
	}
	
	public JSONObject spotPrice(String currency) {
		
		if (currency == null) {
			currency = DEFAULT_CURRENCY;
		}
		
		return helper(String.format("%s/spot", currency));
	}
	
	public JSONObject spotPrice() {		
		return helper(String.format("%s/spot", DEFAULT_CURRENCY));
	}
	
	
	
	private JSONObject helper(String query) {
		
		String url = "https://" + API_HOST + PRICE_PATH;
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url + query).openConnection();
			connection.setRequestMethod("GET");
 
 
			
//			int responseCode = connection.getResponseCode();
//			System.out.println("\nSending 'GET' request to URL : " + url + query);  //test
//			System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject responseJson = new JSONObject(response.toString());
			JSONObject data = (JSONObject) responseJson.get("data");


			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	//
	private void queryAPI() {
		JSONObject spot = spotPrice();
		JSONObject buy = buyPrice();
		JSONObject sell = sellPrice();
		try {
			System.out.println(spot);
			System.out.println(buy);
			System.out.println(sell);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	/**
	 * Main entry for sample Coinbase API requests.
	 */
	public static void main(String[] args) {
		CoinbaseAPI tmApi = new CoinbaseAPI();
		tmApi.queryAPI();
	}

}
