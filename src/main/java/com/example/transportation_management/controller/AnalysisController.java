package com.example.transportation_management.controller;

import com.example.transportation_management.entity.MostPassedStationDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.String2ListDTO;
import com.example.transportation_management.service.AnalysisService;
import com.example.transportation_management.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

//TODO: 修改前后端接口
@RestController
@RequestMapping("analysis")
public class AnalysisController {
    @Resource
    AnalysisService analysisService;

    /**
     * 9.
     * @param
     * @return
     */
    @GetMapping("/mostPassedStations")
    public Result getMostPassedStations(){
        return Result.ok(analysisService.getMostPassedStations());
    }

    /**
     * 12.
     * @return
     */
    @GetMapping("/linesByType")
    public Map<String, Integer> getAllTypesOfLines(){
        return analysisService.sortLinesByType();
    }

    /**
     * 13.
     * @param lineName1
     * @param lineName2
     * @return
     */
    @GetMapping("/repeatedStations")
    public List<Station> getRepeatedStations(String lineName1, String lineName2){
        return analysisService.findRepeatedStations(lineName1, lineName2);
    }

    /**
     * 14.
     * @param lineName
     * @return
     */
    @GetMapping("/getOtherLines")
    public List<String2ListDTO> findOtherLines(String lineName){
        return analysisService.findOtherLines(lineName);
    }
}
