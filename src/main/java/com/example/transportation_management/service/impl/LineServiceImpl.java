package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.entity.Str2IntDTO;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.service.LineService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static org.neo4j.driver.Values.NULL;
import static org.neo4j.driver.Values.parameters;

@Service
public class LineServiceImpl implements LineService {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private final Session session = driver.session();
    @Resource
    LineRepository lineRepository;
    @Resource
    PassRepository passRepository;

    @Override
    public Line queryLineByName(String name) {
        return lineRepository.findById(name).orElse(null);
    }

    @Override
    public List<Str2ListDTO> queryLinesByStationName(String name) {//Integer curPage, Integer pageSize
        String cql = "match (s:Station)-[r]-() where s.name = $name return s.id as station_id, collect(distinct r.line_name) as line_name";
        Result result = session.run(cql, parameters("name", name));
        List<Record> list = result.list();
        List<Str2ListDTO> resList = new LinkedList<>();
        for(Record record: list){
            List<Value> values = record.values();
            List<String> lineNames = ParseUtil.solveValues(values.get(1), String.class);
            String stationId = ParseUtil.solveValue(values.get(0), String.class);
            resList.add(new Str2ListDTO(stationId, lineNames));
        }
        return resList;
    }

    @Override
    public List<String> queryDirectLineByStations(String begin, String end) {
        return passRepository.findIntersectLines(begin, end);
    }

    @Override
    public List<Str2IntDTO> queryNextLinesToCome(String stationId, String curTime, String interval) {
        String endTime = ParseUtil.addTime(curTime, interval);
        Result result = session.run("match ()-[r]->(s:Station) WHERE s.id= $station_id return r.line_name as line, [x IN r.timetable WHERE x >='"+curTime+"' and x <'"+endTime+"'][0] as arrive_time", Values.parameters("station_id", stationId));
        List<Record> list = result.list();
        List<Str2IntDTO> resList = new LinkedList<>();
        for(Record record: list){
            List<Value> values = record.values();
            if(values.get(1)!=NULL){
                String lineName = ParseUtil.solveValue(values.get(0), String.class);
                String arriveTime = ParseUtil.solveValue(values.get(1), String.class);
                Long tmp = ParseUtil.getInterval(curTime, arriveTime);
                resList.add(new Str2IntDTO(lineName, tmp.intValue()));
            }
        }
        return resList;
    }
}
