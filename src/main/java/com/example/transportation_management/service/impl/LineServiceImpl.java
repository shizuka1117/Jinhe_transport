package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.entity.FromTo;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.LineService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.relation.Relation;
import java.util.*;

import static org.neo4j.driver.Values.NULL;

@Service
public class LineServiceImpl implements LineService {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private Session session = driver.session();
    @Resource
    LineRepository lineRepository;
    @Resource
    StationRepository stationRepository;
    @Resource
    PassRepository passRepository;
    @Resource
    FromToRepository fromToRepository;

    @Override
    public Map<String, List<String>> queryLinesByStationName(String name) {//Integer curPage, Integer pageSize
        Result result = session.run("match (s:Station)-[r]-() where s.name contains '"+ name +"' return s.id as station_id,  collect(distinct r.line_name) as line_name");
        List<Record> list = result.list();
        Map<String, List<String>> resMap = new LinkedHashMap<>();
        for(Record record: list){
            List<Value> values = record.values();
            List<String> lineNames = ParseUtil.solveValues(values.get(1), String.class);
            String stationId = ParseUtil.solveValue(values.get(0), String.class);
            resMap.put(stationId, lineNames);
        }
        return resMap;
//        Pageable pageable = PageRequest.of(curPage,pageSize);
    }

    @Override
    public List<String> queryDirectLineByStations(String begin, String end) {
        List<String> beginPasses = passRepository.findLinesByStation(begin);
        List<String> endPasses = passRepository.findLinesByStation(end);
        List<String> resList = new LinkedList<>();
        for(String pass: beginPasses){
            if(endPasses.contains(pass))
                resList.add(pass);
        }
        return resList;
    }

    @Override
    public Map<String, String> queryNextLinesToCome(String stationId, String curTime, String interval) {
        String endTime = ParseUtil.addTime(curTime, interval);
        Result result = session.run("match ()-[r]->(s:Station) WHERE s.id= $station_id return r.line_name as line, [x IN r.timetable WHERE x >='"+curTime+"' and x <'"+endTime+"'][0] as arrive_time", Values.parameters("station_id", stationId));
        List<Record> list = result.list();
        Map<String, String> resMap = new LinkedHashMap<>();
        for(Record record: list){
            List<Value> values = record.values();
            if(values.get(1)!=NULL){
                String lineName = ParseUtil.solveValue(values.get(0), String.class);
                String arriveTime = ParseUtil.solveValue(values.get(1), String.class);
                Long tmp = ParseUtil.getInterval(curTime, arriveTime);
                resMap.put(lineName, tmp.toString());
            }
        }
        return resMap;
    }
}
