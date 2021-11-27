package com.example.transportation_management;

import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.utils.ParseUtil;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.*;

import static org.neo4j.driver.Values.NULL;
import static org.neo4j.driver.Values.parameters;

public class QueryTest {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private Session session = driver.session();
    @Test
    public void testQueryLinesByStationName(){
    }

    @Test
        public void testQueryPath(){
        Result result = session.run("MATCH (n:Station{name:'红瓦寺'}),(m:Station{name:'动物园'}), p = shortestPath((n)-[*..]->(m)) return n.id as s1_id, m.id as s2_id, p");
        List<Station> list = new LinkedList<>();
        Path minPath = null;
        Integer min = Integer.MAX_VALUE;
        while(result.hasNext()){
            Record record = result.next();
            List<Value> values = record.values();
            Path p = values.get(2).asPath();
            if(p.length()<min)
                minPath = p;
        }
        Iterable<Node> nodes = minPath.nodes();
        for (Node node : nodes) {
            list.add(new Station(node.get("id").asString(), node.get("name").asString(), node.get("english").asString()));
        }
        System.out.println(list);
    }

    @Test
    public void testDirectPath(){
        String cql = "match ()-[r]->(s:Station) return r.line_name, count(s) order by count(s) desc limit 15";
        Result result = session.run(cql);
        List<Record> list = result.list();
        Map<String, Integer> resMap = new LinkedHashMap<>();
        for (Record record:list) {
            List<Value> values = record.values();
            resMap.put(values.get(0).asString(), values.get(1).asInt());
        }
        System.out.println(resMap);
    }
}
