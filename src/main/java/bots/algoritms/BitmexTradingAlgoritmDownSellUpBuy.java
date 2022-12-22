package bots.algoritms;

import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.OrderMaker;
import bots.exceptions.NoSuchBuySideOrderIdException;
import bots.exceptions.NoSuchSellSideOrderIdIdException;

import java.util.HashMap;
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
    private Set<String> filledOrderIdSet; // filled order set
    private OrderMaker orderMaker;

    public BitmexTradingAlgoritmDownSellUpBuy(int ordersQuanity, double moneyQuanityInOrder, double stepMoneyBetweenOrders, OrderMaker orderMaker) {
        this.stepMoneyBetweenOrders = stepMoneyBetweenOrders;
        this.ordersQuanity = ordersQuanity;
        this.moneyQuanityInOrder = moneyQuanityInOrder;
        this.orderMaker = orderMaker;
    }
    public void makeStartOrders (){
        double bitcoinInitialPrice = orderMaker.getCurrentBitcoinPrice();
        while (allOrdersQuanity() < ordersQuanity && buyOrderIdPriceMap.size() < ordersQuanity) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("BitmexTradingAlgoritmDownSellUpBuy. makeStartOrders. BincoinInitialPrice=" + bitcoinInitialPrice);
            cursorBitcoinPrice = bitcoinInitialPrice - stepMoneyBetweenOrders;
            System.out.println("cursorBitcoinPrice: " + cursorBitcoinPrice);
            LimitOrder planningLimitOrder = new LimitOrder(Symbol.XBTUSD, OrderSide.BUY, cursorBitcoinPrice, moneyQuanityInOrder);
            System.out.println("planningLimitOrder: " + planningLimitOrder);
            LimitOrder makingLimitOrder = orderMaker.makeOrder(planningLimitOrder);
            System.out.println("makingLimitOrder: " + makingLimitOrder);
            buyOrderIdPriceMap.put(makingLimitOrder.getId(), cursorBitcoinPrice);
            buyOrderIdPriceMap.forEach((key, value) -> System.out.println(key + ": "+ value));
        }
    }
    private void makeCounterOrder(String oldOrderId, OrderSide orderSide) {
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
        filledOrderIdSet.forEach(filledOrderId -> {
            if(buyOrderIdPriceMap.containsKey(filledOrderId)) {
                makeCounterOrder(filledOrderId, OrderSide.SELL);
                buyOrderIdPriceMap.remove(filledOrderId);
            }
            if (sellOrderIdPriceMap.containsKey(filledOrderId)) {
                makeCounterOrder(filledOrderId, OrderSide.BUY);
                sellOrderIdPriceMap.remove(filledOrderId);}
        });
    }


    @Override
    public void updateFilledOrderIdsSet() {
        filledOrderIdSet = orderMaker.getAllOrders().stream().
                filter(limitOrder -> limitOrder.getStatus().contains("filled")).
                map(LimitOrder::getId).
                collect(Collectors.toSet());
    }

    @Override
    public boolean accountKeysIsValid() {
        return orderMaker.accountKeysIsValid();
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
}
