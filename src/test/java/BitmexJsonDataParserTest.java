import bots.algoritms.ordermakers.jsondataparsers.BitmexJsonDataParser;
import bots.algoritms.ordermakers.orders.LimitOrder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class BitmexJsonDataParserTest {
    private BitmexJsonDataParser bitmexJsonDataParser = new BitmexJsonDataParser();
    @Test
    public void parseBitcoinPrice() {
        String testMessage1 = "[{\"bidPrice\":100}]";
        String testMessage2 = "[{\"idPrice\":100}]";

        double result = bitmexJsonDataParser.parseBitcoinPrice(testMessage1);
        Executable executable = ()->{bitmexJsonDataParser.parseBitcoinPrice(testMessage2);};

        Assertions.assertEquals(100.0, result);
        Assertions.assertThrows(JSONException.class, executable);
    }

    @Test
    public void parseLimitOrder() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderID","1234567876");
        jsonObject.put("side", "Buy");
        jsonObject.put("orderQty", 100.0);
        jsonObject.put("price", 30000.0);
        jsonObject.put("ordStatus", "filled");

        LimitOrder limitOrder = bitmexJsonDataParser.parseLimitOrder(jsonObject.toString());

        Assertions.assertEquals(jsonObject.get("orderID"),limitOrder.getId());
        Assertions.assertEquals(jsonObject.get("side"), limitOrder.getSide());
        Assertions.assertEquals(jsonObject.get("orderQty"), limitOrder.getOrderQty());
        Assertions.assertEquals(jsonObject.get("price"), limitOrder.getPrice());
        Assertions.assertEquals(jsonObject.get("ordStatus"), limitOrder.getStatus());
    }


    @Test
    public void parseAllLimitOrders() {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("orderID","1234567876");
        jsonObject1.put("side", "Buy");
        jsonObject1.put("orderQty", 100.0);
        jsonObject1.put("price", 30000.0);
        jsonObject1.put("ordStatus", "filled");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("orderID","1234567876");
        jsonObject2.put("side", "Buy");
        jsonObject2.put("orderQty", 300.0);
        jsonObject2.put("price", 30000.0);
        jsonObject2.put("ordStatus", "new");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject1);
        jsonArray.put(jsonObject2);

        Set<LimitOrder> limitOrders = bitmexJsonDataParser.parseAllLimitOrders(jsonArray.toString());

        Assertions.assertEquals(2, limitOrders.size());
    }


}
