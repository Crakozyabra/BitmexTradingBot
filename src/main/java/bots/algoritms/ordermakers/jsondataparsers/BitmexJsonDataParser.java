package bots.algoritms.ordermakers.jsondataparsers;

import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class BitmexJsonDataParser implements JsonDataParser {
   public double parseBitcoinPrice(String message){ // current bitcoin average price between bid and ask price
       JSONArray jsonArray = new JSONArray(message);
       JSONObject lastBitcoinPriceTimestampObject = jsonArray.getJSONObject(0);
       double bidPrice = Double.parseDouble(lastBitcoinPriceTimestampObject.get("bidPrice").toString());
       // double askPrice = Double.parseDouble(lastBitcoinPriceTimestampObject.get("askPrice").toString());
       // return (bidPrice + askPrice) / 2;
       return bidPrice;
   }

   public Set<LimitOrder> parseAllLimitOrders(String message) { // current existing bots.algoritms.ordermakers.orders on bitmex for concrete apiKey and apiSecret
       JSONArray limitOrdersArray = new JSONArray(message);
       Set<LimitOrder> limitOrders = new HashSet<>();
       for (int i = 0; i < limitOrdersArray.length(); i++) {
           try {
               String limitOrder = limitOrdersArray.get(i).toString();
               LimitOrder limitOrderPOJO = parseLimitOrder(limitOrder);
               limitOrders.add(limitOrderPOJO);
           } catch (Exception e) {}
       }
       return limitOrders;
   }

   public LimitOrder parseLimitOrder(String message) {
       JSONObject limitOrder = new JSONObject(message);
       String orderID = limitOrder.get("orderID").toString();
       OrderSide side = limitOrder.get("side").toString().equals("Sell") ? OrderSide.SELL : OrderSide.BUY;
       double orderQty = limitOrder.getDouble("orderQty");
       double price = limitOrder.getDouble("price");
       String status = limitOrder.get("ordStatus").toString();
       LimitOrder limitOrderPOJO = new LimitOrder(Symbol.XBTUSD, side, price, orderQty);
       limitOrderPOJO.setId(orderID);
       limitOrderPOJO.setStatus(status);
       return limitOrderPOJO;
   }

}
