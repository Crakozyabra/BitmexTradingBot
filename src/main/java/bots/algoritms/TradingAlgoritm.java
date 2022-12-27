package bots.algoritms;

import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;

public interface TradingAlgoritm {

    public void makeStartOrders();
    public void updateFilledOrderIdsSet();
    public void makeCounterOrderIfFilledOrderAviable();

}
