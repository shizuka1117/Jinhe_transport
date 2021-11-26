package com.example.transportation_management.dao;

import com.example.transportation_management.entity.FromTo;
import com.example.transportation_management.entity.Pass;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FromToRepository extends Neo4jRepository<FromTo, Integer> {

    @Query("MATCH p=(n:Station)-[r]->() where n.id = $station_id and r.line_name = $line_name return p")
    FromTo findFromTo(@Param("station_id") String stationId, @Param("line_name") String lineName);
}