package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.entity.Str2IntDTO;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.entity.Str2StrDTO;
import com.example.transportation_management.service.LineService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

import static org.neo4j.driver.Values.NULL;
import static org.neo4j.driver.Values.parameters;

@Service
public class LineServiceImpl implements LineService {
    @Resource
    Session session;
    @Resource
    LineRepository lineRepository;
    @Resource
    FromToRepository fromToRepository;

    @Override
    public Line queryLineByName(String name) {
        return lineRepository.findById(name).orElse(null);
    }

    @Override
    public List<Str2ListDTO> queryLinesByStationName(String name) {
        String cql = "match (s:Station)-[r]-() where s.name = $name return s.id, collect(distinct r.line_name)";
        Result result = session.run(cql, parameters("name", name));
        List<Record> list = result.list();
        List<Str2ListDTO> resList = new LinkedList<>();
        for(Record record: list){
            List<Value> values = record.values();
            List<String> lineNames = ParseUtil.solveValues(values.get(1), String.class);
            String stationId = ParseUtil.solveValue(values.get(0));
            resList.add(new Str2ListDTO(stationId, lineNames));
        }
        return resList;
    }

    @Override
    public List<Str2StrDTO> queryDirectLineByStations(String begin, String end) {
        List<String> list = fromToRepository.findIntersectLines(begin, end);
        List<Str2StrDTO> resList = new LinkedList<>();
        // 把名字切割成两部分：路线名和方向
        for (String str: list){
            int index = str.lastIndexOf("路");
            resList.add(new Str2StrDTO(str.substring(0, index+1), str.substring(index+1)));
        }
        return resList;
    }

    @Override
    public List<Str2IntDTO> queryNextLinesToCome(String stationId, String beginTime, String interval) {
        String endTime = ParseUtil.addTime(beginTime, interval);
        String cql = "match ()-[r]->(s:Station) WHERE s.id= $id return r.line_name, [x IN r.timetable WHERE x >=$begin_time and x <$end_time][0]";
        Result result = session.run(cql, Values.parameters("id", stationId, "begin_time", beginTime, "end_time", endTime));
        List<Record> list = result.list();
        List<Str2IntDTO> resList = new LinkedList<>();
        for(Record record: list){
            List<Value> values = record.values();
            if(values.get(1)!=NULL){
                String lineName = ParseUtil.solveValue(values.get(0));
                String arriveTime = ParseUtil.solveValue(values.get(1));
                Long tmp = ParseUtil.getInterval(beginTime, arriveTime);
                resList.add(new Str2IntDTO(lineName, tmp.intValue()));
            }
        }
        return resList;
    }

    @Override
    public List<Str2StrDTO> queryNextLinesToCome(String stationId, String curTime) {
        String cql = "match (s:Station)<-[r]-() where s.id = $id return r.line_name, [x IN r.timetable where x >= $time][0..3]";
        Result result = session.run(cql, parameters("id", stationId, "time", curTime));
        List<Record> list = result.list();
        List<Str2StrDTO> resList = new LinkedList<>();
        String base = "分钟后到站";
        String res;
        for(Record record: list){
            List<Value> values = record.values();
            if(values.get(1)!=NULL){
                String lineName = ParseUtil.solveValue(values.get(0));
                List<String> arriveTime = ParseUtil.solveValues(values.get(1), String.class);
                for(int i = 0; i<arriveTime.size(); i++){
                    Long tmp = ParseUtil.getInterval(curTime, arriveTime.get(i));
                    if(tmp==0)
                        res = "即将到站";
                    else
                        res = tmp+base;
                    resList.add(new Str2StrDTO(lineName+"班次"+(i+1), res));
                }
            }
        }
        return resList;
    }
}
