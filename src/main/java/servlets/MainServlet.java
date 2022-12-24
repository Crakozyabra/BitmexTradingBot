package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpResponse;

import bots.algoritms.BitmexTradingAlgoritmDownSellUpBuy;
import bots.algoritms.TradingAlgoritm;
import bots.BitmexTradingBot;
import bots.TradingBot;
import bots.algoritms.ordermakers.BitmexOrderMaker;
import bots.algoritms.ordermakers.OrderMaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet(name = "CompareDataServlet", value = "")
public class MainServlet extends HttpServlet {
    private Logger logger = null;

    @Override
    public void init() throws ServletException {
        super.init();
        logger = LogManager.getLogger();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().getRequestDispatcher("/view/post_data.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        JSONObject jsonObject = getJSONFromRequest(request);

        String apiKey = jsonObject.getString("apiKey");
        String apiSecret = jsonObject.getString("apiSecret");
        double moneyQuanityInOrder = 100.0;
        double stepMoneyBetweenOrders = 200.0;
        int ordersQuanity = 3;
        try {
            moneyQuanityInOrder = jsonObject.getDouble("orderPrice");
        } catch (JSONException e) {
            logger.debug("Can not parse orderPrice from request. Default orderPrice=" + moneyQuanityInOrder + "\n" + e.getMessage());
        }

        try {
            stepMoneyBetweenOrders = jsonObject.getDouble("stepBetweenOrders");
        } catch (JSONException e) {
            logger.debug("Can not parse stepMoneyBetweenOrders from request. Default stepMoneyBetweenOrders=" + stepMoneyBetweenOrders + "\n" + e.getMessage());
        }

        try {
            ordersQuanity = jsonObject.getInt("ordersQuanity");
        } catch (JSONException e) {
            logger.debug("Can not parse orderQuanity from request. Default ordersQuanity=" + ordersQuanity + "\n" + e.getMessage());
        }



        TradingBot tradingBot = createNewBot(apiKey, apiSecret, ordersQuanity, moneyQuanityInOrder, stepMoneyBetweenOrders);
        if (tradingBot!=null) {
            getServletContext().setAttribute(String.valueOf(tradingBot.getBotId()), tradingBot);
            logger.trace("Created bot with id for : " + tradingBot.getBotId() + " and added to servlet context");
        }
        sendJSONResponseWithCreatedBotId(response, tradingBot);
    }

    private JSONObject getJSONFromRequest(HttpServletRequest request) throws IOException {
        BufferedReader bufferedReader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine());
        }
        logger.debug("request: " + stringBuilder.toString());
        return new JSONObject(stringBuilder.toString());
    }

    private TradingBot createNewBot(String apiKey, String apiSecret, int ordersQuanity, double moneyQuanityInOrder, double stepMoneyBetweenOrders) {
        OrderMaker orderMaker = new BitmexOrderMaker(apiKey, apiSecret);
        if (orderMaker.accountKeysIsValid()) {
            TradingAlgoritm tradingAlgoritm = new BitmexTradingAlgoritmDownSellUpBuy(ordersQuanity, moneyQuanityInOrder, stepMoneyBetweenOrders, orderMaker);
            return new BitmexTradingBot(apiKey, tradingAlgoritm);
        }
        return null;
    }

    private void sendJSONResponseWithCreatedBotId (HttpServletResponse response, TradingBot tradingBot) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonObject = new JSONObject();

        if (tradingBot == null) {
            jsonObject.put("botId", -1);
        } else {
            jsonObject.put("botId", tradingBot.getBotId());
        }

        out.print(jsonObject.toString());
        out.flush();
    }


}
