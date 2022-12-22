package bots.algoritms.ordermakers;

import bots.algoritms.ordermakers.orders.LimitOrder;

import java.util.Set;

public interface OrderMaker {
    public LimitOrder makeOrder(LimitOrder order);
    public Set<LimitOrder> getAllOrders();
    public double getCurrentBitcoinPrice();
    public boolean accountKeysIsValid();
}
