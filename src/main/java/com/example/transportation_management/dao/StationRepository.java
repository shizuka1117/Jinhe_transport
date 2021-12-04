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

    @Query("MATCH (s:Station) where s.name = $name return s")
    List<Station> findByName(@Param("name") String name);

    @Query("MATCH (s:Station) WHERE s.name starts with '地铁' return s.name")
    List<String> findAllRailStations();

    @Query("MATCH (n1:Station)-[r1]->(n2:Station) where not exists {MATCH (n3:Station)-[r2]->(n1:Station) where r2.line_name=r1.line_name} return distinct n1.name")
    List<String> findAllBeginStations();

    @Query("MATCH (n1:Station)-[r1]->(n2:Station) where not exists {MATCH (n2:Station)-[r2]->(n3:Station) where r2.line_name=r1.line_name} return distinct n2.name")
    List<String> findAllEndStations();

    @Query("match ()-[r1]-(s1:Station) where r1.line_name=$line1 and exists {match ()-[r2]-(s2:Station) where r2.line_name=$line2 and s2.id in s1.id} return distinct s1")
    List<Station> findRepeatedStations(@Param("line1")String lineName1, @Param("line2")String lineName2);

    @Query("match ()-[r]-(s:Station) where r.line_name=$line return s")
    List<Station> findAllPassingStations(@Param("line") String lineName);
}
