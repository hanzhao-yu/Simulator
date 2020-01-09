package listener;

public class SpotPrice {
	private static int spotPrice = 0;
	private static final Object lock = new Object();

	public int price(int cur) {
		synchronized (lock) {
			if (cur == 0) {
				return spotPrice;
			} else {
				spotPrice = cur;
				return cur;
			}
		}
	}
}
