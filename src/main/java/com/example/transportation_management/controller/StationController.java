package com.example.transportation_management.controller;

import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.LineService;
import com.example.transportation_management.service.StationService;
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
     * @param name 线路名
     * @param direction 方向（上行/下行）
     * @return
     */
    @GetMapping("/stations")
    public List<Station> getStationsByLineName(String name, String direction){
        String lineName = name+direction;
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

    @GetMapping("/path")
    public PathInSameLineDTO getPath(String begin, String end, String lineName){
        return stationService.queryPathByStations(begin, end, lineName);
    }

    @GetMapping("/directPath")
    public List<String> getPath(String begin, String end){
        return lineService.queryDirectLineByStations(begin, end);
    }

    @GetMapping("/timetable")
    public Map<String, List<String>> getTimetable(String lineName){
        return stationService.queryLineTimetable(lineName);
    }
}
