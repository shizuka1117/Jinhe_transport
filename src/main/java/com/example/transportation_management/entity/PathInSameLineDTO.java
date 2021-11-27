package com.example.transportation_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 专门保存4的数据
 */
@Data
@AllArgsConstructor
public class PathInSameLineDTO {
    String name;
    String runtime;
    List<Station> alongStations;
}
