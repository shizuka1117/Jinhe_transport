package com.example.transportation_management.dao;

import com.example.transportation_management.entity.FromTo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FromToRepository extends Neo4jRepository<FromTo, Integer> {
    @Query("MATCH ()-[r]->(n:Station) where r.line_name = $line_name and n.id = $station_id return r.timetable[0]")
    String findFirstPassTime(@Param("line_name") String lineName, @Param("station_id") String stationId);

    @Query("MATCH ()-[r]->(n:Station) where r.line_name = $line_name return count(r)")
    Integer isPassExisting(@Param("line_name") String lineName);

    @Query("MATCH ()-[r]-(n:Station) where n.id = $station_id and r.line_name<>$line_name return distinct r.line_name")
    List<String> findAllFromTo(@Param("station_id") String stationId, @Param("line_name") String lineName);

    @Query("match (s1:Station)-[r1]-() where s1.name = $name1 with r1\n" +
            "match (s2:Station)-[r2]-() where s2.name = $name2 and r2.line_name in r1.line_name return distinct r2.line_name as line_name")
    List<String> findIntersectLines(@Param("name1") String name1, @Param("name2") String name2);
}
