import bots.algoritms.ordermakers.BitmexOrderMaker;
import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BitmexOrderMakerTest{

    private BitmexOrderMaker bitmexOrderMaker;
    private HttpClient httpClient;
    private HttpResponse httpResponse;
    @BeforeEach
    public void init(){
        bitmexOrderMaker = new BitmexOrderMaker("9z27ZL8GIE_Z36YQn2wOEWOk", "u-uyAKmPIvoZ5FySeotto6389b0exVHF4UGzPY6jY7i_9t3A");
        httpClient = Mockito.mock(HttpClient.class);
        httpResponse = Mockito.mock(HttpResponse.class);
        bitmexOrderMaker.setClient(httpClient);
    }

    @Test
    public void makeOrderTest() throws IOException, InterruptedException {


       LimitOrder requestOrderData = new LimitOrder(Symbol.XBTUSD, OrderSide.BUY, 17000.0, 100.0);

       JSONObject resronseJSONOrderData = new JSONObject();
       resronseJSONOrderData.put("orderID", "793fb683-0016-484f-af69-7cb6d95f8c19");
       resronseJSONOrderData.put("symbol","XBTUSD");
       resronseJSONOrderData.put("side","Buy");
       resronseJSONOrderData.put("orderQty",100);
       resronseJSONOrderData.put("price",17000);
       resronseJSONOrderData.put("ordType","Limit");
       resronseJSONOrderData.put("ordStatus","New");

       lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
       when(httpResponse.body()).thenReturn(resronseJSONOrderData.toString());
       LimitOrder responseOrderData = bitmexOrderMaker.makeOrder(requestOrderData);

       Assertions.assertEquals("https://testnet.bitmex.com/api/v1/order", bitmexOrderMaker.getUrl().toString());
       Assertions.assertEquals(64, bitmexOrderMaker.getSignature().length());
       Assertions.assertEquals(responseOrderData.getSymbol(), requestOrderData.getSymbol());
       Assertions.assertEquals(responseOrderData.getSide(), responseOrderData.getSide());
       Assertions.assertEquals(responseOrderData.getOrdType(), requestOrderData.getOrdType());
       Assertions.assertEquals(responseOrderData.getPrice(), requestOrderData.getPrice());
       Assertions.assertEquals(responseOrderData.getOrderQty(), requestOrderData.getOrderQty());

       lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

       Assertions.assertThrows(RuntimeException.class, ()->{bitmexOrderMaker.makeOrder(requestOrderData);});
    }

    @Test
    public void getAllOrders() throws IOException, InterruptedException {
        // GIVEN
        JSONObject resronseJSONOrderData1 = new JSONObject();
        resronseJSONOrderData1.put("orderID", "793fb683-0016-484f-af69-7cb6d95f8c19");
        resronseJSONOrderData1.put("symbol","XBTUSD");
        resronseJSONOrderData1.put("side","Buy");
        resronseJSONOrderData1.put("orderQty",100);
        resronseJSONOrderData1.put("price",17000);
        resronseJSONOrderData1.put("ordType","Limit");
        resronseJSONOrderData1.put("ordStatus","New");

        JSONObject resronseJSONOrderData2 = new JSONObject();
        resronseJSONOrderData2.put("orderID", "793fb683-0016-484f-af69-7cb6d95f8c19");
        resronseJSONOrderData2.put("symbol","XBTUSD");
        resronseJSONOrderData2.put("side","Buy");
        resronseJSONOrderData2.put("orderQty",100);
        resronseJSONOrderData2.put("price",17000);
        resronseJSONOrderData2.put("ordType","Limit");
        resronseJSONOrderData2.put("ordStatus","Filled");

        JSONArray responseJSONArrayData = new JSONArray();
        responseJSONArrayData.put(resronseJSONOrderData1);
        responseJSONArrayData.put(resronseJSONOrderData2);

        // WHEN
        lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(responseJSONArrayData.toString());
        Set<LimitOrder> limitOrderSet = bitmexOrderMaker.getAllOrders();

        // THEN
        Assertions.assertTrue(bitmexOrderMaker.getUrl().toString().contains("https://testnet.bitmex.com/api/v1/order?symbol=XBTUSD&count=100&reverse=false&endTime="));
        Assertions.assertEquals(64, bitmexOrderMaker.getSignature().length());
        Assertions.assertEquals(2, limitOrderSet.size());

        // AND WHEN
        lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

        // THEN
        Assertions.assertThrows(RuntimeException.class, ()->{bitmexOrderMaker.getAllOrders();});
    }

    @Test
    public void getCurrentBitcoinPriceTest() throws IOException, InterruptedException {
        // GIVEN
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bidPrice", 17000);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        // WHEN
        lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(jsonArray.toString());
        double currentBitcoinPrice = bitmexOrderMaker.getCurrentBitcoinPrice();

        // THEN
        Assertions.assertTrue(bitmexOrderMaker.getUrl().toString().contains("https://testnet.bitmex.com/api/v1/quote?symbol=XBTUSD&count=100&reverse=false&endTime="));
        Assertions.assertEquals(64, bitmexOrderMaker.getSignature().length());
        Assertions.assertEquals(17000, currentBitcoinPrice);

        // AND WHEN
        lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

        // THEN
        Assertions.assertThrows(RuntimeException.class, ()->{bitmexOrderMaker.getCurrentBitcoinPrice();});
    }


    @Test
    public void accountKeysIsValidTest() throws IOException, InterruptedException {
        // GIVEN
        JSONObject message = new JSONObject();
        message.put("message", "Invalid API Key.");
        JSONObject error = new JSONObject();
        error.put("error", message);

        // WHEN
        lenient().when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn(error.toString());
        boolean keysIsValid = bitmexOrderMaker.accountKeysIsValid();

        // THEN
        Assertions.assertFalse(keysIsValid);
        Assertions.assertTrue(bitmexOrderMaker.getUrl().toString().contains("https://testnet.bitmex.com/api/v1/order?symbol=XBTUSD&count=100&reverse=false&endTime="));
        Assertions.assertEquals(64, bitmexOrderMaker.getSignature().length());

        // GIVEN
        JSONObject noError = new JSONObject();
        noError.put("noError", "some message");

        // WHEN
        when(httpResponse.body()).thenReturn(noError.toString());
        keysIsValid = bitmexOrderMaker.accountKeysIsValid();

        // THEN
        Assertions.assertTrue(keysIsValid);

    }



}
