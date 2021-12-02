package com.example.transportation_management.controller;

import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.Str2IntDTO;
import com.example.transportation_management.entity.Str2ListDTO;
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
     * 10. 统计停靠路线最多的站点并排序
     * @return ID、站台名、线路条数、停靠线路
     */
    @GetMapping("/mostPassedStations")
    public Result getMostPassedStations(){
        return Result.ok(analysisService.getMostPassedStations());
    }

    /**
     * 11. 统计地铁站、起点站、终点站数量，并返回站点名
     * @return 特殊站点的类型和站名
     */
    @GetMapping("/specialStations")
    public Result getSpecialStations(){
        return Result.ok(analysisService.sortStationsByType());
    }

    /**
     * 12. 分组统计常规公交(包括干线、支线、城乡线、驳接线、社区线)、
     * 快速公交(K字开头)、高峰公交(G字开头)、夜班公交(N字开头)的数量
     * @return 线路类型和数量
     */
    @GetMapping("/linesByType")
    public Result getAllTypesOfLines(){
        return Result.ok(analysisService.sortLinesByType());
    }

    /**
     * 13. 查询两条线路重复的站点名
     * @param line1 线路名1
     * @param direction1 方向1
     * @param line2 线路名2
     * @param direction2 方向2
     * @return 重复的站点
     */
    @GetMapping("/repeatedStations")
    public Result getRepeatedStations(String line1, String direction1, String line2, String direction2){
        String lineName1 = line1+direction1;
        String lineName2 = line2+direction2;
        if (analysisService.isPassExisting(lineName1)==0)
            return Result.fail("线路方向’"+lineName1+"'不存在！");
        if (analysisService.isPassExisting(lineName2)==0)
            return Result.fail("线路方向’"+lineName2+"'不存在！");
        return Result.ok(analysisService.findRepeatedStations(lineName1, lineName2));
    }

    /**
     * 14. 查询换乘线路。换乘线路数即线路停靠的所有站台停靠其他线路的数量的总和。(2/3/4分)
     * @param line 线路名
     * @param direction 方向
     * @return 沿路站点名和对应的换乘线路
     */
    @GetMapping("/transferLines")
    public Result findOtherLines(String line, String direction){
        String lineName = line+direction;
        if (analysisService.isPassExisting(lineName)==0)
            return Result.fail("线路方向’"+lineName+"'不存在！");
        return Result.ok(analysisService.findOtherLines(lineName));
    }
}
