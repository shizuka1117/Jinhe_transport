package com.example.transportation_management.controller;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.utils.ParseUtil;
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
     * 1. 查询某条线路的基本信息
     * @param name 路线 name
     * @return
     */
    @GetMapping("/lineInfo")
    public Line getLineById(String name){
        //处理前端输入，截取“路”之前的部分
        name = ParseUtil.parseLineName(name);
        Optional<Line> lineById = lineRepository.findById(name);
        return lineById.orElse(null);
    }
}
