/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.value;

import com.opengamma.engine.ComputationTargetType;

/**
 *
 */
public class ForwardPriceTradeRenamingFunction extends ForwardPriceRenamingFunction {

  /**
   * @param targetType
   */
  public ForwardPriceTradeRenamingFunction() {
    super(ComputationTargetType.TRADE);
  }

}
