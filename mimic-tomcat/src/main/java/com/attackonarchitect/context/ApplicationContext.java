package com.attackonarchitect.context;

import com.attackonarchitect.ComponentScanner;
import com.attackonarchitect.core.Wrapper;
import com.attackonarchitect.filter.chain.Chain;
import com.attackonarchitect.filter.chain.FilterChainImplFactory;
import com.attackonarchitect.handler.RouteMaxMatchStrategy;
import com.attackonarchitect.handler.RouteStrategy;
import com.attackonarchitect.http.HttpMTRequest;
import com.attackonarchitect.http.HttpMTResponse;
import com.attackonarchitect.listener.Notifier;
import com.attackonarchitect.listener.webcontext.ServletContextAttributeEvent;
import com.attackonarchitect.listener.webcontext.ServletContextEvent;
import com.attackonarchitect.logger.Logger;
import com.attackonarchitect.servlet.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 */
public class ApplicationContext extends ContainerBase implements ServletRegisterContext, ServletManager {
    //////--------
    private final ComponentScanner provider;

    private ApplicationContext(ComponentScanner provider) {
        this.provider = provider;
    }

//    private static ApplicationContext instance;

    public static ServletContext getInstance(ComponentScanner scanner, Notifier notifier, Logger logger) {
        ApplicationContext instance;
//        if(instance == null){
            instance = new ApplicationContext(scanner);
            instance.setNotifiler(notifier);
            instance.setAttribute("notifier",notifier);
            instance.setLogger(logger);
            instance.setName(scanner.getApplicationName());
            ServletContextEvent sce = new ServletContextEvent();
            sce.setSource(instance);
            sce.setName(scanner.getApplicationName());
            //触发通知，但是放在这里通知其实不合理
            //因为万一有其他的 ServletContext 实现
            //所以还是放在工厂里面更好感觉。
            notifier.notifyListeners(sce);
            instance.log("ServletContext created.");
            instance.init();
//        }
        return instance;
    }

    private void init() {
        this.servletMap.put("default", new StandardWrapper(new ServletInformation(new DefaultMimicServlet()), this));
        this.servletMap.put("error", new StandardWrapper(new ServletInformation(new ErrorMimicServlet()), this));

        //新增 loadOnStartup 初始化功能
        Collection<ServletInformation> values = provider.getServletInformationMap().values();

        // 依据loadOnStartup顺序进行初始化
        values.stream().filter(info -> info.getLoadOnStartup() > 0)
                .sorted(Comparator.comparingInt(ServletInformation::getLoadOnStartup))
                .forEachOrdered(ServletInformation::loadServlet);
    }

    private Map<String, Object> attributeDepot;

    @Override
    public <T> void setAttribute(String name, T obj) {
        this.getAttributeDepot().put(name, obj);
        ServletContextAttributeEvent event = new ServletContextAttributeEvent();
        event.setName(name);
        event.setValue(obj);

        getNotifiler().notifyListeners(event);
    }

    @Override
    public Object getAttribute(String name) {
        return this.getAttributeDepot().get(name);
    }


    private Notifier notifier;

    ///////getter setter

    private Map<String, Object> getAttributeDepot() {
        if (attributeDepot == null) {
            attributeDepot = new HashMap<>();
        }
        return attributeDepot;
    }

    public void setNotifiler(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public Notifier getNotifiler() {
        return this.notifier;
    }


    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public String getDocBase() {
        return this.docBase;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {

    }

    @Override
    public String getWrapperClass() {
        return "";
    }

    @Override
    public void setWrapperClass(String wrapperClass) {

    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    @Override
    public String findServletMapping(String pattern) {
        return "";
    }

    @Override
    public String[] findServletMappings() {
        return new String[0];
    }

    @Override
    public void reload() {

    }

    private String docBase;

    private String path;

    @Override
    public String getInfo() {
        return "Mimic Servlet Context, version 0.1";
    }

    @Override
    public void addChild(Container container) {
        super.addChild(container);

        if (container instanceof ServletInformation) {
            ServletInformation info = (ServletInformation) container;
            for (String urlPattern : info.getUrlPattern()) {
                this.servletMap.put(urlPattern, new StandardWrapper(info, this));
            }
        }
    }

    private final Map<String, StandardWrapper> servletMap = new ConcurrentHashMap<>();

    @Override
    public void invoke(HttpMTRequest request, HttpMTResponse response) throws IOException, ServletException {
        String uri = request.uri();
        final String path = this.getPath();
        if (uri.startsWith(path)) {
            uri = uri.substring(path.length());
        }
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

//        HttpMTResponse response = new HttpMTResponse(ctx);
        // filter责任链
        Chain filterChain = FilterChainImplFactory.createFilterChain(resolveServlet(uri), uri, provider);

//        response.setRequest(request);
        filterChain.start(request,response);
    }

    private synchronized StandardWrapper resolveServlet(final String uri) {
        StandardWrapper servletWrapper = servletMap.get(uri);
        if (Objects.isNull(servletWrapper)) {
            // 路由策略 -- 最大匹配
            RouteStrategy strategy = new RouteMaxMatchStrategy(this);
            ServletInformation servlet = strategy.route(uri);
            servletWrapper = new StandardWrapper(servlet, this);
            servletMap.put(uri, servletWrapper);
        }
        return servletWrapper;
    }

    @Override
    public ServletInformation getSpecifedServlet(String uri) {
        if (servletMap.containsKey(uri)) {
//            return servletMap.get(uri);
            StandardWrapper wrapper = servletMap.get(uri);
            return wrapper.getServlet();
        }
        Map<String, ServletInformation> servletInformationMap = provider.getServletInformationMap();
        ServletInformation servletInformation = servletInformationMap.get(uri);
//        Map<String, String> webServletComponents = provider.getWebServletComponents();
//        String clazzName = webServletComponents.get(uri);
        servletMap.put(uri, new StandardWrapper(servletInformation, this));
        return servletInformation;
    }

    @Override
    public Map<String, Servlet> getAllServletMapping(boolean init) {
        throw new UnsupportedOperationException("暂不支持获取所有Servlet映射");
    }

    @Override
    public Set<String> getAllRequestUri() {
        return this.provider.getServletInformationMap().keySet();
    }
}
