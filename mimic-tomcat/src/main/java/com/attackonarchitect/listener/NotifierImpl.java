package com.attackonarchitect.listener;

import com.attackonarchitect.context.Container;
import com.attackonarchitect.context.ServletContext;
import com.attackonarchitect.listener.request.ServletRequestAttributeEvent;
import com.attackonarchitect.listener.request.ServletRequestAttributeListener;
import com.attackonarchitect.listener.session.SessionEvent;
import com.attackonarchitect.listener.session.SessionListener;
import com.attackonarchitect.listener.webcontext.ServletContextAttributeEvent;
import com.attackonarchitect.listener.webcontext.ServletContextAttributeListener;
import com.attackonarchitect.listener.webcontext.ServletContextEvent;
import com.attackonarchitect.listener.webcontext.ServletContextListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

/**
 * @description:
 */
public class NotifierImpl implements Notifier{

    //web context
    private final List<ServletContextListener> servletContextListenersList = new CopyOnWriteArrayList<>();
    private final List<ServletContextAttributeListener> servletContextAttributeListenerList = new CopyOnWriteArrayList<>();

    //request
    private final List<ServletRequestAttributeListener> servletRequestAttributeListenerList = new CopyOnWriteArrayList<>();

    //session
    private final List<SessionListener> sessionListenerList = new CopyOnWriteArrayList<>();

    private final List<ContainerListener> containerListenerList = new CopyOnWriteArrayList<>();

    private final ExecutorService service = Executors.newFixedThreadPool(1);


    public NotifierImpl(List<String> webListeners) {
    }

    public void init(List<String> webListeners, final Container parent) {
        webListeners.forEach(listenerClazzName->{
            try {
                Class<?> clazz = parent.getLoader().loadClass(listenerClazzName);
                this.addListener((EventListener) clazz.newInstance());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException | ClassCastException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Future<?> notifyListeners(Event event) {
        if (event instanceof ServletRequestAttributeEvent) {
            return notifyServletRequestAttributeListeners((ServletRequestAttributeEvent) event);
        } else if (event instanceof ServletContextAttributeEvent){
            return notifyServletContextAttributeListeners((ServletContextAttributeEvent) event);
        } else if (event instanceof ServletContextEvent){
            return notifyServletContextListeners((ServletContextEvent) event);
        } else if (event instanceof SessionEvent) {
            return notifySessionListeners((SessionEvent) event);
        } else if (event instanceof ContainerEvent) {
            return notifyContainerListeners((ContainerEvent) event);
        } else {
            System.err.println("不合法的事件类型: " + event.getClass().getName());
            return null;
        }
    }

    @Override
    public void addListener(EventListener eventListener) {
        if (eventListener instanceof ServletContextAttributeListener) {
            this.servletContextAttributeListenerList.add((ServletContextAttributeListener) eventListener);
        }
        if (eventListener instanceof ServletContextListener) {
            this.servletContextListenersList.add((ServletContextListener) eventListener);
        }
        if (eventListener instanceof ServletRequestAttributeListener) {
            this.servletRequestAttributeListenerList.add((ServletRequestAttributeListener) eventListener);
        }
        if (eventListener instanceof SessionListener) {
            this.sessionListenerList.add((SessionListener) eventListener);
        }
        if (eventListener instanceof ContainerListener) {
            this.containerListenerList.add((ContainerListener) eventListener);
        }
    }

    @Override
    public void removeListener(EventListener eventListener) {
        if (eventListener instanceof ServletContextAttributeListener) {
            this.servletContextAttributeListenerList.remove((ServletContextAttributeListener) eventListener);
        }
        if (eventListener instanceof ServletContextAttributeListener) {
            this.servletContextListenersList.remove((ServletContextListener) eventListener);
        }
        if (eventListener instanceof ServletRequestAttributeListener) {
            this.servletRequestAttributeListenerList.remove((ServletRequestAttributeListener) eventListener);
        }
        if (eventListener instanceof SessionListener) {
            this.sessionListenerList.remove((SessionListener) eventListener);
        }
        if (eventListener instanceof ContainerListener) {
            this.containerListenerList.remove((ContainerListener) eventListener);
        }
    }

    private static class EventRunner<T extends EventListener> implements Runnable {
        private final Object[] list;

        private final BiConsumer<T, Event> runnable;

        private final Event event;

        private EventRunner(List<T> list, BiConsumer<T, Event> runnable, Event event) {
            this.event = event;
            this.runnable = runnable;

            //noinspection unchecked
            this.list = list.toArray();
        }

        @Override
        public void run() {
            for (Object item : this.list) {
                runnable.accept((T) item, event);
            }
        }
    }

    private Future<?> notifyServletContextListeners(ServletContextEvent event) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getServletContextListenersList()
//                        .forEach(listener -> {
//                            listener.contextInitialized((ServletContextEvent) event);
//                        });
//            }
//        }).start();

        return service.submit(new EventRunner<>(getServletContextListenersList(),
                (listener, event0) -> {
                    listener.contextInitialized((ServletContextEvent) event0);
                }, event));
    }

    private Future<?> notifyServletContextAttributeListeners(ServletContextAttributeEvent event) {
        return service.submit(new EventRunner<>(getServletContextAttributeListenerList(),
                (listener, event0) -> {
                    listener.attributeAdded((ServletContextAttributeEvent) event0);
                }, event));
    }

    private Future<?> notifyServletRequestAttributeListeners(ServletRequestAttributeEvent event) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getServletRequestAttributeListenerList().forEach(listener->{
//                    listener.requestAttributeAdded((ServletRequestAttributeEvent) event);
//                });
//            }
//        }).start();

        return service.submit(new EventRunner<>(getServletRequestAttributeListenerList(),
                (listener, event0) -> {
                    listener.requestAttributeAdded((ServletRequestAttributeEvent) event0);
                }, event));
    }


    private Future<?> notifySessionListeners(final SessionEvent event) {
        return service.submit(new EventRunner<>(this.sessionListenerList, (listener, event0) -> {
            listener.sessionEvent((SessionEvent) event0);
        }, event));
    }

    private Future<?> notifyContainerListeners(final ContainerEvent event) {
        return service.submit(new EventRunner<>(this.containerListenerList, (listener, event0) -> {
            listener.containerEvent((ContainerEvent) event0);
        }, event));
    }


    ////getter,setter


    public List<ServletContextAttributeListener> getServletContextAttributeListenerList() {
        return servletContextAttributeListenerList;
    }

    public List<ServletContextListener> getServletContextListenersList() {
        return servletContextListenersList;
    }

    public List<ServletRequestAttributeListener> getServletRequestAttributeListenerList() {
        return servletRequestAttributeListenerList;
    }

    public void stop() {
        this.service.shutdown();
    }
}
