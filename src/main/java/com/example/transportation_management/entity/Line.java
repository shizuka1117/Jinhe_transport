package com.example.transportation_management.entity;



import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;


/**
 * 线路节点
 */
@NodeEntity
@Data
public class Line {
    @Id
    private String name; //eg: "1"
    private String route;
    private String onewayTime;
    private String directional;
    private double kilometer;
    private String runtime;
    private double interval;
    private String type;
}
