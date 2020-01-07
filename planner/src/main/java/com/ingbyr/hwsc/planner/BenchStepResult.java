package com.ingbyr.hwsc.planner;

import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@ToString
public class BenchStepResult {
    public int bench;
    public double runtime;
    public long indNum;
    public int gen;
    public List<Long> memoryLog;
}
