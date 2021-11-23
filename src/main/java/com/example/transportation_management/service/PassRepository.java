package com.example.transportation_management.service;

import com.example.transportation_management.entity.Pass;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassRepository extends Neo4jRepository<Pass,Integer> {
}
