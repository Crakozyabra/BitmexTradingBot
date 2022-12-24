package bots;

import bots.algoritms.TradingAlgoritm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

public class BitmexTradingBot implements TradingBot, Runnable{

  private int botId;
  private String apiKey;
  private TradingAlgoritm tradingAlgoritm;
  private static Logger logger = LogManager.getLogger();

  public BitmexTradingBot(String apiKey, TradingAlgoritm tradingAlgoritm) {
    this.botId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    this.apiKey = apiKey;
    this.tradingAlgoritm = tradingAlgoritm;
  }

  @Override
  public void run() {

    tradingAlgoritm.makeStartOrders();
    while (!Thread.currentThread().isInterrupted()) {
      logger.trace("Cycle iteration. Bot id=" + botId);
      try {
        tradingAlgoritm.updateFilledOrderIdsSet();
      } catch (Exception e) {
        logger.error("filledOrderIdsSet do not update. Bot id=" + botId);
        throw new RuntimeException(e);
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      try {
        tradingAlgoritm.makeCounterOrderIfFilledOrderAviable();
      } catch (Exception e) {
        logger.error("Counter order does not maked. Bot id=" + botId);
        throw new RuntimeException(e);
      }


    }
  }

  public int getBotId() {
    return botId;
  }

  public String getApiKey() {
    return apiKey;
  }
}
