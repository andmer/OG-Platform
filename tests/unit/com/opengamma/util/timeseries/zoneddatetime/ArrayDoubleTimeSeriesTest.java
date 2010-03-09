 package com.opengamma.util.timeseries.zoneddatetime;


import java.util.List;

import javax.time.calendar.ZonedDateTime;

public class ArrayDoubleTimeSeriesTest extends ZonedDateTimeDoubleTimeSeriesTest {

  @Override
  public ZonedDateTimeDoubleTimeSeries createEmptyTimeSeries() {
    return new ArrayZonedDateTimeDoubleTimeSeries();
  }

  @Override
  public ZonedDateTimeDoubleTimeSeries createTimeSeries(ZonedDateTime[] times, double[] values) {
    return new ArrayZonedDateTimeDoubleTimeSeries(times, values);
  }

  @Override
  public ZonedDateTimeDoubleTimeSeries createTimeSeries(List<ZonedDateTime> times, List<Double> values) {
    return new ArrayZonedDateTimeDoubleTimeSeries(times, values);
  }

  @Override
  public ZonedDateTimeDoubleTimeSeries createTimeSeries(ZonedDateTimeDoubleTimeSeries dts) {
    return new ArrayZonedDateTimeDoubleTimeSeries(dts);
  }
}
