package com.example.transportation_management.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity("from_to")
@Data
public class FromTo {

    @Id
    private Integer id;

    @StartNode
    private Station beginStation;

    @EndNode
    private Station endStation;

    @Property(name="lineName")
    private String lineName;
}
