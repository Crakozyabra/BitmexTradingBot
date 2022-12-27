import bots.algoritms.BitmexTradingAlgoritmDownSellUpBuy;
import bots.algoritms.ordermakers.OrderMaker;
import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BitmexTradingAlgoritmDownSellUpBuyTest {

    private OrderMaker orderMaker;
    private BitmexTradingAlgoritmDownSellUpBuy bitmexTradingAlgoritmDownSellUpBuy;
    @BeforeEach
    public void init() {
        // GIVEN
        orderMaker = Mockito.mock(OrderMaker.class);
        bitmexTradingAlgoritmDownSellUpBuy = new BitmexTradingAlgoritmDownSellUpBuy(3, 100.0, 200.0, orderMaker);
    }

    @Test
    public void makeStartOrdersTest() {

        // WHEN
        lenient().when(orderMaker.getCurrentBitcoinPrice()).thenReturn(17000.0);
        lenient().when(orderMaker.makeOrder(Mockito.any(LimitOrder.class))).thenAnswer(i -> {
            LimitOrder limitOrder = (LimitOrder) i.getArguments()[0];
            limitOrder.setId(ThreadLocalRandom.current().nextInt(0,Integer.MAX_VALUE) + "");
            return limitOrder;
        });
        bitmexTradingAlgoritmDownSellUpBuy.makeStartOrders();

        // THEN
        Assertions.assertEquals(3, bitmexTradingAlgoritmDownSellUpBuy.getBuyOrderIdPriceMap().size());
    }

}
