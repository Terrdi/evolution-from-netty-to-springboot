package com.attackonarchitect.context;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.listener.webcontext.ServletContextEvent;
import com.attackonarchitect.listener.webcontext.ServletContextListener;
import com.attackonarchitect.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 */
public class ServletContextFactory {
    private ServletContextFactory() {
    }

//    private static ServletContext cache;

    public static ServletContext getInstance(ComponentScanner scanner, Notifier notifier, Logger logger) {
//        if (cache == null) {
//            cache = ApplicationContext.getInstance(scanner, notifier, logger);
//            ((ApplicationContext) cache).setNotifiler(notifier);
//            ServletContextEvent sce = new ServletContextEvent();
//            sce.setSource(cache);
//            sce.setName("ccccccccc");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    notifyServletContextListener(webListeners, sce);
//                }
//            }).start();
//        }

//        return cache;
        return ApplicationContext.getInstance(scanner, notifier, logger);
    }
}

