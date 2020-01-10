package listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONException;

import db.DBConnection;
import db.DBConnectionFactory;
import external.CoinbaseAPI;

/**
 * Application Lifecycle Listener implementation class Listener
 *
 */
@WebListener
public class TransactionUpdater implements ServletContextListener {
	private DBConnection conn = DBConnectionFactory.getDBConnection();
	private CoinbaseAPI api = new CoinbaseAPI();
	private Price price = new Price();
	private ScheduledExecutorService scheduler;

	/**
	 * Default constructor.
	 */
	public TransactionUpdater() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		scheduler.shutdownNow();
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					Integer buyPrice = (int) (api.buyPrice().getDouble("amount") * 1000);
					Integer sellPrice = (int) (api.sellPrice().getDouble("amount") * 1000);
					Integer spotPrice = (int) (api.spotPrice().getDouble("amount") * 1000);
					price.spotPrice(spotPrice);
					price.buyPrice(buyPrice);
					price.sellPrice(sellPrice);
					conn.updateTransactions(buyPrice, sellPrice);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

}
