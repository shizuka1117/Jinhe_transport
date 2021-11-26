package com.example.transportation_management.dao;

import com.example.transportation_management.entity.Pass;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassRepository extends Neo4jRepository<Pass,Integer> {

    /**
     * 找到第一次经过某站的时间
     * @param lineName
     * @param stationId
     * @return
     */
    @Query("MATCH ()-[r]->(n:Station) where r.line_name = $line_name and n.id = $station_id return r.timetable[0]")
    String findFirstPassTime(@Param("line_name") String lineName, @Param("station_id") String stationId);

    @Query("MATCH ()-[r]-(n:Station) where l.name = $line_name and n.id = $station_id return r")
    String find(@Param("line_name") String lineName, @Param("station_id") String stationId);

    @Query("MATCH ()-[r]-(n:Station) where n.name = $station_name return distinct r.line_name")
    List<String> findLinesByStation(@Param("station_name") String stationName);

    @Query("MATCH (l:Line)-[r]->(n:Station) where n.id = $station_id and r.line_name = $line_name return r.timetable")
    List<String> findTimetable(@Param("station_id") String stationId, @Param("line_name") String lineName);
}
