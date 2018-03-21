package com.plusnconsulting;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DeviceReading {
    public DeviceReading() {
    }

    public DeviceReading(final UUID serial,final double[] location, final HashMap<String, Double> measurements,final Date taken) {
        this.serial = serial;
        this.location = location;
        this.measurements = measurements;
        this.taken = taken;
    }

    public String id;
    public UUID serial;
    public double[] location;
    public HashMap<String, Double> measurements;
    public Date taken;
}