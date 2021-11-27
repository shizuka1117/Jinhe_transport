package com.example.transportation_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
/**
 * 专门用于保存需求10的结果
 */
@Data
@AllArgsConstructor
public class MostPassedStationDTO {
    String stationName;
    String stationId;
    Integer size;
    List<String> lines;
}
