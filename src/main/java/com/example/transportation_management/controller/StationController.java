package com.example.transportation_management.controller;

import com.example.transportation_management.entity.*;
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
     * @return 站点列表
     */
    @GetMapping("/allStations")
    public Result getStations(String line, @RequestParam(required = false, defaultValue = "") String direction){
        if(lineService.queryLineByName(ParseUtil.parseLineName(line))==null)
            return Result.fail("线路'"+line+"'不存在！");
        String lineName = line+direction;
        List<Station> list = stationService.queryPathByLineName(lineName);
        if (list==null){
            if(!direction.isEmpty())
                return Result.fail("线路’"+line+"’不存在’"+direction+"’方向");
            else
                return Result.fail("请指定线路方向！");
        }
        return Result.ok(list);
    }

    /**
     * 3. 查询某站停靠的所有线路。
     * @param station 站名
     * @return 站点名、线路名
     */
    @GetMapping("/allLines")
    public Result getLines(String station){
        List<Str2ListDTO> list = lineService.queryLinesByStationName(station);
        if(list.size()==0)
            return Result.fail("站点’"+station+"’不存在！");
        return Result.ok(list);
    }

    /**
     * 4. 查询某条线路从某站到某站，线路的运行方向、沿路站点和运行时长
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param line 线路名（无方向）
     * @return 运行方向、沿路站点和运行时长
     */
    @GetMapping("/path")
    public Result getPath(String begin, String end, String line){
        if(stationService.queryStationByName(begin).size()==0)
            return Result.fail("站点’"+begin+"’不存在！");
        if(stationService.queryStationByName(end).size()==0)
            return Result.fail("站点’"+end+"’不存在！");
        if(lineService.queryLineByName(ParseUtil.parseLineName(line))==null)
            return Result.fail("线路’"+line+"’不存在！");
        PathInSameLineDTO pathInSameLineDTO = stationService.queryPathByStations(begin, end, line);
        if(pathInSameLineDTO==null)
            return Result.fail("不存在直达线路。");
        return Result.ok(pathInSameLineDTO);
    }

    /**
     * 5.查询某两个站台之间的最短路径（已完成）
     * @param begin 起始站点名字
     * @param end 终止站点名名字
     * @return 途经站点
     */
    @GetMapping("/shortestPath")
    public Result getShortestPath(String begin, String end){
        if(stationService.queryStationByName(begin).size()==0)
            return Result.fail("站点’"+begin+"’不存在！");
        if(stationService.queryStationByName(end).size()==0)
            return Result.fail("站点’"+end+"’不存在！");
        List<Station> list = stationService.queryShortestPathByStations(begin, end);
        if (list.size()==0)
            return Result.fail("不存在路径。");
        return Result.ok(list);
    }

    /**
     * 6.根据起始站点名查询某两个站台间是否存在直达线路
     * @param begin 起始站台名
     * @param end 结束站台名
     * @return 直达线路名和方向
     */
    @GetMapping("/directLine")
    public Result getLine(String begin, String end){
        if(stationService.queryStationByName(begin).size()==0)
            return Result.fail("站点’"+begin+"’不存在！");
        if(stationService.queryStationByName(end).size()==0)
            return Result.fail("站点’"+end+"’不存在！");
        List<Str2StrDTO> list = lineService.queryDirectLineByStations(begin, end);
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
    public Result getTimetable(String line, @RequestParam(required = false, defaultValue = "")String direction){
        if(lineService.queryLineByName(ParseUtil.parseLineName(line))==null)
            return Result.fail("线路’"+line+"’不存在！");
        List<Str2ListDTO> list = stationService.queryLineTimetable(line+direction);
        if(list.size()==0){
            if(!direction.isEmpty())
                return Result.fail("线路’"+line+"’不存在’"+direction+"’方向");
            else
                return Result.fail("请指定线路方向！");
        }
        return Result.ok(list);
    }

    /**
     * 8.查询某个时刻某个站台某个时段内即将停靠的线路
     * @param id 站台id
     * @param time 当前时间
     * @param interval 时段长度
     * @return 线路名，几分钟后到站
     */
    @GetMapping("/nextLines")
    public Result getNextLines(String id, String time, String interval){
        if(stationService.queryStationById(id)==null)
            return Result.fail("id为’"+id+"’的站点不存在！");
        List<Str2IntDTO> list = lineService.queryNextLinesToCome(id, time, interval);
        if(list.size()==0)
            return Result.fail("该时段无线路经过站点’"+id+"’");
        return Result.ok(list);
    }

    /**
     * 9.查询某个时刻某个站台线路最近的3趟班次信息
     * @param id 站台id
     * @param time 某个时刻
     * @return 线路名（以及班次顺序），几分钟后到站
     */
    @GetMapping("/nextShifts")
    public Result getNextLines(String id, String time){
        if(stationService.queryStationById(id)==null)
            return Result.fail("id为’"+id+"’的站点不存在！");
        List<Str2StrDTO> list = lineService.queryNextLinesToCome(id, time);
        if (list.size()==0)
            return Result.fail("该时段无线路经过站点’"+id+"’");
        return Result.ok(list);
    }
}
