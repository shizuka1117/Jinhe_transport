package com.example.transportation_management.controller;

import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.LineService;
import com.example.transportation_management.service.StationService;
import com.example.transportation_management.utils.ParseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("station")
public class StationController {
    @Resource
    private StationService stationService;
    @Resource
    private LineService lineService;

    /**
     * 2. 获取某条线路的全部站点信息
     * @param line 线路名
     * @param direction 方向（上行/下行）
     * @return
     */
    @GetMapping("/allStations")
    public List<Station> getStationsByLineName(String line, String direction){
        String lineName = line+direction;
        return stationService.queryPathByLineName(lineName);
    }

    /**
     * 3. 查询某站停靠的所有线路。
     * @param station 站名
     * @return
     */
    @GetMapping("/allLines")
    public Map<String, List<String>> getLinesByStation(String station){
        return lineService.queryLinesByStationName(station);
    }

    /**
     * 4. 查询某条线路从某站到某站，线路的运行方向、沿路站点和运行时长
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param line 线路名（无方向）
     * @return
     */
    @GetMapping("/path")
    public PathInSameLineDTO getPath(String begin, String end, String line){
        begin = ParseUtil.parseStationName(begin);
        end = ParseUtil.parseStationName(end);
        return stationService.queryPathByStations(begin, end, line);
    }

    /**
     * 5.查询某两个站台之间的最短路径（已完成）
     * @param begin 起始站点名字
     * @param end 终止站点名名字
     * @return
     */
    @GetMapping("/shortestPath")
    List<Station> getShortestPathByStations(String begin, String end){
        begin = ParseUtil.parseStationName(begin);
        end = ParseUtil.parseStationName(end);
        return stationService.queryShortestPathByStations(begin, end);
    }

    /**
     * 6.根据起始站点名查询某两个站台间是否存在直达线路
     * @param begin 起始站台名
     * @param end 结束站台名
     * @return 直达线路名（包括方向）
     */
    @GetMapping("/directLine")
    public List<String> getLine(String begin, String end){
//        begin = ParseUtil.parseStationName(begin);
//        end = ParseUtil.parseStationName(end);
        return lineService.queryDirectLineByStations(begin, end);
    }

    /**
     * 7.查询某条线路某个方向的全部班次信息
     * @param line 线路名（含方向）
     * @return 站名与时间表的map
     */
    @GetMapping("/timetable")
    public Map<String, List<String>> getTimetable(String line, String direction){
        return stationService.queryLineTimetable(line+direction);
    }

    /**
     * 8.查询某个时刻某个站台某个时段内即将停靠的线路
     * @param id 站台id
     * @param time 当前时间
     * @param interval 时段长度
     * @return 线路名，几分钟后到站
     */
    @GetMapping("/nextLines")
    Map<String, String> getNextLinesToCome(String id, String time, String interval){
        time = ParseUtil.parseTime(time);
        return lineService.queryNextLinesToCome(id, time, interval);
    }
}
