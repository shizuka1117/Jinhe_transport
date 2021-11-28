package com.example.transportation_management.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

import java.util.List;

@RelationshipEntity("from_to")
@Data
@NoArgsConstructor
public class FromTo {

    @GeneratedValue//关系读入时是没有id的，需要自动生成
    @Id
    private Integer id;

    @StartNode
    private Station startNode;

    @EndNode
    private Station endNode;

    @Property(name="line_name")
    private String lineName;

    private List<String> timetable;
}
