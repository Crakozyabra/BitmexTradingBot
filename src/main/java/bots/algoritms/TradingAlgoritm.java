package bots.algoritms;

public interface TradingAlgoritm {

    public void makeStartOrders();
    public void updateFilledOrderIdsSet();
    public void makeCounterOrderIfFilledOrderAviable();
}
