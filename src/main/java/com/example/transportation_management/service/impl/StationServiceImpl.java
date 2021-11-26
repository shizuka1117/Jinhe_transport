package com.example.transportation_management.service.impl;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.entity.FromTo;
import com.example.transportation_management.entity.LineWithStationsVO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.StationService;
import com.example.transportation_management.utils.ParseUtil;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.NULL;

@Service
public class StationServiceImpl implements StationService {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "1.414213562"));
    private Session session = driver.session();

    @Resource
    FromToRepository fromToRepository;
    @Resource
    StationRepository stationRepository;
    @Resource
    PassRepository passRepository;
    @Override

    public List<Station> queryPathByLineName(String lineName) {
        Station beginStation = stationRepository.findBeginStationByLineName(lineName);
        Station curStation = beginStation;
        List<Station> resList = new LinkedList<>();
        while(curStation != null){
            resList.add(curStation);
            curStation = stationRepository.findNextStation(curStation.getId(), lineName);
        }
        return resList;
    }

    /** TODO: 逻辑错误
     *
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param lineName 线路名（无方向）
     * @return
     */
    @Override
    public LineWithStationsVO queryPathByStations(String begin, String end, String lineName) {
//        Station beginStation = stationRepository.findByStationNameAndLineName(begin, lineName);
//        Station endStation = stationRepository.findByStationNameAndLineName(end, lineName);
//        String beginTime = passRepository.findFirstPassTime(lineName, begin);
//        String endTime = passRepository.findFirstPassTime(lineName, end);
//        lineName = passRepository.find(lineName, begin);
//        Long interval = ResultUtil.getInterval(beginTime, endTime);
//        Station curStation = beginStation;
//        List<Station> stations = new LinkedList<>();
//        while(curStation != endStation){
//            stations.add(curStation);
//            curStation = stationRepository.findNextStation(curStation.getId(), lineName);
//        }
//        return new LineWithStationsVO(lineName, interval, stations);
        return null;
    }

    @Override
    public List<Station> queryShortestPathByStations(String begin, String end) {
        Result result = session.run("MATCH (n:Station{name:'"+begin+"'}),(m:Station{name:'"+end+"'}), p = shortestPath((n)-[*..]->(m)) return n.id as s1_id, m.id as s2_id, p");
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
        return list;
    }

    @Override
    public Map<String, String> queryNextLinesToCome(String stationId, String curTime) {
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
        return resMap;
    }

    @Override
    public Map<String, List<String>> queryLineTimetable(String lineName) {
        Map<String, List<String>> resMap = new LinkedHashMap<>();//必须使用linkedHashMap而不能使用hashmap，保持结果有序
        Station beginStation = stationRepository.findBeginStationByLineName(lineName);
        List<String> beginTimetable = passRepository.findTimetable(beginStation.getId(), lineName);
        resMap.put(beginStation.getName(), beginTimetable);
        Station curStation = beginStation;
        //TODO: 修改逻辑
        while(curStation != null){
            FromTo fromTo = fromToRepository.findFromTo(curStation.getId(), lineName);
            if(fromTo!=null){
                resMap.put(fromTo.getEndNode().getName(), fromTo.getTimetable());
                curStation = fromTo.getEndNode();
            }
            else
                break;
        }
        return resMap;
    }
}
