package io.microconfig.osdf.metrics;

import static java.lang.Math.abs;

public enum MetricType {
    ABSOLUTE {
        @Override
        public boolean check(double baseline, double actual, double deviation) {
            return abs(baseline - actual) <= deviation;
        }
    },
    RELATIVE {
        @Override
        public boolean check(double baseline, double actual, double deviation) {
            if (baseline == 0) return actual == 0;
            return abs(actual / baseline - 1) <= deviation;
        }
    };

    public abstract boolean check(double baseline, double actual, double deviation);
}
