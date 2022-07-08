package cn.ranmc;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Main {
    @Getter
    private static String key = "";

    public static void main(String[] args) {
        HttpServer httpserver;
        int port = 8086;

        System.out.println("查询我的世界服务器 By阿然");
        System.out.println("作者QQ 2263055528");
        System.out.println("请输入key用于回复信息");
        Scanner scanner = new Scanner(System.in);
        key = scanner.next();
        try {
            HttpServerProvider provider = HttpServerProvider.provider();
            httpserver = provider.createHttpServer(new InetSocketAddress(port), 100);

            httpserver.createContext("/check",new CheckHttpHandler(key));
            httpserver.setExecutor(null);
            httpserver.start();

            System.out.println("已运行在端口:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
