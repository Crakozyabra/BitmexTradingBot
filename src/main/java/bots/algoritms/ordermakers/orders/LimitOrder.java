package bots.algoritms.ordermakers.orders;


import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.OrderType;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;

import java.util.Objects;

public class LimitOrder implements Order {
    private String id;
    private String status;
    private Symbol symbol;
    private OrderSide side;
    private OrderType ordType = OrderType.LIMIT;
    private Double price;
    private Double orderQty;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSide() {
        return side.toString();
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public String getOrdType() {
        return ordType.toString();
    }

    public void setOrdType(OrderType ordType) {
        this.ordType = ordType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Double orderQty) {
        this.orderQty = orderQty;
    }

    public LimitOrder(Symbol symbol, OrderSide side, Double price, Double orderQty) {
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.orderQty = orderQty;
    }

    public String getSymbol() {
        return symbol.toString();
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitOrder that = (LimitOrder) o;
        return Objects.equals(id, that.id) && Objects.equals(status, that.status) && symbol == that.symbol && side == that.side && ordType == that.ordType && Objects.equals(price, that.price) && Objects.equals(orderQty, that.orderQty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, symbol, side, ordType, price, orderQty);
    }

    @Override
    public String toString() {
        return "LimitOrder{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", symbol=" + symbol +
                ", side=" + side +
                ", ordType=" + ordType +
                ", price=" + price +
                ", orderQty=" + orderQty +
                '}';
    }
}
