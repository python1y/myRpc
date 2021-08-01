package proxy;

import client.RpcClient;
import message.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /***
     * 获得接口代理实例
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("远程调用方法");

        //构造请求
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParamTypes(method.getParameterTypes());
        rpcRequest.setParams(args);

        //rpcClient发送请求
        RpcClient client = new RpcClient();
        Object res = client.start(rpcRequest, host, port);

        System.out.println("远程方法调用结束");
        return res;
    }
}
