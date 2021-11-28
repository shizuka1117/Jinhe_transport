package com.example.transportation_management.dao;

import com.example.transportation_management.entity.Pass;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassRepository extends Neo4jRepository<Pass,Integer> {

    @Query("MATCH p=(l:Line)-[r]->(n:Station) where r.line_name=$line_name and n.name=$station_name return p")
    Pass find(@Param("line_name") String lineName, @Param("station_name") String station_name);

    @Query("match (s1:Station)-[r1]-() where s1.name contains $name1 with r1\n" +
            "match (s2:Station)-[r2]-() where s2.name contains $name2 and r2.line_name in r1.line_name return distinct r2.line_name as line_name")
    List<String> findIntersectLines(@Param("name1") String name1, @Param("name2") String name2);
}
