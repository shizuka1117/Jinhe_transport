package com.example.transportation_management;

import com.example.transportation_management.entity.Station;
import com.example.transportation_management.utils.ParseUtil;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.*;

import static org.neo4j.driver.Values.NULL;

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
        String stationId = "59760";
        String curTime = "10:32";
        Result result = session.run("match (s:Station)<-[r]-() where s.id = '"+stationId+"' return r.line_name as line_name, [x IN r.timetable where x >= '"+curTime+"'][0..3] as timetable");
        List<Record> list = result.list();
        Map<String, String> resMap = new LinkedHashMap<>();
        for(Record record: list){
            List<Value> values = record.values();
            if(values.get(1)!=NULL){
                String lineName = ParseUtil.solveValue(values.get(0), String.class);
                List<String> arriveTime = ParseUtil.solveValues(values.get(1), String.class);
                for(int i = 0; i<arriveTime.size(); i++){
                    Long tmp = ParseUtil.getInterval(curTime, arriveTime.get(i));
                    resMap.put(lineName+(i+1), tmp.toString());
                }
            }
        }
        System.out.println(resMap);
    }
}
