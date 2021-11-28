package com.example.transportation_management.service;

import com.example.transportation_management.entity.PathInSameLineDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.String2ListDTO;
import com.example.transportation_management.entity.String2StringDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StationService {

    List<Station> queryStationByName(String name);
    /**
     * 2. 查询某条线路方向的全部站点信息（已完成）
     * @param lineName
     * @return
     */
    List<Station> queryPathByLineName(String lineName);

    /**
     * 4. 查询某条线路从某站到某站，线路的运行方向、沿路站点和运行时长
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param line 线路名（无方向）
     * @return
     */
    PathInSameLineDTO queryPathByStations(String begin, String end, String line);

    /**
     * 5.查询某两个站台之间的最短路径
     * @param begin 起始站点名
     * @param end 终止站点名
     * @return
     */
    List<Station> queryShortestPathByStations(String begin, String end);

    /**
     * 9.查询某个时刻某个站台线路最近的3趟班次信息
     * @param stationId 站台id
     * @param curTime 某个时刻
     * @return 线路名（以及班次顺序），几分钟后到站
     */
    List<String2StringDTO> queryNextLinesToCome(String stationId, String curTime);

    /**
     * 7.查询某条线路某个方向的全部班次信息
     * @param lineName 线路名（含方向）
     * @return 站名与时间表的map
     */
    List<String2ListDTO> queryLineTimetable(String lineName);
}
