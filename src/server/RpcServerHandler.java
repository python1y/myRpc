package server;

import message.RpcRequest;
import message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class RpcServerHandler implements Runnable {

    private Socket clientSocket;
    private  Map<String,Object> services;

    public RpcServerHandler(Socket client, Map<String, Object> services) {
        this.clientSocket = client;
        this.services = services;
    }

    @Override
    public void run() {
        ObjectInputStream oin = null;
        ObjectOutputStream oout = null;
        RpcResponse response = new RpcResponse();

        try {
            // 1. 获取流以待操作
            oin = new ObjectInputStream(clientSocket.getInputStream());
            oout = new ObjectOutputStream(clientSocket.getOutputStream());

            // 2. 从网络IO输入流中请求数据，强转参数类型
            Object requestObj = oin.readObject();
            RpcRequest request = null;
            //3.处理请求
            if(!(requestObj instanceof RpcRequest)){
                response.setError(new Exception("请求参数错误"));
                oout.writeObject(response);
                oout.flush();
                return;
            }else{
                request = (RpcRequest) requestObj;
            }

            //4.查找并执行服务方法
            Object service = services.get(request.getClassName());
            Class<?> serviceClass = service.getClass();
            Method method = serviceClass.getMethod(request.getMethodName(), request.getParamTypes());
            Object res = method.invoke(service, request.getParams());

            response.setResult(res);
            oout.writeObject(response);
            oout.flush();

        } catch (Exception e) {
            try {	//异常处理
                if(oout != null){
                    response.setError(e);
                    oout.writeObject(response);
                    oout.flush();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        } finally {
            try {
                oin.close();
                oout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
