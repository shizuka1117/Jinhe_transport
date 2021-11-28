package com.example.transportation_management.controller;

import com.example.transportation_management.dao.FromToRepository;
import com.example.transportation_management.dao.LineRepository;
import com.example.transportation_management.entity.Line;
import com.example.transportation_management.dao.PassRepository;
import com.example.transportation_management.dao.StationRepository;
import com.example.transportation_management.service.LineService;
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
    private LineService lineService;

    /**
     * 1. 查询某条线路的基本信息
     * @param name 路线 name
     * @return
     */
    @GetMapping("/lineInfo")
    public Result getLineById(String name){
        name = ParseUtil.parseLineName(name);
        System.out.println(name);
        Line line = lineService.queryLineByName(name);
        if(line==null)
            return Result.fail("线路'"+name+"'不存在！");
        return Result.ok(line);
    }
}
