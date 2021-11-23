package com.example.transportation_management.service;

import com.example.transportation_management.entity.Station;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends Neo4jRepository<Station,String> {
}
