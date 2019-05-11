package test_websocket.city;

import com.alibaba.fastjson.JSONObject;
import com.pusher.java_websocket.client.WebSocketClient;
import com.pusher.java_websocket.handshake.ServerHandshake;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.net.URI;
import java.net.URISyntaxException;


public class TsClient extends WebSocketClient {
    public MsgCallback callback;
    private String message;
    private Channel channel;

    public TsClient ( String url, Channel channel ) throws URISyntaxException {
        super( new URI( url ) );
        this.channel = channel;
    }

    @Override
    public void onOpen ( ServerHandshake handshakedata ) {
        System.out.println( "TS Client Open" );
    }

    @Override
    public void onMessage ( String message ) {
        channel.writeAndFlush( new TextWebSocketFrame( message ) );
    }

    @Override
    public void onClose ( int code, String reason, boolean remote ) {
        System.out.println( "TS Client Close" );
    }

    @Override
    public void onError ( Exception ex ) {
        System.out.println( ex );
    }

    public void checkState ( JSONObject target ) {
        this.send( target.toJSONString() );
    }

    public void requireData ( JSONObject target ) {
        this.send( target.toJSONString() );
    }

    public void clientClose () {
        this.close();
    }

    public boolean getState () {
        do {
        } while ( !this.message.equals( "true" ) && !this.message.equals( "false" ) );
        return Boolean.parseBoolean( this.message );
    }

    public JSONObject getData () {
        do {
        } while ( !JSONObject.isValid( this.message ) );
        return JSONObject.parseObject( this.message );
    }

}
