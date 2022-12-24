package listeners;

import bots.BitmexTradingBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@WebListener
public class SimpleServletContextListener implements ServletContextAttributeListener{

   private ExecutorService executorService = Executors.newCachedThreadPool();
   private Map<String, Future<?>> botIdFutureMap = new HashMap<>();
   private final Logger logger = LogManager.getLogger();

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        ServletContextAttributeListener.super.attributeAdded(event);
        logger.trace("start");
        if (event.getValue() instanceof BitmexTradingBot bitmexTradingBot) {
            Future<?> futureForBotStop = executorService.submit(bitmexTradingBot);
            botIdFutureMap.put(event.getName(), futureForBotStop);
            logger.debug("Start thread with bot with id: " + event.getName());
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        ServletContextAttributeListener.super.attributeRemoved(event);
        String botIdForRemove = event.getName();
        boolean botWasStop = botIdFutureMap.get(botIdForRemove).cancel(true);
        logger.debug("Bot with id=" + botIdForRemove + " was stop: " + botWasStop);
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        ServletContextAttributeListener.super.attributeReplaced(event);
        logger.trace("start");
    }
}
