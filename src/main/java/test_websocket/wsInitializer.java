package test_websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class wsInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel ( SocketChannel ch ) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast( "httpServerCodec", new HttpServerCodec() );
        pipeline.addLast( "chunkedWriteHandler", new ChunkedWriteHandler() );
        pipeline.addLast( "httpObjectAggregator", new HttpObjectAggregator( 8192 ) );
        pipeline.addLast( new WebSocketServerProtocolHandler( "/" ) );

        pipeline.addLast( new textWebsocketHandler() );
    }
}
