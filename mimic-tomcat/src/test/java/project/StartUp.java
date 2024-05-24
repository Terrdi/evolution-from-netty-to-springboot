package project;

import com.attackonarchitect.MimicTomcatServer;
import com.attackonarchitect.WebScanPackage;

/**
 * @description:
 */

@WebScanPackage
public class StartUp {
    public static void main(String[] args) {
        MimicTomcatServer server = new MimicTomcatServer(9999);
        // 注解启动
//        server.addConfig(StartUp.class, "/", "/tmp/mini-tomcat/webapp1");

        // xml启动
        server.addConfig("/WEB-INF/web.xml", "/xml", "/tmp/mini-tomcat/webapp2");
        server.addConfig("/tmp/mini-tomcat/webapp1/web.xml", "/", "/tmp/mini-tomcat/webapp1");

        // jar启动
//        new MimicTomcatServer(9999).start("target/mimic-tomcat-1.0-SNAPSHOT.jar");

        // SPI 启动
//        new MimicTomcatServer(9999).start(StartUp.class.getClassLoader());

        server.start();
    }
}
