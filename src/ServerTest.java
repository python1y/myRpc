import server.RpcServer;

import java.io.IOException;

public class ServerTest {

    public static void main(String[] args) throws IOException {
        RpcServer server = new RpcServer();
        server.start(9090,"");
    }

}
