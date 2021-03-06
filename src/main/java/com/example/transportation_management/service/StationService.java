package com.example.transportation_management.service;

import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.Str2ListDTO;
import com.example.transportation_management.entity.Str2StrDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StationService {
    Station queryStationById(String id);

    List<Station> queryStationByName(String name);
    /**
     * 2. 查询某条线路方向的全部站点信息
     * @param lineName 线路名（包含方向）
     * @return 站点列表
     */
    List<Station> queryPathByLineName(String lineName);

    /**
     * 4. 查询某条线路从某站到某站，线路的运行方向、沿路站点和运行时长
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param line 线路名（无方向）
     * @return 方向、站点和时长
     */
    PathInSameLineDTO queryPathByStations(String begin, String end, String line);

    /**
     * 5.查询某两个站台之间的最短路径
     * @param begin 起始站点名
     * @param end 终止站点名
     * @return 途径站点
     */
    List<Station> queryShortestPathByStations(String begin, String end);

    /**
     * 7.查询某条线路某个方向的全部班次信息
     * @param lineName 线路名（含方向）
     * @return 站名与时间表的map
     */
    List<Str2ListDTO> queryLineTimetable(String lineName);
}
