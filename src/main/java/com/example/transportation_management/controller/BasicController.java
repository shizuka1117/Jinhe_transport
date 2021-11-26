package com.example.transportation_management.controller;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("basic")
public class BasicController {
    @Resource
    private LineRepository lineRepository;
    @Resource
    private FromToRepository fromToRepository;
    @Resource
    private StationRepository stationRepository;
    @Resource
    private PassRepository passRepository;

    /**
     * 1. 返回一条路线的全部信息
     * @param id 路线id
     * @return
     */
    @GetMapping("/getLineInfo")
    public Line getLineById(String id){
        Optional<Line> lineById = lineRepository.findById(id);
        return lineById.orElse(null);
    }
}
