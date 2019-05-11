package test_websocket.city;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class mapData {
    private static baseMap basemap = new baseMap();

    public static String mapTransfer () {
        return JSON.toJSONString( basemap );
    }
}


class baseMap {
    //private List<LinkedHashMap<String, Object>> road, building;
    private List<road> roads = new ArrayList<>();
    private List<building> buildings = new ArrayList<>();

    public baseMap () {
        road r1 = new road( new vector2D( 0, 0 ), new vector2D( 1, 1 ), 3 );
        road r2 = new road( new vector2D( 0, 0 ), new vector2D( 1, 1 ), 3 );
        building b1 = new building( "asd", new vector2D( 1, 1 ) );
        building b2 = new building( "asd", new vector2D( 1, 1 ) );

        roads.add( r1 );
        roads.add( r2 );
        buildings.add( b1 );
        buildings.add( b2 );

//        System.out.println( JSON.toJSONString( r1 ) );
    }

    public List<road> getRoads () {
        return roads;
    }

    public void setRoads ( List<road> roads ) {
        this.roads = roads;
    }

    public List<building> getBuildings () {
        return buildings;
    }

    public void setBuildings ( List<building> buildings ) {
        this.buildings = buildings;
    }


}

class road {
    @JSONField( name = "from" )
    private vector2D from;

    @JSONField( name = "to" )
    private vector2D to;

    @JSONField( name = "width" )
    private double width;

    public road ( vector2D from, vector2D to, double width ) {
        this.from = from;
        this.to = to;
        this.width = width;
    }

    public vector2D getFrom () {
        return from;
    }

    public void setFrom ( vector2D from ) {
        this.from = from;
    }

    public vector2D getTo () {
        return to;
    }

    public void setTo ( vector2D to ) {
        this.to = to;
    }

    public double getWidth () {
        return width;
    }

    public void setWidth ( double width ) {
        this.width = width;
    }


}

class building {
    @JSONField( name = "prototype" )
    private String prototype;

    @JSONField( name = "center" )
    private vector2D center;

    public building ( String prototype, vector2D center ) {
        this.prototype = prototype;
        this.center = center;
    }

    public vector2D getCenter () {
        return center;
    }

    public void setCenter ( vector2D center ) {
        this.center = center;
    }

    public String getPrototype () {
        return prototype;
    }

    public void setPrototype ( String prototype ) {
        this.prototype = prototype;
    }

}

class vector2D {
    private double x = 0, y = 0;

    public vector2D ( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public double getX () {
        return x;
    }

    public double getY () {
        return y;
    }
}
