package com.example.transportation_management.controller;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.utils.ParseUtil;
import com.example.transportation_management.utils.Result;
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
    public Result getLineById(String name){
        name = ParseUtil.parseLineName(name);
        Optional<Line> lineById = lineRepository.findById(name);
        if(lineById.isPresent())
            return Result.fail("不存在该线路！", null);
        return Result.ok(lineById);
    }
}
