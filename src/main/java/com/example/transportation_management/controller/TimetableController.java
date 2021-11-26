package com.example.transportation_management.controller;

import com.example.transportation_management.service.LineService;
import com.example.transportation_management.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/timetable")
public class TimetableController {
    @Resource
    private StationService stationService;
    @GetMapping("/get")
    public Map<String, List<String>> getTimetable(String lineName){
        return stationService.queryLineTimetable(lineName);
    }
}
