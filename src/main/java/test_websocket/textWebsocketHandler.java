package test_websocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class textWebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ConcurrentHashMap<Integer, ChannelGroup> channelGroupHashMap;
    private static ConcurrentHashMap<Channel, Integer> channelRoomIdHashMap;
    private static ConcurrentLinkedQueue<Channel> channelOperation;
    private static Channel tsChannel = null;

    static {
        channelRoomIdHashMap = new ConcurrentHashMap<>();
        channelGroupHashMap = new ConcurrentHashMap<>();
        channelOperation = new ConcurrentLinkedQueue();
        channelGroupHashMap.put( 0, new DefaultChannelGroup( GlobalEventExecutor.INSTANCE ) );
    }

    @Override
    protected void channelRead0 ( ChannelHandlerContext ctx, TextWebSocketFrame msg ) throws Exception {
        Channel channel = ctx.channel();

        JSONObject jsonObject = JSONObject.parseObject( msg.text() );
        String type = jsonObject.getString( "type" );
        System.out.println( type );
        if ( type.equals( "Login" ) ) {
            JSONArray data = new JSONArray();
            for ( int roomId : channelGroupHashMap.keySet() ) {
                if ( roomId == 0 ) {
                    continue;
                }
                JSONObject dataContent = new JSONObject( true );
                dataContent.put( "room", roomId );
                JSONArray players = new JSONArray();
                channelGroupHashMap.get( roomId ).forEach( ch -> {
                    players.add( new JSONObject().put( "name", ch.remoteAddress() ) );
                } );
                dataContent.put( "players", players );
                data.add( dataContent );
            }
            channel.writeAndFlush( new TextWebSocketFrame( data.toJSONString() ) );

        } else if ( type.equals( "Enter room" ) ) {
            int roomId = JSONObject.parseObject( jsonObject.getString( "data" ) ).getInteger( "room" );
            System.out.println( "RoomID" + roomId );
            if ( roomId <= 0 ) {
//                JSONObject object = new JSONObject( true );
//                object.put( "data", new JSONObject().put( "info", "RoomId cannot be 0" ) );
//                object.put( "type", "Error" );
                channel.writeAndFlush( new TextWebSocketFrame( createJson( "Error", "RoomId cannot be 0" ) ) );
            } else {
                if ( !channelGroupHashMap.containsKey( roomId ) ) {
                    channelGroupHashMap.put( roomId, new DefaultChannelGroup( GlobalEventExecutor.INSTANCE ) );
                }
                channelGroupHashMap.get( roomId ).add( channel );
                channelRoomIdHashMap.put( channel, roomId );
                JSONObject object = new JSONObject( true );
                object.put( "data", "还没写" );
                object.put( "type", "Synchronization Data" );
                channel.writeAndFlush( new TextWebSocketFrame( object.toJSONString() ) );
            }

        } else if ( type.equals( "Synchronization Data" ) ) {
            // To do store
            //channelTsClientHashMap.get( channel ).checkState( jsonObject );
            if ( tsChannel == null ) {
                throw new Exception( "tsChannel is null" );
            }
            tsChannel.writeAndFlush( new TextWebSocketFrame( msg.text() ) );
            channelOperation.add( channel );
            JSONObject data = jsonObject.getJSONObject( "data" );
            data.put( "state", "insert" );
//            JSONObject Info = new JSONObject( true );
//            Info.put( "data", new JSONObject().put( "info", "Data Received" ) );
//            Info.put( "type", "Message" );

//            JSONObject reject = new JSONObject( true );
//            reject.put( "data", new JSONObject().put( "info", "Data Received" ) );
//            reject.put( "type", "Message" );


            int roomId = channelRoomIdHashMap.get( channel );
            System.out.println( "RoomID in Syn" + roomId );
            channelGroupHashMap.get( roomId ).forEach( ch -> {
                if ( channel != ch ) {
                    ch.writeAndFlush( new TextWebSocketFrame( createJson( "Synchronization Data", data ) ) );
                    //ch.writeAndFlush( new TextWebSocketFrame( transfer.toJSONString() ) );
                } else {
                    //ch.write( new TextWebSocketFrame( msg.text() ) );
                    ch.writeAndFlush( new TextWebSocketFrame( createJson( "Message", "Data Received" ) ) );
                }
            } );
//
//            JSONObject transfer = new JSONObject( true );
//            transfer.put( "data", jsonObject.getString( "data" ) );
//            transfer.put( "type", "Synchronization Data" );


        } else if ( type.equals( "Message" ) ) {
            String info = jsonObject.getJSONObject( "data" ).getString( "info" );
            if ( info.equals( "Synchronization success" ) ) {
                //
            } else if ( info.equals( "Request synchronization" ) ) {
                JSONObject object = new JSONObject( true );
                object.put( "data", "//" );
                object.put( "type", "Synchronization Data" );
                channel.writeAndFlush( new TextWebSocketFrame( object.toJSONString() ) );
            } else if ( info.equals( "Log out" ) ) {
//                JSONObject object = new JSONObject( true );
//                object.put( "data", new JSONObject().put( "info", "Log out confirmed" ) );
//                object.put( "type", "Message" );
                channel.writeAndFlush( new TextWebSocketFrame( createJson( "Message", "Log out confirmed" ) ) );
            } else if ( info.equals( "tsclient" ) ) {
                tsChannel = channel;
                channel.writeAndFlush( new TextWebSocketFrame( createJson( "Message", "TsClient confirmed" ) ) );
            } else if ( info.equals( "State check" ) ) {
                JSONObject data = jsonObject.getJSONObject( "data" );
                String state = data.getString( "result" );
                if ( !Boolean.parseBoolean( state ) ) {
                    data.remove( "info" );
                    data.remove( "result" );
                    data.put( "state", "remove" );
                    channelOperation.poll().writeAndFlush( new TextWebSocketFrame( createJson( "Synchronization Data", data ) ) );
                } else {
                    channelOperation.poll();
                }
            }
        }

        //channelGroupHashMap.get( 0 ).writeAndFlush( "xsas" );
        //System.out.println( "From client: " + msg.text() );


    }

    @Override
    public void channelActive ( ChannelHandlerContext ctx ) throws Exception {
        //super.channelActive( ctx );
    }

    @Override
    public void handlerAdded ( ChannelHandlerContext ctx ) throws Exception {
        //super.handlerAdded( ctx );
        System.out.println( "Handler Added: " + ctx.channel().id().asLongText() );
        Channel channel = ctx.channel();
        channelGroupHashMap.get( 0 ).add( channel );
        channelRoomIdHashMap.put( channel, 0 );
        new Thread( () -> channel.writeAndFlush( new TextWebSocketFrame( "accept" ) ) ).start();

        //System.out.println( mapData.mapTransfer() );
//        JSONObject object = new JSONObject();
//        object.put( "data", channelGroupHashMap.keySet() );
//        object.put( "type", "Room List" );
//        channelGroupHashMap.get( 0 ).forEach( ch -> {
//            if ( channel != ch ) {
//                ch.writeAndFlush( new TextWebSocketFrame( "New Client: " + channel.remoteAddress() ) );
//            } else {
//                new Thread( () -> ch.writeAndFlush( new TextWebSocketFrame( object.toJSONString() ) ) ).start();
//            }
//        } );
    }

    @Override
    public void handlerRemoved ( ChannelHandlerContext ctx ) throws Exception {
        //super.handlerRemoved( ctx );
        System.out.println( "Handler Removed: " + ctx.channel().id().asLongText() );
    }

    @Override
    public void exceptionCaught ( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
        System.out.println( "Exception happens" );
        cause.printStackTrace();
        ctx.close();
    }

    private String createJson ( String type, JSONObject data ) {
        JSONObject object = new JSONObject( true );
        object.put( "data", data );
        object.put( "type", type );

        return object.toJSONString();
    }

    private String createJson ( String type, String data ) {
        JSONObject object = new JSONObject( true );
        object.put( "data", new JSONObject().put( "info", data ) );
        object.put( "type", type );

        return object.toJSONString();
    }

}
