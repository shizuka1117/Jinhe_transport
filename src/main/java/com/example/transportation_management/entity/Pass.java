package com.example.transportation_management.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.List;

@RelationshipEntity("path")
@Data
public class Pass {
    @Id
    private Integer id;

    @StartNode
    private Line line;

    @EndNode
    private Station station;

    @Property("line_name")
    private String lineName;

    @Property("type")
    private String type;

    private List<String> timetable;
}
