package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.entity.MostPassedStationDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.Str2IntDTO;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.service.AnalysisService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private final Session session = driver.session();
    @Resource
    StationRepository stationRepository;
    @Resource
    FromToRepository fromToRepository;

    @Override
    public Integer isPassExisting(String line) {
        return fromToRepository.isPassExisting(line);
    }

    @Override
    public List<MostPassedStationDTO> getMostPassedStations() {
        Result result = session.run("match (s:Station)-[r]-() return s.name, s.id, count(distinct r.line_name), collect(distinct r.line_name) order by count(distinct r.line_name) desc limit 15");
        List<Record> list = result.list();
        List<MostPassedStationDTO> resList = new LinkedList<>();
        for(Record record: list) {
            List<Value> values = record.values();
            resList.add(new MostPassedStationDTO(values.get(0).asString(), values.get(1).asString(), values.get(2).asInt(), ParseUtil.solveValues(values.get(3), String.class)));
        }
        return resList;
    }

    @Override
    public List<Str2ListDTO> sortStationsByType() {
        List<Str2ListDTO> resList = new LinkedList<>();
        resList.add(new Str2ListDTO("地铁站", stationRepository.findAllRailStations()));
        resList.add(new Str2ListDTO("始发站", stationRepository.findAllBeginStations()));
        resList.add(new Str2ListDTO("终点站", stationRepository.findAllEndStations()));
        return resList;
    }

    @Override
    public List<Str2IntDTO> sortLinesByType() {
        Result result = session.run("match (l:Line) return l.type, count(l)");
        List<Record> list = result.list();
        List<Str2IntDTO> resList = new LinkedList<>();
        for (Record record:list) {
            List<Value> values = record.values();
            resList.add(new Str2IntDTO(values.get(0).asString(), values.get(1).asInt()));
        }
        return resList;
    }

    //TODO: 没写完
    @Override
    public List<Station> findRepeatedStations(String lineName1, String lineName2) {
        return stationRepository.findRepeatedStations(lineName1, lineName2);
    }

    @Override
    public List<Str2ListDTO> findOtherLines(String lineName) {
        Station curStation = stationRepository.findBeginStationByLineName(lineName);
        List<Str2ListDTO> resList = new LinkedList<>();
        List<String> lineList;
        while(curStation != null){
            lineList = fromToRepository.findAllFromTo(curStation.getId(), lineName);
            if(lineList.size()>0)
                resList.add(new Str2ListDTO(curStation.getName(), lineList));
            curStation = stationRepository.findNextStation(curStation.getId(), lineName);
        }
        return resList;
    }

    @Override
    public List<Str2IntDTO> sortStationsByConnectingLines() {
        String cql = "match (s1:Station)-[r]->(s2:Station) return s1.id, s2.id, s1.name, s2.name, count(distinct r) order by count(distinct r) desc limit 15";
        Result result = session.run(cql);
        List<Record> list = result.list();
        List<Str2IntDTO> resList = new LinkedList<>();
        for (Record record:list) {
            List<Value> values = record.values();
            String name = values.get(2).asString()+"-->"+values.get(3).asString();
            resList.add(new Str2IntDTO(name, values.get(4).asInt()));
        }
        return resList;
    }

    @Override
    public List<Str2IntDTO> sortLinesByStations() {
        String cql = "match ()-[r]->(s:Station) return r.line_name, count(s) order by count(s) desc limit 15";
        Result result = session.run(cql);
        List<Record> list = result.list();
        List<Str2IntDTO> resList = new LinkedList<>();
        for (Record record:list) {
            List<Value> values = record.values();
            resList.add(new Str2IntDTO(values.get(0).asString(), values.get(1).asInt()));
        }
        return resList;
    }
}
