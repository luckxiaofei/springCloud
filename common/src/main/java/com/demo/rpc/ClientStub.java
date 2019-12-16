package com.demo.rpc;


import org.springframework.beans.factory.FactoryBean;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientStub implements FactoryBean<Object> {

    private Class<?> mapperInterface;

    private String serviceName;

    /**
     * 地址可以考虑一个合适的方式去获取
     */
    private String address = "localhost";
    // 端口可以放到配置中心或者其他方式获取
    private int port = 9000;

    private Object proxy;

    // 构造的时候，实例化了一个代理对象
    public ClientStub(final Class<?> mapperInterface) {
        proxy = Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 代理对象的实现比较关键。实现的过程就是向服务端连接socket，发送数据接收数据
                        Socket socket = null;
                        // 服务端返回的流
                        ObjectInputStream retStream = null;
                        // 网服务端写的流
                        ObjectOutputStream outputStream = null;

                        try {
                            socket = new Socket();
                            socket.connect(new InetSocketAddress(address, port)); // 这里的ip和端口，写死。正常情况应该放在zookeeper上比较好

                            ServerSocket serverSocket = new ServerSocket(port);

                            System.out.println("start. . .");
                            Socket clientSocket = serverSocket.accept();

                            // 创建序列化对象，向服务端写数据，根据自己的需要定义，服务端按照格式取出来即可
                            outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.writeUTF(serviceName); // 远程服务的名称
                            outputStream.writeUTF(mapperInterface.getName()); // 调用的类的全路径
                            outputStream.writeUTF(method.getName()); // 调用的方法名
                            outputStream.writeObject(method.getParameterTypes()); // 返回值类型
                            outputStream.writeObject(args); // 请求参数

                            // 把客户端调用时序列化的数据反序列化出来
                            retStream = new ObjectInputStream(socket.getInputStream());
                            Object object = retStream.readObject(); // 读取远程返回的数据

                            return object;

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (retStream != null) {
                                    retStream.close();
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            try {
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            try {
                                if (socket != null) {
                                    socket.close();
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                        return null;
                    }
                });
    }

    // 实现了FactoryBean，这个方法返回实际的对象
    @Override
    public Object getObject() throws Exception {
        System.err.println("getObject");
        // 这里返回的是我们的代理对象
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<?> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
