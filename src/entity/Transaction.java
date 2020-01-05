package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Transaction {
	private String itemId;
	private String userId;
	private Integer targetPrice;
	private Integer amount;
	private String buySell;
	
	public String getItemId() {
		return itemId;
	}
	public String getUserId() {
		return userId;
	}
	public Integer getTargetPrice() {
		return targetPrice;
	}
	public Integer getAmount() {
		return amount;
	}
	public String getBuySell() {
		return buySell;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", itemId);
			obj.put("user_id", userId);
			obj.put("target_price", targetPrice);
			obj.put("amount", amount);
			obj.put("buy_sell", buySell);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	private Transaction(Builder builder) {
		this.itemId = builder.itemId;
		this.userId = builder.userId;
		this.targetPrice = builder.targetPrice;
		this.amount = builder.amount;
		this.buySell = builder.buySell;
	}

	public static class Builder {
		private String itemId;
		private String userId;
		private Integer targetPrice;
		private Integer amount;
		private String buySell;

		public Builder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}

		public Builder setUserId(String userId) {
			this.userId = userId;
			return this;
		}

		public Builder setTargetPrice(int targetPrice) {
			this.targetPrice = targetPrice;
			return this;
		}

		public Builder setAmount(int amount) {
			this.amount = amount;
			return this;
		}

		public Builder setBuySell(String buySell) {
			this.buySell = buySell;
			return this;
		}

		public Transaction build() {
			return new Transaction(this);
		}
	}

}
