package com.example.transportation_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 专门用于保存需求4的结果
 */
@Data
@AllArgsConstructor
public class LineWithStationsVO {
    String name;
    Long runTime;
    List<Station> stations;
}
