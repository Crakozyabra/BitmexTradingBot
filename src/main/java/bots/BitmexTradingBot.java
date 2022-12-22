package bots;

import bots.algoritms.TradingAlgoritm;

import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

public class BitmexTradingBot implements TradingBot, Runnable{

  private int botId;
  private String apiKey;
  private TradingAlgoritm tradingAlgoritm;

  public BitmexTradingBot(String apiKey, TradingAlgoritm tradingAlgoritm) {
    this.botId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    this.apiKey = apiKey;
    this.tradingAlgoritm = tradingAlgoritm;
  }

  @Override
  public void run() {
    if(!tradingAlgoritm.accountKeysIsValid()) {
      System.out.println("Account keys is not valid. Bot with id=" + botId + " was stopped");
      throw new RuntimeException();
    }


    tradingAlgoritm.makeStartOrders();
    while (!Thread.currentThread().isInterrupted()) {
      System.out.println("BitmexTradingBot. run. id=" + botId + " timestamp=" + LocalTime.now());
      try {
        tradingAlgoritm.updateFilledOrderIdsSet();
      } catch (Exception e) {
        System.out.println("BitmexTradingBot. run. tradingAlgoritm.updateFilledOrderIdsSet() exception");
      }

      try {
        tradingAlgoritm.makeCounterOrderIfFilledOrderAviable();
      } catch (Exception e) {
        System.out.println("BitmexTradingBot. run. tradingAlgoritm.makeCounterOrderIfFilledOrderAviable() exception");
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
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
