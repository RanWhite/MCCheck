package cn.ranmc;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        HttpServer httpserver;
        int port = 8086;

        System.out.println("查询我的世界服务器 By阿然");
        System.out.println("QQ 2263055528");
        try {
            HttpServerProvider provider = HttpServerProvider.provider();
            httpserver = provider.createHttpServer(new InetSocketAddress(port), 100);

            httpserver.createContext("/check",new CheckHttpHandler());
            httpserver.setExecutor(null);
            httpserver.start();

            System.out.println("已运行在端口:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
