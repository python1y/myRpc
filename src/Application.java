import java.lang.reflect.Method;
import java.net.URL;

public class Application {

    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL myRpc = classLoader.getResource("./proxy");
        System.out.println(Class.forName("proxy.RpcClientProxy"));
    }

}
