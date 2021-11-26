package com.example.transportation_management.dao;

import com.example.transportation_management.entity.Station;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends Neo4jRepository<Station,String> {
    @Query("MATCH (n1:Station)-[r1]->(n2:Station) where r1.line_name=$line_name and not exists\n" +
            "{MATCH (n3:Station)-[r2]->(n1:Station) where r2.line_name=$line_name} return n1")
    Station findBeginStationByLineName(@Param("line_name")String lineName);

    @Query("MATCH (n1:Station)-[r1]->(n2:Station) where r1.line_name=$line_name and not exists\n" +
            "{MATCH (n2:Station)-[r2]->(n3:Station) where r2.line_name=$line_name} return n2")
    Station findEndStationByLineName(@Param("line_name")String lineName);

    @Query("MATCH (s:Station)-[r]->(n:Station) where r.line_name = $line_name and s.id = $station_id return n")
    Station findNextStation(@Param("station_id") String stationId, @Param("line_name")String lineName);

    @Query("MATCH ()-[r]-(n:Station) where r.name = $line_name and n.name = $station_name return n")
    Station findByStationNameAndLineName(@Param("station_name") String stationName, @Param("line_name")String lineName);
}
