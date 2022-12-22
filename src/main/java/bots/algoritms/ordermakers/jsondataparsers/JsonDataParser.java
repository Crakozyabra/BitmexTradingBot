package bots.algoritms.ordermakers.jsondataparsers;

import bots.algoritms.ordermakers.orders.LimitOrder;

import java.util.Set;

public interface JsonDataParser {
    public double parseBitcoinPrice(String message);
    public Set<LimitOrder> parseAllLimitOrders(String message);
    public LimitOrder parseLimitOrder(String message);
}
