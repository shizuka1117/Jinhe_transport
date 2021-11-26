package com.example.transportation_management.service;

import com.example.transportation_management.entity.LineWithStationsVO;
import com.example.transportation_management.entity.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface StationService {
    /**
     * 2. 查询某条线路方向的全部站点信息（已完成）
     * @param lineName
     * @return
     */
    List<Station> queryPathByLineName(String lineName);

    /**
     * TODO: 4. 查询某线路从某站到某站线路的运行方向、沿路站点和运行时长
     * @param begin 起始站点名
     * @param end 结束站点名
     * @param lineName 线路名（无方向）
     * @return
     */
    LineWithStationsVO queryPathByStations(String begin, String end, String lineName);

    /**
     * 5.查询某两个站台之间的最短路径（已完成）
     * @param begin 起始站点id
     * @param end 终止站点名id
     * @return
     */
    List<Station> queryShortestPathByStations(String begin, String end);

    /**
     * TODO: 9.查询某个时刻某个站台线路最近的3趟班次信息
     * @param stationId 站台id
     * @param curTime 某个时刻
     * @return 线路名（以及班次顺序），几分钟后到站
     */
    Map<String, String> queryNextLinesToCome(String stationId, String curTime);

    /**
     * 7.查询某条线路某个方向的全部班次信息
     * @param lineName 线路名（含方向）
     * @return 站名与时间表的map
     */
    Map<String, List<String>> queryLineTimetable(String lineName);
}
