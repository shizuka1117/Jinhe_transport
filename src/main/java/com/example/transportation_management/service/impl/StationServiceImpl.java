package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.service.StationService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class StationServiceImpl implements StationService {
    @Resource
    Session session;

    @Resource
    FromToRepository fromToRepository;
    @Resource
    StationRepository stationRepository;

    @Override
    public Station queryStationById(String id) {
        return stationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Station> queryStationByName(String name) {
        return stationRepository.findByName(name);
    }

    @Override
    public List<Station> queryPathByLineName(String lineName) {
        Station beginStation = stationRepository.findBeginStationByLineName(lineName);
        if(beginStation.getId()==null)
            return null;
        Station endStation = stationRepository.findEndStationByLineName(lineName);
        String cql = "MATCH (n:Station{name:$begin}),(m:Station{name:$end}), p = (n)-[r*..]->(m) where all(x in r where x.line_name = $line_name) return p";
        Result result = session.run(cql, parameters("begin", beginStation.getName(), "end", endStation.getName(), "line_name", lineName));
        List<Station> resList = new LinkedList<>();
        Record record = result.next();
        List<Value> values = record.values();
        Path p = values.get(0).asPath();
        for (Node node : p.nodes())//获取沿路站点
            resList.add(new Station(node.get("id").asString(), node.get("name").asString(), node.get("english").asString()));
        return resList;
    }

    @Override
    public PathInSameLineDTO queryPathByStations(String begin, String end, String line) {
        String cql = "MATCH (n:Station{name:$begin}),(m:Station{name:$end}), p = (n)-[r*..]->(m) where all(x in r where x.line_name starts with $line_name) return p, r[0].line_name";
        Result result = session.run(cql, parameters("begin", begin, "end", end, "line_name", line));
        if (!result.hasNext())
            return null;
        Record record = result.next();
        List<Value> values = record.values();
        Path p = values.get(0).asPath();
        String lineName = values.get(1).asString();
        //获取沿路站点
        List<Station> stationList = new LinkedList<>();
        for (Node node : p.nodes())
            stationList.add(new Station(node.get("id").asString(), node.get("name").asString(), node.get("english").asString()));
        //计算时间
        String beginTime = fromToRepository.findFirstPassTime(lineName,stationList.get(0).getId());
        String endTime = fromToRepository.findFirstPassTime(lineName,stationList.get(p.length()).getId());
        String interval = ParseUtil.getInterval(beginTime, endTime).toString();
        return new PathInSameLineDTO(lineName, interval, stationList);
    }

    @Override
    public List<Station> queryShortestPathByStations(String begin, String end) {
        String cql = "MATCH (n:Station{name:$begin}),(m:Station{name:$end}), p = shortestPath((n)-[*..]->(m)) return p";
        Result result = session.run(cql, parameters("begin", begin, "end", end));
        List<Station> list = new LinkedList<>();
        Path minPath = null;
        int min = Integer.MAX_VALUE;
        // 查找返回路径中长度最短的
        while(result.hasNext()){
            Record record = result.next();
            List<Value> values = record.values();
            Path p = values.get(0).asPath();
            if(p.length()<min)
                minPath = p;
        }
        if (minPath!=null){
            for (Node node : minPath.nodes()) {
                list.add(new Station(node.get("id").asString(), node.get("name").asString(), node.get("english").asString()));
            }
        }
        return list;
    }



    @Override
    public List<Str2ListDTO> queryLineTimetable(String lineName) {
        List<Str2ListDTO> resList = new LinkedList<>();
        Station endStation = stationRepository.findEndStationByLineName(lineName);
        if(endStation==null)
            return resList;
        String cql = "match p = (n:Line)-[r*..]->(m:Station{name:$end}) where all(x in r where x.line_name = $line_name) return p";
        Result result = session.run(cql, parameters( "end", endStation.getName(), "line_name", lineName));
        Record record = result.next();
        List<Value> values = record.values();
        Path p = values.get(0).asPath();
        List<List<String>> timeList = new LinkedList<>();
        List<String> nameList = new LinkedList<>();
        for(Node node: p.nodes())
            nameList.add(ParseUtil.solveValue(node.get("name")));
        for(Relationship relationship: p.relationships())
            timeList.add(ParseUtil.solveValues(relationship.get("timetable"), String.class));
        for(int i = 0; i<p.length(); i++)
            resList.add(new Str2ListDTO(nameList.get(i+1), timeList.get(i)));
        return resList;
    }
}
