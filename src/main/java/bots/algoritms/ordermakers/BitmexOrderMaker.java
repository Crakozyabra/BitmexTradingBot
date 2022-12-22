package bots.algoritms.ordermakers;

import bots.algoritms.ordermakers.URL.URL;
import bots.algoritms.ordermakers.URL.URLBuilder;
import bots.algoritms.ordermakers.URL.urlconstants.ResourceURL;
import bots.algoritms.ordermakers.URL.urlconstants.UtilURL;
import bots.algoritms.ordermakers.URL.urlconstants.Verb;
import bots.algoritms.ordermakers.jsondataparsers.BitmexJsonDataParser;
import bots.algoritms.ordermakers.jsondataparsers.JsonDataParser;
import bots.algoritms.ordermakers.orders.LimitOrder;
import bots.algoritms.ordermakers.orders.orderconstants.OrderSide;
import bots.algoritms.ordermakers.orders.orderconstants.Symbol;
import org.json.JSONException;
import org.json.JSONObject;
import websocketclient.Signature;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

public class BitmexOrderMaker implements OrderMaker {
    private final String apiKey;
    private final String apiSecret;
    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonDataParser jsonDataParser = new BitmexJsonDataParser();

    public BitmexOrderMaker(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    @Override
    public LimitOrder makeOrder(LimitOrder order) {
        URL url = new URLBuilder()
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.ORDER)
                .build();
        String orderJsonStr = new JSONObject(order).toString();
        String expires = createExpires();
        String signature = createSignature(url, Verb.POST, orderJsonStr, expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.POST, orderJsonStr, expires);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(orderJsonStr))
                .header("api-signature", signature)
                .header("api-expires", expires)
                .header("api-key", apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .uri(URI.create(url.toString()))
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("BitmexOrderMaker. makeOrder:" + response.body());
        return jsonDataParser.parseLimitOrder(response.body());
    }

    @Override
    public Set<LimitOrder> getAllOrders() {
        URL url = new URLBuilder()
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.ORDER)
                .queryData("?symbol=XBTUSD&count=100&reverse=false&endTime=" + LocalDateTime.now())
                .build();
        String expires = createExpires();
        String signature = createSignature(url, Verb.GET, "?symbol=XBTUSD&count=100&reverse=false&endTime=" + LocalDateTime.now(), expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.GET, "?symbol=XBTUSD&count=100&reverse=false&endTime=" + LocalDateTime.now(), expires);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("api-signature", signature)
                .header("api-expires", expires)
                .header("api-key", apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .uri(URI.create(url.toString()))
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return jsonDataParser.parseAllLimitOrders(response.body());
    }



    @Override
    public double getCurrentBitcoinPrice(){
        URL url = new URLBuilder()
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.QUOTE)
                .queryData("?symbol=XBTUSD&count=1&reverse=false&endTime=" + LocalDateTime.now())
                .build();

        String expires = createExpires();
        String signature = createSignature(url, Verb.GET, "?symbol=XBTUSD&count=1&reverse=false&endTime=" + LocalDateTime.now(), expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.GET, "?symbol=XBTUSD&count=1&reverse=false&endTime=" + LocalDateTime.now(), expires);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("api-signature", signature)
                .header("api-expires", expires)
                .header("api-key", apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .uri(URI.create(url.toString()))
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return jsonDataParser.parseBitcoinPrice(response.body());
    }

    private String createSignature(URL url, Verb verb, String data, String expires) {
        Signature signature = new Signature();
        switch (verb) {
            case POST -> {
                String path = url.getApiPath()+url.getResourcePath();
                return signature.signatureToString(signature.createSignature("POST", path, data, expires, apiSecret ));
            }
            case GET -> {
                String path = url.getApiPath() + url.getResourcePath() + url.getQueryData();
                return signature.signatureToString(signature.createSignature("GET", path, "", expires, apiSecret ));
            }
            default -> {
                return null;
            }
        }
    }

    private String createExpires() {
        return (Instant.now().getEpochSecond() + 100) + "";
    }

    public boolean accountKeysIsValid() {
        URL url = new URLBuilder()
                .protocol(UtilURL.PROTOCOL_HTTP)
                .net(UtilURL.TESTNET)
                .baseUrl(UtilURL.BASE_URL)
                .apiPath(UtilURL.API_PATH_HTTP)
                .recoursePath(ResourceURL.ORDER)
                .queryData("?symbol=XBTUSD&count=100&reverse=false&endTime=" + LocalDateTime.now())
                .build();
        String expires = createExpires();
        String signature = createSignature(url, Verb.GET, "?symbol=XBTUSD&count=100&reverse=false&endTime=" + LocalDateTime.now(), expires);

        while(signature.length() != 64) {
            expires = createExpires();
            signature = createSignature(url, Verb.GET, "?symbol=XBTUSD&count=100&reverse=false&endTime=" + LocalDateTime.now(), expires);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("api-signature", signature)
                .header("api-expires", expires)
                .header("api-key", apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json")
                .uri(URI.create(url.toString()))
                .build();

        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("get all orders query: " + response.body());
        try {
            JSONObject jsonObject = new JSONObject(response.body());
            String errorMessage = jsonObject.getJSONObject("error").getString("message");

            if (errorMessage.equals("Invalid API Key.") || errorMessage.equals("Signature not valid.")) {
                return false;
            }
        } catch (JSONException e) {}
        return true;
        // {"error":{"message":"Invalid API Key.","name":"HTTPError"}}
        // {"error":{"message":"Signature not valid.","name":"HTTPError"}}
    }

    public static void main(String[] args) {
        OrderMaker orderMaker = new BitmexOrderMaker("9z27ZL8GIE_Z36YQn2wOEWOk", "u-uyAKmPIvoZ5FySeotto6389b0exVHF4UGzPY6jY7i_9t3A");
        double currentBitcoinPrice = orderMaker.getCurrentBitcoinPrice();
        System.out.println(currentBitcoinPrice);
        System.out.println(orderMaker.getAllOrders());
        LimitOrder limitOrder = new LimitOrder(Symbol.XBTUSD, OrderSide.SELL, currentBitcoinPrice-200.0, 200.0);
        System.out.println(orderMaker.makeOrder(limitOrder));
    }
}
