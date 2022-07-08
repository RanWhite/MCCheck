package cn.ranmc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class CheckHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        InputStreamReader ios = new InputStreamReader(exchange.getRequestBody(),"UTF-8");
        char[] bytes = new char[1024];
        int len = 0;
        String result = "";
        while ((len = ios.read(bytes)) != -1) {
            result += new String(bytes,0,len);
        }
        JSONObject obj = new JSONObject(result);

        if (obj.has("method") && obj.getString("method").equals("newmsg")) {
            JSONObject data = obj.getJSONObject("data");
            String msg = data.getString("msg");
            if(msg.contains("查服 ")) {
                msg = msg.replace("查服 ", "");
                String ip = msg;
                int port = 25565;
                if (msg.contains(":")) {
                    port = Integer.parseInt(ip.split(":")[1]);
                    ip = ip.split(":")[0];
                }
                long time = System.currentTimeMillis();
                JSONObject ping = new JSONObject(new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(ip).setPort(port).setTimeout(5000)));
                System.out.println("查询结果\n地址: " + msg +
                        "\n人数: " + ping.getJSONObject("players").getInt("online") + "/" + ping.getJSONObject("players").getInt("max") +
                        "\n版本: " + ping.getJSONObject("version").getString("name") +
                        "\n延迟: " + (System.currentTimeMillis() - time) + "ms");
            }
            os.write("ok".getBytes("UTF-8"));
        } else {
            os.write("error".getBytes("UTF-8"));
        }
        os.close();
    }

}