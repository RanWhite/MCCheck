package cn.ranmc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CheckHttpHandler implements HttpHandler {

    private String key = "";

    public CheckHttpHandler(String key) {
        this.key = key;
    }

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
                System.out.println("来自 " + data.getString("fromid") + "的查询");
                System.out.println(text);
                HttpPost post = new HttpPost("http://127.0.0.1:8203/api?json&key=" + key);
                try {
                    List<BasicNameValuePair> list = new ArrayList<>();
                    list.add(new BasicNameValuePair("method", "sendText"));
                    list.add(new BasicNameValuePair("wxid", wxid));
                    list.add(new BasicNameValuePair("msg", text));
                    list.add(new BasicNameValuePair("atid", ""));
                    list.add(new BasicNameValuePair("pid", "0"));
                    post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
                    HttpResponse response = HttpClients.createDefault().execute(post);
                    HttpEntity entity = response.getEntity();
                    System.out.println(EntityUtils.toString(entity, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            os.write("ok".getBytes("UTF-8"));
        } else {
            os.write("error".getBytes("UTF-8"));
        }
        os.close();
    }

}