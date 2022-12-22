package listeners;

import bots.BitmexTradingBot;

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

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        ServletContextAttributeListener.super.attributeAdded(event);
        System.out.println("SimpleServletContextListener. attributeAdded. Bot id has been added to servlet context: " + event.getName());
        if (event.getValue() instanceof BitmexTradingBot bitmexTradingBot) {
            System.out.println("SimpleServletContextListener. attributeAdded. ");
            Future<?> futureForBotStop = executorService.submit(bitmexTradingBot);
            botIdFutureMap.put(event.getName(), futureForBotStop);
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        ServletContextAttributeListener.super.attributeRemoved(event);
        String botIdForRemove = event.getName();
        System.out.println("SimpleServletContextListener. attributeRemoved. Bot id has been removed from servlet context: " + botIdForRemove);
        boolean botWasStop = botIdFutureMap.get(botIdForRemove).cancel(true);
        System.out.println("Bot with id=" + botIdForRemove + " was stop: " + botWasStop);
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        ServletContextAttributeListener.super.attributeReplaced(event);
        System.out.println("SimpleServletContextListener. attributeReplaced. Event name: " + event.getName());
    }
}
