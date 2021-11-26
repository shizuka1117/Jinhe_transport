package com.example.transportation_management.controller;

import com.example.transportation_management.entity.LineWithStationsVO;
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
    @GetMapping("/getStations")
    public List<Station> getStationsByLineName(String name, String direction){
        String lineName = name+direction;
        return stationService.queryPathByLineName(lineName);
    }
    @GetMapping("/getAllLine")
    public Map<String, List<String>> getLinesByStation(String name){
        return lineService.queryLinesByStationName(name);
    }

    @GetMapping("/getPath")
    public LineWithStationsVO getPath(String begin, String end, String lineName){
        return stationService.queryPathByStations(begin, end, lineName);
    }

    @GetMapping("/getDirectPath")
    public List<String> getDirectPath(String begin, String end){
        return lineService.queryDirectLineByStations(begin, end);
    }


}
