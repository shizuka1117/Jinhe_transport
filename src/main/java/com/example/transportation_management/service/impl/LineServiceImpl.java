package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.entity.Str2IntDTO;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.entity.Str2StrDTO;
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
        String cql = "match (s:Station)-[r]-() where s.name = $name return s.id, collect(distinct r.line_name)";
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
    public List<Str2StrDTO> queryDirectLineByStations(String begin, String end) {
        List<String> list = passRepository.findIntersectLines(begin, end);
        List<Str2StrDTO> resList = new LinkedList<>();
        for (String str: list){
            int index = str.lastIndexOf("路");
            resList.add(new Str2StrDTO(str.substring(0, index+1), str.substring(index+1)));
        }
        return resList;
    }

    @Override
    public List<Str2IntDTO> queryNextLinesToCome(String stationId, String curTime, String interval) {
        String endTime = ParseUtil.addTime(curTime, interval);
        Result result = session.run("match ()-[r]->(s:Station) WHERE s.id= $id return r.line_name, [x IN r.timetable WHERE x >='"+curTime+"' and x <'"+endTime+"'][0]", Values.parameters("id", stationId));
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

    @Override
    public List<Str2StrDTO> queryNextLinesToCome(String stationId, String curTime) {
        Result result = session.run("match (s:Station)<-[r]-() where s.id = $id return r.line_name, [x IN r.timetable where x >= $time][0..3]", parameters("id", stationId, "time", curTime));
        List<Record> list = result.list();
        List<Str2StrDTO> resList = new LinkedList<>();
        StringBuffer stringBuffer = new StringBuffer("分钟后到站");
        for(Record record: list){
            List<Value> values = record.values();
            if(values.get(1)!=NULL){
                String lineName = ParseUtil.solveValue(values.get(0), String.class);
                List<String> arriveTime = ParseUtil.solveValues(values.get(1), String.class);
                for(int i = 0; i<arriveTime.size(); i++){
                    Long tmp = ParseUtil.getInterval(curTime, arriveTime.get(i));
                    String str = "";
                    if(tmp==0)
                        str = "即将到站";
                    else
                        str = stringBuffer.insert(0, tmp.toString()).toString();
                    resList.add(new Str2StrDTO(lineName+"班次"+(i+1), str));
                }
            }
        }
        return resList;
    }
}
