package com.example.transportation_management.service;

import com.example.transportation_management.entity.Line;
import com.example.transportation_management.entity.Str2IntDTO;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.entity.Str2StrDTO;

import java.util.List;


public interface LineService {

    Line queryLineByName(String name);
    /**
     * 3. 根据站点名查询某个站点停靠的所有线路
     * @param name 站点名（例：锦城广场）
     * @return 站点名、线路名（包括方向）
     */
    List<Str2ListDTO> queryLinesByStationName(String name);

    /**
     * 6.根据起始站点名查询某两个站台间是否存在直达线路
     * @param begin 起始站台名
     * @param end 结束站台名
     * @return 直达线路名（包括方向）
     */
    List<Str2StrDTO> queryDirectLineByStations(String begin, String end);

    /**
     * 8.查询某个时刻某个站台某个时段内即将停靠的线路
     * @param stationId 站台id
     * @param curTime 当前时间
     * @param interval 时段长度
     * @return 线路名，几分钟后到站
     */
    List<Str2IntDTO> queryNextLinesToCome(String stationId, String curTime, String interval);

    /**
     * 9.查询某个时刻某个站台线路最近的3趟班次信息
     * @param stationId 站台id
     * @param curTime 某个时刻
     * @return 线路名（以及班次顺序），几分钟后到站
     */
    List<Str2StrDTO> queryNextLinesToCome(String stationId, String curTime);
}
