/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.analytics;

import java.util.Collection;
import java.util.Collections;

import com.opengamma.engine.position.Position;

/**
 * A meta-function which can be put into any dependency graph to indicate
 * that a particular output is going to be sourced from the live data repository. 
 *
 * @author kirk
 */
public class LiveDataSourcingFunction implements AnalyticFunction {
  private final AnalyticValueDefinition _specifiedResult;
  private final String _shortName;
  
  public LiveDataSourcingFunction(AnalyticValueDefinition specifiedResult) {
    if(specifiedResult == null) {
      throw new NullPointerException("Must specify the desired live data.");
    }
    _specifiedResult = specifiedResult;
    _shortName = "Live Data Source For " + specifiedResult;
  }

  /**
   * @return the specifiedResult
   */
  public AnalyticValueDefinition getSpecifiedResult() {
    return _specifiedResult;
  }

  @Override
  public Collection<AnalyticValueDefinition> getInputs() {
    return Collections.emptySet();
  }

  @Override
  public Collection<AnalyticValueDefinition> getPossibleResults() {
    return Collections.singleton(_specifiedResult);
  }

  @Override
  public String getShortName() {
    return _shortName;
  }

  @Override
  public boolean isApplicableTo(String securityType) {
    return true;
  }

  @Override
  public boolean isApplicableTo(Position position) {
    return true;
  }

}
