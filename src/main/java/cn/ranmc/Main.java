package cn.ranmc;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class Main {
    @Getter
    private static String key = "";
    private static WebSocketClient client;

    private static void check(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.has("method") && obj.getString("method").equals("newmsg")) {
                JSONObject data = obj.getJSONObject("data");
                String msg = data.getString("msg");
                if(msg.contains("查服 ")) {
                    String wxid = data.getString("fromid");
                    msg = msg.replace("查服 ", "");
                    String ip = msg;
                    int port = 25565;
                    if (msg.contains(":")) {
                        port = Integer.parseInt(ip.split(":")[1]);
                        ip = ip.split(":")[0];
                    }
                    JSONObject ping = new JSONObject(new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(ip).setPort(port)));
                    String text = "查询结果\n地址:" + msg +
                            "\n人数:" + ping.getJSONObject("players").getInt("online") + "/" + ping.getJSONObject("players").getInt("max") +
                            "\n版本:" + ping.getJSONObject("version").getString("name");
                    System.out.println("来自 " + data.getString("nickName") + "的查询");
                    System.out.println(text);
                    JSONObject send = new JSONObject();
                    send.put("method", "sendText");
                    send.put("wxid", wxid);
                    send.put("msg", text);
                    send.put("atid", "");
                    send.put("pid", "0");
                    client.send(send.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.out.println("查询我的世界服务器 By阿然");
        System.out.println("作者QQ 2263055528");

        HttpGet get = new HttpGet("http://127.0.0.1:8203/ext/www/key.ini");
        try {
            HttpResponse response = HttpClients.createDefault().execute(get);
            HttpEntity entity = response.getEntity();
            JSONObject obj = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
            key = obj.getString("key");
            System.out.println("获取成功key");

            client = new WebSocketClient(new URI("ws://127.0.0.1:8202/wx?name=www&key=" + key),
                    new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("连接成功");
                }

                @SneakyThrows
                @Override
                public void onMessage(String s) {
                    check(s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("连接关闭");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("连接错误");
                }
            };
            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("启动失败");
        }
        /*
        HttpServer httpserver;
        int port = 8086;
        System.out.println("请输入key用于回复信息");
        Scanner scanner = new Scanner(System.in);
        key = scanner.next();*/
/*
        try {
            HttpServerProvider provider = HttpServerProvider.provider();
            httpserver = provider.createHttpServer(new InetSocketAddress(port), 100);

            httpserver.createContext("/check",new CheckHttpHandler(key));
            httpserver.setExecutor(null);
            httpserver.start();

            System.out.println("已运行在端口:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }
}
