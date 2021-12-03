package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.entity.*;
import com.example.transportation_management.service.StationService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

@Service
public class StationServiceImpl implements StationService {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private final Session session = driver.session();

    @Resource
    FromToRepository fromToRepository;
    @Resource
    StationRepository stationRepository;
    @Resource
    PassRepository passRepository;

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



    //TODO: 修改逻辑
    @Override
    public List<Str2ListDTO> queryLineTimetable(String lineName) {
        List<Str2ListDTO> resList = new LinkedList<>();
        Station beginStation = stationRepository.findBeginStationByLineName(lineName);
        if(beginStation==null)
            return resList;
        Pass pass = passRepository.find(lineName, beginStation.getName());
        resList.add(new Str2ListDTO(beginStation.getName(), pass.getTimetable()));
        Station curStation = beginStation;
        while(curStation != null){
            FromTo fromTo = fromToRepository.findFromTo(curStation.getId(), lineName);
            if(fromTo!=null){
                resList.add(new Str2ListDTO(fromTo.getEndNode().getName(), fromTo.getTimetable()));
                curStation = fromTo.getEndNode();
            }
            else
                break;
        }
        return resList;
    }
}
