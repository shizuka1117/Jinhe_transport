package com.example.transportation_management.controller;

import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.service.LineService;
import com.example.transportation_management.service.StationService;
import com.example.transportation_management.utils.ParseUtil;
import com.example.transportation_management.utils.Result;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @param line 线路名
     * @param direction 方向（上行/下行）
     * @return
     */
    @GetMapping("/allStations")
    public Result getStationsByLineName(String line,  @RequestParam(required = false, defaultValue = "") String direction){
        if(!direction.isEmpty())
            line = line+direction;
        System.out.println(direction);
        List<Station> list = stationService.queryPathByLineName(line);
        if (list==null)
            return Result.fail("线路'"+line+"'不存在！");
        return Result.ok(list);
    }

    /**
     * 3. 查询某站停靠的所有线路。
     * @param station 站名
     * @return
     */
    @GetMapping("/allLines")
    public Result getLinesByStation(String station){
        if(stationService.queryStationByName(station).size()==0)
            return Result.fail("站点'"+station+"'不存在！");
        return Result.ok(lineService.queryLinesByStationName(station));
    }

    /**
     * 4. 查询某条线路从某站到某站，线路的运行方向、沿路站点和运行时长
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param line 线路名（无方向）
     * @return
     */
    @GetMapping("/path")
    public Result getPath(String begin, String end, String line){
        //TODO: 抽一个函数出来判断
        begin = ParseUtil.parseStationName(begin);
        end = ParseUtil.parseStationName(end);
        if(stationService.queryStationByName(begin).size()==0)
            return Result.fail("站点'"+begin+"'不存在！");
        if(stationService.queryStationByName(end).size()==0)
            return Result.fail("站点'"+end+"'不存在！");
        if(lineService.queryLineByName(ParseUtil.parseLineName(line))==null)
            return Result.fail("线路'"+line+"'不存在！");
        return Result.ok(stationService.queryPathByStations(begin, end, line));
    }

    /**
     * 5.查询某两个站台之间的最短路径（已完成）
     * @param begin 起始站点名字
     * @param end 终止站点名名字
     * @return 途经站点
     */
    @GetMapping("/shortestPath")
    public Result getShortestPathByStations(String begin, String end){
        begin = ParseUtil.parseStationName(begin);
        end = ParseUtil.parseStationName(end);
        if(stationService.queryStationByName(begin).size()==0)
            return Result.fail("站点'"+begin+"'不存在！");
        if(stationService.queryStationByName(end).size()==0)
            return Result.fail("站点'"+end+"'不存在！");
        return Result.ok(stationService.queryShortestPathByStations(begin, end));
    }

    /**
     * 6.根据起始站点名查询某两个站台间是否存在直达线路
     * @param begin 起始站台名
     * @param end 结束站台名
     * @return 直达线路名（包括方向）
     */
    @GetMapping("/directLine")
    public Result getLine(String begin, String end){
        begin = ParseUtil.parseStationName(begin);
        end = ParseUtil.parseStationName(end);
        if(stationService.queryStationByName(begin).size()==0)
            return Result.fail("站点'"+begin+"'不存在！");
        if(stationService.queryStationByName(end).size()==0)
            return Result.fail("站点'"+end+"'不存在！");
        List<String> list = lineService.queryDirectLineByStations(begin, end);
        if(list.size()==0)
            return Result.fail("不存在直达线路！");
        return Result.ok(list);
    }

    /**
     * 7.查询某条线路某个方向的全部班次信息
     * @param line 线路名（含方向）
     * @return 站名与时间表的map
     */
    @GetMapping("/timetable")
    public Result getTimetable(String line, String direction){
        if(lineService.queryLineByName(ParseUtil.parseLineName(line))==null)
            return Result.fail("线路'"+line+"'不存在！");
        return Result.ok(stationService.queryLineTimetable(line+direction));
    }

    /**
     * 8.查询某个时刻某个站台某个时段内即将停靠的线路
     * @param id 站台id
     * @param time 当前时间
     * @param interval 时段长度
     * @return 线路名，几分钟后到站
     */
    @GetMapping("/nextLines")
    public Result getNextLinesToCome(String id, String time, String interval){
        time = ParseUtil.parseTime(time);
        return Result.ok(lineService.queryNextLinesToCome(id, time, interval));
    }
}
