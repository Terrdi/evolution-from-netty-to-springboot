package com.attackonarchitect.listener;

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
        init(webListeners);
    }

    private void init(List<String> webListeners) {

        webListeners.forEach(listenerClazzName->{
            try {
                Class<?> clazz = Class.forName(listenerClazzName);
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
    public void notifyListeners(Event event) {
        if (event instanceof ServletRequestAttributeEvent) {
            notifyServletRequestAttributeListeners((ServletRequestAttributeEvent) event);
        } else if (event instanceof ServletContextAttributeEvent){
            notifyServletContextAttributeListeners((ServletContextAttributeEvent) event);
        } else if (event instanceof ServletContextEvent){
            notifyServletContextListeners((ServletContextEvent) event);
        } else if (event instanceof SessionEvent) {
            notifySessionListeners((SessionEvent) event);
        } else if (event instanceof ContainerEvent) {
            notifyContainerListeners((ContainerEvent) event);
        } else {
            System.err.println("不合法的事件类型: " + event.getClass().getName());
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

    private void notifyServletContextListeners(ServletContextEvent event) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getServletContextListenersList()
//                        .forEach(listener -> {
//                            listener.contextInitialized((ServletContextEvent) event);
//                        });
//            }
//        }).start();

        service.submit(new EventRunner<>(getServletContextListenersList(),
                (listener, event0) -> {
                    listener.contextInitialized((ServletContextEvent) event0);
                }, event));
    }

    private void notifyServletContextAttributeListeners(ServletContextAttributeEvent event) {
        service.submit(new EventRunner<>(getServletContextAttributeListenerList(),
                (listener, event0) -> {
                    listener.attributeAdded((ServletContextAttributeEvent) event0);
                }, event));
    }

    private void notifyServletRequestAttributeListeners(ServletRequestAttributeEvent event) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getServletRequestAttributeListenerList().forEach(listener->{
//                    listener.requestAttributeAdded((ServletRequestAttributeEvent) event);
//                });
//            }
//        }).start();

        service.submit(new EventRunner<>(getServletRequestAttributeListenerList(),
                (listener, event0) -> {
                    listener.requestAttributeAdded((ServletRequestAttributeEvent) event0);
                }, event));
    }


    private void notifySessionListeners(final SessionEvent event) {
        service.submit(new EventRunner<>(this.sessionListenerList, (listener, event0) -> {
            listener.sessionEvent((SessionEvent) event0);
        }, event));
    }

    private void notifyContainerListeners(final ContainerEvent event) {
        service.submit(new EventRunner<>(this.containerListenerList, (listener, event0) -> {
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

}
