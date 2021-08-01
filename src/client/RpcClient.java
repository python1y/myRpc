package client;

import message.RpcRequest;
import message.RpcResponse;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {

    public Object start(RpcRequest rpcRequest, String host, int port) throws IOException {
        Socket server = new Socket(host, port);

        ObjectInputStream oin = null;
        ObjectOutputStream oout = null;

        try {
            //写入请求
            oout = new ObjectOutputStream(server.getOutputStream());
            oout.writeObject(rpcRequest);
            oout.flush();

            //获得结果
            oin = new ObjectInputStream(server.getInputStream());
            Object res = oin.readObject();

            //拆包
            RpcResponse response;
            if (!(res instanceof RpcResponse)) {
                throw new InvalidClassException(String.format("返回参数不正确，应该为%s类型", RpcResponse.class));
            } else {
                response = (RpcResponse) res;
            }
            //返回结果
            if (response.getError() != null) {
                throw response.getError();
            }
            return response.getResult();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            oin.close();
            oout.close();
        }

        return null;
    }
}
