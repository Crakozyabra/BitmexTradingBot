package bots.algoritms;

import bots.algoritms.ordermakers.OrderMaker;
import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import bots.exceptions.NoSuchBuySideOrderIdException;
import bots.exceptions.NoSuchSellSideOrderIdIdException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BitmexTradingAlgoritmDownSellUpBuy implements TradingAlgoritm{
    private int botId;
    private boolean isTrade;
    private Map<String, Double> buyOrderIdPriceMap = new HashMap<>();
    private Map<String, Double> sellOrderIdPriceMap = new HashMap<>();
    private double cursorBitcoinPrice; // current cursor bitcoin price
    private double stepMoneyBetweenOrders = 200.0; // money step between bots.algoritms.ordermakers.orders only for buy or only for sell
    private int ordersQuanity = 3; // commons order quanity (for buy and for sell)
    private double moneyQuanityInOrder = 100.0; // money in USD for sell or for buy in order
    private Set<String> filledOrderIdSet = new HashSet<>();
    private OrderMaker orderMaker;

    private static Logger logger = LogManager.getLogger();

    public BitmexTradingAlgoritmDownSellUpBuy(int ordersQuanity, double moneyQuanityInOrder, double stepMoneyBetweenOrders, OrderMaker orderMaker) {
        this.stepMoneyBetweenOrders = stepMoneyBetweenOrders;
        this.ordersQuanity = ordersQuanity;
        this.moneyQuanityInOrder = moneyQuanityInOrder;
        this.orderMaker = orderMaker;
    }
    public void makeStartOrders (){
        logger.trace("start");
        double bitcoinInitialPrice = orderMaker.getCurrentBitcoinPrice();
        cursorBitcoinPrice = bitcoinInitialPrice;
        while (allOrdersQuanity() < ordersQuanity && buyOrderIdPriceMap.size() < ordersQuanity) {
            logger.debug("buyOrderIdPriceMap size="+buyOrderIdPriceMap.size()+"; sellOrderIdPriceMap size="+sellOrderIdPriceMap);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.debug("bitcoinInitialPrice=" + bitcoinInitialPrice);
            cursorBitcoinPrice = cursorBitcoinPrice -stepMoneyBetweenOrders;
            LimitOrder planningLimitOrder = new LimitOrder(Symbol.XBTUSD, OrderSide.BUY, cursorBitcoinPrice, moneyQuanityInOrder);
            LimitOrder makingLimitOrder = orderMaker.makeOrder(planningLimitOrder);
            buyOrderIdPriceMap.put(makingLimitOrder.getId(), cursorBitcoinPrice);
            buyOrderIdPriceMap.forEach((key, value) -> logger.info("Limit order created: id="+ key + "; bitcoin price="+ value));
        }
    }
    private void makeCounterOrder(String oldOrderId, OrderSide orderSide) {
        logger.trace("start");
        String newOrderId = "";
        if (orderSide == OrderSide.BUY) {
            if (!buyOrderIdPriceMap.containsKey(oldOrderId)) {
                throw new NoSuchBuySideOrderIdException();
            }
            cursorBitcoinPrice = buyOrderIdPriceMap.get(oldOrderId) + stepMoneyBetweenOrders;
            LimitOrder planningLimitOrder = new LimitOrder(Symbol.XBTUSD, OrderSide.SELL, cursorBitcoinPrice, moneyQuanityInOrder);
            LimitOrder makingLimitOrder = orderMaker.makeOrder(planningLimitOrder);
            sellOrderIdPriceMap.put(makingLimitOrder.getId(), cursorBitcoinPrice);
            buyOrderIdPriceMap.remove(oldOrderId);
        }

        if (orderSide == OrderSide.SELL) {
            if (!sellOrderIdPriceMap.containsKey(oldOrderId)) {
                throw new NoSuchSellSideOrderIdIdException();
            }
            cursorBitcoinPrice = sellOrderIdPriceMap.get(oldOrderId) - stepMoneyBetweenOrders;
            LimitOrder planningLimitOrder = new LimitOrder(Symbol.XBTUSD, OrderSide.BUY, cursorBitcoinPrice, moneyQuanityInOrder);
            LimitOrder makingLimitOrder = orderMaker.makeOrder(planningLimitOrder);
            buyOrderIdPriceMap.put(makingLimitOrder.getId(), cursorBitcoinPrice);
            sellOrderIdPriceMap.remove(oldOrderId);
        }
    }

    private int allOrdersQuanity(){
        return buyOrderIdPriceMap.size() + sellOrderIdPriceMap.size();
    }


    @Override
    public void makeCounterOrderIfFilledOrderAviable() {
        logger.trace("start");
        filledOrderIdSet.forEach(filledOrderId -> {
            if(buyOrderIdPriceMap.containsKey(filledOrderId)) {
                logger.trace("buyOrderIdPriceMap branch");
                makeCounterOrder(filledOrderId, OrderSide.SELL);
                buyOrderIdPriceMap.remove(filledOrderId);
            }

            if (sellOrderIdPriceMap.containsKey(filledOrderId)) {
                logger.trace("sellOrderIdPriceMap branch");
                makeCounterOrder(filledOrderId, OrderSide.BUY);
                sellOrderIdPriceMap.remove(filledOrderId);
            }
        });
    }


    @Override
    public void updateFilledOrderIdsSet() {
        logger.trace("start");
        filledOrderIdSet = orderMaker.getAllOrders().stream().
                filter(limitOrder -> limitOrder.getStatus().contains("filled")).
                map(LimitOrder::getId).
                collect(Collectors.toSet());
        logger.debug("filledOrderIdSet: " + String.join(",", filledOrderIdSet));
    }


    public double getStepMoneyBetweenOrders() {
        return stepMoneyBetweenOrders;
    }

    public void setStepMoneyBetweenOrders(double stepMoneyBetweenOrders) {
        this.stepMoneyBetweenOrders = stepMoneyBetweenOrders;
    }

    public int getOrdersQuanity() {
        return ordersQuanity;
    }

    public void setOrdersQuanity(int ordersQuanity) {
        this.ordersQuanity = ordersQuanity;
    }

    public double getMoneyQuanityInOrder() {
        return moneyQuanityInOrder;
    }

    public void setMoneyQuanityInOrder(double moneyQuanityInOrder) {
        this.moneyQuanityInOrder = moneyQuanityInOrder;
    }

    public OrderMaker getOrderMaker() {
        return orderMaker;
    }

    public void setOrderMaker(OrderMaker orderMaker) {
        this.orderMaker = orderMaker;
    }

    public int getBotId() {
        return botId;
    }

    public void setBotId(int botId) {
        this.botId = botId;
    }

    public boolean isTrade() {
        return isTrade;
    }

    public void setTrade(boolean trade) {
        isTrade = trade;
    }

    public Map<String, Double> getBuyOrderIdPriceMap() {
        return buyOrderIdPriceMap;
    }

    public void setBuyOrderIdPriceMap(Map<String, Double> buyOrderIdPriceMap) {
        this.buyOrderIdPriceMap = buyOrderIdPriceMap;
    }

    public Map<String, Double> getSellOrderIdPriceMap() {
        return sellOrderIdPriceMap;
    }

    public void setSellOrderIdPriceMap(Map<String, Double> sellOrderIdPriceMap) {
        this.sellOrderIdPriceMap = sellOrderIdPriceMap;
    }

    public double getCursorBitcoinPrice() {
        return cursorBitcoinPrice;
    }

    public void setCursorBitcoinPrice(double cursorBitcoinPrice) {
        this.cursorBitcoinPrice = cursorBitcoinPrice;
    }

    public Set<String> getFilledOrderIdSet() {
        return filledOrderIdSet;
    }

    public void setFilledOrderIdSet(Set<String> filledOrderIdSet) {
        this.filledOrderIdSet = filledOrderIdSet;
    }
}
