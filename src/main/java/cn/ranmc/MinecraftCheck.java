package cn.ranmc;

import org.json.JSONObject;

public class MinecraftCheck {

    public static String getResult(String result) {
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
                    System.out.println("来自" + data.getString("nickName") + "的查询");
                    System.out.println(text);
                    JSONObject send = new JSONObject();
                    send.put("method", "sendText");
                    send.put("wxid", wxid);
                    send.put("msg", text);
                    send.put("atid", "");
                    send.put("pid", "0");
                    return send.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }
}
