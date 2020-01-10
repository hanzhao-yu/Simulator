package listener;

public class Price {
	private static int spotPrice = 0;
	private static int buyPrice = 0;
	private static int sellPrice = 0;
	private static final Object lock = new Object();

	public int spotPrice(int cur) {
		synchronized (lock) {
			if (cur == 0) {
				return spotPrice;
			} else {
				spotPrice = cur;
				return cur;
			}
		}
	}
	
	public int buyPrice(int cur) {
		synchronized (lock) {
			if (cur == 0) {
				return buyPrice;
			} else {
				buyPrice = cur;
				return cur;
			}
		}
	}
	
	public int sellPrice(int cur) {
		synchronized (lock) {
			if (cur == 0) {
				return sellPrice;
			} else {
				sellPrice = cur;
				return cur;
			}
		}
	}
}
