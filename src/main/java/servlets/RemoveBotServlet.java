package servlets;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "RemoveBotServlet", value = "/remove_bot")
public class RemoveBotServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader bufferedReader = req.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine());
        }
        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
        String botIdForRemove = jsonObject.getString("botIdForRemove");
        System.out.println("RemoveBotServlet. doPost. Bot id for remove: " + botIdForRemove);
        getServletContext().removeAttribute(botIdForRemove);
    }
}
