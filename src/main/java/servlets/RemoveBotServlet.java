package servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private Logger logger = null;
    @Override
    public void init() throws ServletException {
        super.init();
        logger = LogManager.getLogger();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = getJSONFromRequest(req);
        String botIdForRemove = jsonObject.getString("botIdForRemove");
        getServletContext().removeAttribute(botIdForRemove);
        logger.trace("Bot with id removed from servlet context: " + botIdForRemove);
    }

    private JSONObject getJSONFromRequest(HttpServletRequest request) throws IOException {
        BufferedReader bufferedReader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            stringBuilder.append(bufferedReader.readLine());
        }
        return new JSONObject(stringBuilder.toString());
    }


}
