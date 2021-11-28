package com.example.transportation_management.dao;

import com.example.transportation_management.entity.Line;
import org.springframework.data.neo4j.repository.Neo4jRepository;


public interface LineRepository extends Neo4jRepository<Line,String> {
}
