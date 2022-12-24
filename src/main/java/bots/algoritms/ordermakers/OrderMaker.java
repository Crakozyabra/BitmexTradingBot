package bots.algoritms.ordermakers;

import bots.algoritms.ordermakers.orders.LimitOrder;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public interface OrderMaker {
    public LimitOrder makeOrder(LimitOrder order);
    public Set<LimitOrder> getAllOrders();
    public double getCurrentBitcoinPrice();
    public boolean accountKeysIsValid();
}
