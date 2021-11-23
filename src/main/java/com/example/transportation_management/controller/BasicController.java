package com.example.transportation_management.controller;

import com.example.transportation_management.service.LineRepository;
import com.example.transportation_management.entity.Line;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("basic")
public class BasicController {
    @Resource
    private LineRepository lineRepository;

    @GetMapping("/get")
    public Line getLine(String id){
        Optional<Line> lineById = lineRepository.findById(id);
        return lineById.orElse(null);
    }

}
