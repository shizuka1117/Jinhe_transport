package com.example.transportation_management.service;

import com.example.transportation_management.entity.MostPassedStationDTO;
import com.example.transportation_management.entity.Station;
import com.example.transportation_management.entity.String2ListDTO;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    /**
     * 10. 统计停靠路线最多的站点并排序
     * @return
     */
    List<MostPassedStationDTO> getMostPassedStations();

    /**
     * 11.a 统计特殊站台，即地铁站、起点站、终点站数量，并返回站点名。
     * @return 返回每种站台类型及对应站台名列表
     */
    List<String2ListDTO> sortStationsByType();

    /**
     * 12. 分组统计常规公交(包括干线、支线、城乡线、驳接线、社区线)、
     * 快速公交(K字开头)、高峰公交(G字开头)、夜班公交(N字开头)的数量
     * @return
     */
    Map<String, Integer> sortLinesByType();

    /**
     * 13.查询两条线路重复的站点
     * @param lineName1 线路名1（带方向）
     * @param lineName2 线路名2 （带方向）
     * @return
     */
    List<Station> findRepeatedStations(String lineName1, String lineName2);

    /**
     * 14. 查询换乘线路
     * @param lineName 线路名（带方向）
     * @return
     */
    List<String2ListDTO> findOtherLines(String lineName);

    /**
     * 15. 根据连接两个相邻站台之间线路数量排序两个相邻站台
     * @return 起始站点名-->结束站点名，线路数
     */
    Map<String, Integer> sortStationsByConnectingLines();

    /**
     * 16. 根据站点数量对线路进行排序
     * @return 线路名&相应站点数
     */
    Map<String, Integer> sortLinesByStations();
}
