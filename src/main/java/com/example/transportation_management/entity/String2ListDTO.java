package com.example.transportation_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class String2ListDTO {
    String key;
    List<String> value;
}
