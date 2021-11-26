package com.example.transportation_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * 站点节点
 */
@Data
@NodeEntity
@AllArgsConstructor
public class Station {
    @Id
    private String id;//eg: "41394"
    private String name;
    private String english;
}
