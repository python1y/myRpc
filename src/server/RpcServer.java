package server;

import annotation.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcServer {

    public void start(int port, String clazz) throws IOException {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            Map<String, Object> services = getService(clazz);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

            while (true) {
                Socket client = server.accept();
                RpcServerHandler service = new RpcServerHandler(client, services);
                executor.execute(service);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }

    private Map<String, Object> getService(String clazz) {
        try {
            HashMap<String, Object> services = new HashMap<String, Object>();
            String[] clz = clazz.split(",");
            //获得所有Rpc服务，注册
            ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
            for (String cl : clz) {
                List<Class<?>> classList = getClasses(cl);
                classes.addAll(classList);
            }
            //获得所有接口实现类实例
            for (Class<?> aClass : classes) {
                Object instance = aClass.getDeclaredConstructor().newInstance();
                services.put(aClass.getAnnotation(Service.class).value().getName(), instance);
            }

            return services;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Class<?>> getClasses(String pkgName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();

        File directory = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if(classLoader == null){
                throw new ClassNotFoundException("无法获取到ClassLoader");
            }
            String path = pkgName.replace('.','/');
            URL resource = classLoader.getResource("./"+path);
            if(resource==null){
                throw new ClassNotFoundException("没有这样的资源"+path);
            }
            directory = new File(resource.getFile());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(directory.exists()){
            String[] files = directory.list();
            File[] fileList = directory.listFiles();
            for (int i = 0; fileList != null && i < fileList.length; i++) {
                File file = fileList[i];
                //判断是否是Class文件
                if (file.isFile() && file.getName().endsWith(".class")) {
                    if(pkgName.length()==0){

                    }else{
                        Class<?> clazz = Class.forName(pkgName.substring(1,pkgName.length())+"."+files[i].substring(0, files[i].length() - 6));
                        if(clazz.getAnnotation(Service.class) != null){
                            classes.add(clazz);
                        }
                    }
                }else if(file.isDirectory()){ //如果是目录，递归查找
                    List<Class<?>> result = getClasses(pkgName+"."+file.getName());
                    if(result != null && result.size() != 0){
                        classes.addAll(result);
                    }
                }
            }
        }else{
            throw new ClassNotFoundException(pkgName + "不是一个有效的包名");
        }
        return classes;
    }
}
