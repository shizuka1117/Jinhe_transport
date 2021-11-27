package com.example.transportation_management.dao;

import com.example.transportation_management.entity.Line;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface LineRepository extends Neo4jRepository<Line,String> {
    @Query("match (n:Line{name:$line_name}) return n")
    Line findLineByName(@Param("line_name")String name);
}
