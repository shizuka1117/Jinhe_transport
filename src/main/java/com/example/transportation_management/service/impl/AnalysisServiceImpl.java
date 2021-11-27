package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.entity.MostPassedStationDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.AnalysisService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private Session session = driver.session();
    @Resource
    StationRepository stationRepository;
    @Resource
    FromToRepository fromToRepository;

    /**
     *
     * @return
     */
    @Override
    public List<MostPassedStationDTO> getMostPassedStations() {
        Result result = session.run("match (s:Station)-[r]-() return s.name as name, s.id as id, count(distinct r.line_name) as size, collect(distinct r.line_name) as line_name order by count(distinct r.line_name) desc limit 15");
        List<Record> list = result.list();
        List<MostPassedStationDTO> resList = new LinkedList<>();
        for(Record record: list) {
            List<Value> values = record.values();
            resList.add(new MostPassedStationDTO(values.get(0).asString(), values.get(1).asString(), values.get(2).asInt(), ParseUtil.solveValues(values.get(3), String.class)));
        }
        return resList;
    }

    @Override
    public Map<String, List<String>> sortStationsByType() {
        Map<String, List<String>> resMap = new LinkedHashMap<>();
        resMap.put("地铁站", stationRepository.findAllRailStations());
        resMap.put("始发站", stationRepository.findAllBeginStations());
        resMap.put("终点站", stationRepository.findAllEndStations());
        return resMap;
    }

    @Override
    public Map<String, Integer> sortLinesByType() {
        Result result = session.run("match (l:Line) return l.type, count(l)");
        List<Record> list = result.list();
        Map<String, Integer> resMap = new LinkedHashMap<>();
        for (Record record:list) {
            List<Value> values = record.values();
            resMap.put(values.get(0).asString(), values.get(1).asInt());
        }
        return resMap;
    }

    @Override
    public List<Station> findRepeatedStations(String lineName1, String lineName2) {
        return stationRepository.findRepeatedStations(lineName1, lineName2);
    }

    @Override
    public Map<String, List<String>> findOtherLines(String lineName) {
        Station begin = stationRepository.findBeginStationByLineName(lineName);
        Station curStation = begin;
        Map<String, List<String>> resMap = new LinkedHashMap<>();
        List<String> lineList;
        while(curStation != null){
            lineList = fromToRepository.findAllFromTo(curStation.getId(), lineName);
            if(lineList.size()>0)
                resMap.put(curStation.getName(), lineList);
            curStation = stationRepository.findNextStation(curStation.getId(), lineName);
        }
        return resMap;
    }

    @Override
    public Map<String, Integer> sortStationsByConnectingLines() {
        String cql = "match (s1:Station)-[r]->(s2:Station) return s1.id, s2.id, s1.name as s1, s2.name as s2, count(distinct r) as line_num order by count(distinct r) desc limit 15";
        Result result = session.run(cql);
        List<Record> list = result.list();
        Map<String, Integer> resMap = new LinkedHashMap<>();
        for (Record record:list) {
            List<Value> values = record.values();
            resMap.put(values.get(2).asString()+"-->"+values.get(3), values.get(4).asInt());
        }
        return resMap;
    }

    @Override
    public Map<String, Integer> sortLinesByStations() {
        String cql = "match ()-[r]->(s:Station) return r.line_name, count(s) order by count(s) desc limit 15";
        Result result = session.run(cql);
        List<Record> list = result.list();
        Map<String, Integer> resMap = new LinkedHashMap<>();
        for (Record record:list) {
            List<Value> values = record.values();
            resMap.put(values.get(0).asString(), values.get(1).asInt());
        }
        return resMap;
    }
}
