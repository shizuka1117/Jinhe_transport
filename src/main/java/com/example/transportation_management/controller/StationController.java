package com.example.transportation_management.controller;

import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.LineRepository;
import com.example.transportation_management.service.StationRepository;
import com.example.transportation_management.entity.Line;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("station")
public class StationController {
    @Resource
    private StationRepository stationRepository;
    @Resource
    private LineRepository lineRepository;

    @GetMapping("/getAllStation")
    public List<Station> getStationsByLine(String id){
        return null;
    }
    @GetMapping("/getAllLine")
    public List<Line> getLinesByStation(String id){
        return null;
    }
}
