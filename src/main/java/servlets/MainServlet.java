package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import bots.algoritms.BitmexTradingAlgoritmDownSellUpBuy;
import bots.algoritms.TradingAlgoritm;
import bots.BitmexTradingBot;
import bots.TradingBot;
import bots.algoritms.ordermakers.BitmexOrderMaker;
import bots.algoritms.ordermakers.OrderMaker;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet(name = "CompareDataServlet", value = "")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getServletContext().getRequestDispatcher("/view/post_data.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader bufferedReader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine());
        }
        System.out.println(stringBuilder.toString());


        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        String apiKey = jsonObject.getString("apiKey");
        String apiSecret = jsonObject.getString("apiSecret");
        double moneyQuanityInOrder = 100.0;
        double stepMoneyBetweenOrders = 200.0;
        int ordersQuanity = 3;
        try {
            moneyQuanityInOrder = jsonObject.getDouble("orderPrice");
            stepMoneyBetweenOrders = jsonObject.getDouble("stepBetweenOrders");
            ordersQuanity = jsonObject.getInt("ordersQuanity");
        } catch (JSONException e) {}

        OrderMaker orderMaker = new BitmexOrderMaker(apiKey, apiSecret);
        TradingAlgoritm tradingAlgoritm = new BitmexTradingAlgoritmDownSellUpBuy(ordersQuanity, moneyQuanityInOrder, stepMoneyBetweenOrders, orderMaker);
        TradingBot tradingBot = new BitmexTradingBot(apiKey, tradingAlgoritm);
        System.out.println("MainServlet. doPost. Bot id for add to servlet context: " + tradingBot.getBotId());
        getServletContext().setAttribute(String.valueOf(tradingBot.getBotId()), tradingBot);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print("{\"botId\":\"" + tradingBot.getBotId() + "\"}");
        out.flush();
    }

}
