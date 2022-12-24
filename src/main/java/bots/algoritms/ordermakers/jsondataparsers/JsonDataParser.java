package bots.algoritms.ordermakers.jsondataparsers;

import bots.algoritms.ordermakers.orders.LimitOrder;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public interface JsonDataParser {
    public double parseBitcoinPrice(String message);
    public Set<LimitOrder> parseAllLimitOrders(String message);
    public LimitOrder parseLimitOrder(String message);
}
