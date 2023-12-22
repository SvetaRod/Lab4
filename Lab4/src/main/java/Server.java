import io.undertow.Undertow;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

import java.util.TimeZone;

public class Server {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        Controller.buildSessionFactory();
        Controller.init();

        startWebServer();
        Controller.startThread();
    }

    private static void startWebServer() {
        int port = 8080;
        Undertow.Builder builder = Undertow.builder().addHttpListener(port, "localhost");
        UndertowJaxrsServer server = new UndertowJaxrsServer().start(builder);
        server.deploy(WebAppSingletons.class);
        System.out.println("Сервер запущен: http://localhost:" + port + "/");
    }
}
