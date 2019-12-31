package com.ingbyr.hwsc.planner;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class PlannerResult {
    private int bench;
    private double runtime;
    private long indNum;
    private int gen;
    private List<Long> memoryLog;
}
