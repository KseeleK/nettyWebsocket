package test_websocket;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public interface TypeHandler {
    void typeHandler ( TextWebSocketFrame msg, Channel ctx, JSONObject jsonObject ) throws Exception;
    //void msgHandler(String )
}
