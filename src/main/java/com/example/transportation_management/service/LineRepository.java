package com.example.transportation_management.service;

import com.example.transportation_management.entity.Line;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineRepository extends Neo4jRepository<Line,String> {
}
