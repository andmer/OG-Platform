/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.push.analytics.formatting;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.time.calendar.LocalDate;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.opengamma.core.marketdatasnapshot.VolatilitySurfaceData;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.time.Tenor;
import com.opengamma.web.server.conversion.LabelFormatter;

/* package */ class VolatilitySurfaceDataFormatter extends AbstractFormatter<VolatilitySurfaceData> {

  protected VolatilitySurfaceDataFormatter() {
    super(VolatilitySurfaceData.class);
    addFormatter(new Formatter<VolatilitySurfaceData>(Format.EXPANDED) {
      @Override
      Object format(VolatilitySurfaceData value, ValueSpecification valueSpec) {
        return formatExpanded(value);
      }
    });
  }

  @Override
  public String formatCell(VolatilitySurfaceData value, ValueSpecification valueSpec) {
    int xSize = value.getUniqueXValues().size();
    int ySize = Sets.newHashSet(value.getYs()).size();
    return "Volatility Surface (" + xSize + " x " + ySize + ")";
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> formatExpanded(VolatilitySurfaceData surface) {
    // the x and y values won't necessarily be unique and won't necessarily map to a rectangular grid
    // this projects them onto a grid and inserts nulls where there's no data available
    SortedSet xVals = surface.getUniqueXValues();
    SortedSet yVals = Sets.newTreeSet((Iterable) Arrays.asList(surface.getYs()));
    Map<String, Object> results = Maps.newHashMap();
    results.put("x_labels", getAxisLabels(xVals));
    results.put("y_labels", getAxisLabels(yVals));
    if (isPlottable(surface)) {
      return formatForPlotting(surface, xVals, yVals, results);
    } else {
      return formatForGrid(surface, xVals, yVals, results);
    }
  }

  /**
   * Formats the surface data for display in a grid of text.
   * @param surface The surface data
   * @return The data formatted for display as text
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> formatForGrid(VolatilitySurfaceData surface,
                                            SortedSet xVals,
                                            SortedSet yVals,
                                            Map<String, Object> baseResults) {
    Map<String, Object> results = Maps.newHashMap(baseResults);
    List<List<Object>> vol = Lists.newArrayListWithCapacity(yVals.size());
    for (Object yVal : yVals) {
      List<Object> volVals = Lists.newArrayListWithCapacity(xVals.size());
      for (Object xVal : xVals) {
        Double volatility = surface.getVolatility(xVal, yVal);
        volVals.add(volatility);
      }
      vol.add(volVals);
    }
    results.put("matrix", vol);
    return results;
  }

  /**
   * <p>Formats the surface data for display in the 3D surface viewer.. Returns a map containing the x-axis labels 
   * and values, y-axis labels and values, axis titles and volatility values. The lists of axis labels are sorted and 
   * have no duplicate values (which isn't necessarily true of the underlying data). The volatility data list contains 
   * a value for every combination of x and y values. If there is no corresponding value in the underlying data the 
   * volatility value will be {@code null}.</p>
   * <p>The axis values are numeric values which correspond to the axis labels. It is unspecified what they
   * actually represent but their relative sizes show the relationship between the label values.
   * This allows the labels to be properly laid out on the plot axes.</p>
   * <p>Not all volatility surfaces can be sensibly plotted as a surface and in that case the axis labels can't
   * be converted to a meaningful numeric value. For these surfaces one or both of the axis values will be missing
   * and the UI shouldn't attempt to plot the surface.</p>
   *
   * @param surface The surface
   * @return {x_labels: [...],
   *          x_values: [...],
   *          x_title: "X Axis Title",
   *          y_labels: [...],
   *          y_values: [...],
   *          y_title: "Y Axis Title",
   *          vol: [x0y0, x1y0,... , x0y1, x1y1,...]}
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> formatForPlotting(VolatilitySurfaceData surface,
                                                SortedSet xVals,
                                                SortedSet yVals,
                                                Map<String, Object> baseResults) {
    Map<String, Object> results = Maps.newHashMap(baseResults);
    // the x and y values won't necessarily be unique and won't necessarily map to a rectangular grid
    // this projects them onto a grid and inserts nulls where there's no data available
    // numeric values corresponding to the axis labels to help with plotting the surface
    List<Object> xAxisValues = Lists.newArrayListWithCapacity(xVals.size());
    List<Object> yAxisValues = Lists.newArrayListWithCapacity(yVals.size());
    List<Double> vol = Lists.newArrayListWithCapacity(xVals.size() * yVals.size());
    for (Object yVal : yVals) {
      for (Object xVal : xVals) {
        vol.add(surface.getVolatility(xVal, yVal));
      }
      CollectionUtils.addIgnoreNull(yAxisValues, getAxisValue(yVal));
    }
    for (Object xVal : xVals) {
      CollectionUtils.addIgnoreNull(xAxisValues, getAxisValue(xVal));
    }
    // not all volatility surfaces can be sensibly plotted. if a value can't be meaningfully converted to a number
    // then it will be null and won't be added to the collection. so if the collection is empty it isn't added to the
    // results and a plot won't be generated
    if (!yAxisValues.isEmpty()) {
      results.put("y_values", yAxisValues);
    }
    if (!xAxisValues.isEmpty()) {
      results.put("x_values", xAxisValues);
    }
    results.put("x_title", surface.getXLabel());
    results.put("y_title", surface.getYLabel());
    results.put("vol", vol);
    return results;
  }

  private List<String> getAxisLabels(Collection values) {
    List<String> labels = Lists.newArrayListWithCapacity(values.size());
    for (Object value : values) {
      labels.add(LabelFormatter.format(value));
    }
    return labels;
  }

  /**
   * Returns a numeric value corresponding to a point on volatility surface axis to help with plotting the surface.
   * @param axisValue A point on the axis
   * @return A numeric value corresponding to the value or null if there's no meaningful numeric value
   */
  private Object getAxisValue(Object axisValue) {
    if (axisValue instanceof Number) {
      return axisValue;
    } else if (axisValue instanceof LocalDate) {
      return ((LocalDate) axisValue).toEpochDays();
    } else if (axisValue instanceof Tenor) {
      return ((Tenor) axisValue).getPeriod().toEstimatedDuration().toSeconds();
    }
    return null;
  }

  /**
   * Returns {@link DataType#UNKNOWN UNKNOWN} because the format type can be differ for different instances of
   * {@link VolatilitySurfaceData} depending on the axis types. The type for a given surface instance can
   * be obtained from {@link #getDataTypeForValue}
   * @return {@link DataType#UNKNOWN}
   */
  @Override
  public DataType getDataType() {
    return DataType.UNKNOWN;
  }

  /**
   * If the axis values can be sensibly converted to numbers this returns {@link DataType#SURFACE_DATA}, if not
   * it returns {@link DataType#UNPLOTTABLE_SURFACE_DATA}.
   * @param surfaceData The surface data
   * @return The format type for the surface data, {@link DataType#SURFACE_DATA} or 
   * {@link DataType#UNPLOTTABLE_SURFACE_DATA} depending on the axis types of the data
   */
  @Override
  public DataType getDataTypeForValue(VolatilitySurfaceData surfaceData) {
    if (isPlottable(surfaceData)) {
      return DataType.SURFACE_DATA;
    } else {
      return DataType.UNPLOTTABLE_SURFACE_DATA;
    }
  }

  /**
   * Returns {@code true} if the surface data can be sensibly plotted.
   * @param surfaceData The surface data
   * @return {@code true} if the data can be sensibly plotted
   */
  private boolean isPlottable(VolatilitySurfaceData surfaceData) {
    Object[] xVals = surfaceData.getXs();
    Object[] yVals = surfaceData.getYs();

    if (xVals.length == 0) {
      return false;
    }
    if (yVals.length == 0) {
      return false;
    }
    if (getAxisValue(xVals[0]) == null || getAxisValue(yVals[0]) == null) {
      return false;
    }
    return true;
  }
}