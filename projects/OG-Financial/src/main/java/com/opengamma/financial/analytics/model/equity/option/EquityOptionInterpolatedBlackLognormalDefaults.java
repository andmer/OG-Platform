/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.equity.option;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.OpenGammaFunctionExclusions;
import com.opengamma.financial.analytics.model.curve.forward.ForwardCurveValuePropertyNames;
import com.opengamma.financial.analytics.model.volatility.surface.black.BlackVolatilitySurfacePropertyNamesAndValues;
import com.opengamma.financial.property.DefaultPropertyFunction;
import com.opengamma.financial.security.FinancialSecurityTypes;
import com.opengamma.util.ArgumentChecker;

/**
 * Populates {@link EquityOptionFunction}, including {@link EquityVanillaBarrierOptionBlackFunction}, with defaults appropriate
 * for pricing using an interpolated Black lognormal volatility surface.
 */
public abstract class EquityOptionInterpolatedBlackLognormalDefaults extends DefaultPropertyFunction {
  /** The logger */
  private static final Logger s_logger = LoggerFactory.getLogger(EquityOptionInterpolatedBlackLognormalDefaults.class);
  /** Map of id name to discounting curve configuration */
  private final Map<String, String> _idToDiscountingCurveConfig;
  /** Map of id name to discounting curve name */
  private final Map<String, String> _idToDiscountingCurveName;
  /** Map of id name to volatility surface name */
  private final Map<String, String> _idToSurfaceName;
  /** Map of id name to volatility surface calculation method name */
  private final Map<String, String> _idToSurfaceInterpolatorName;
  /** Map of id name to forward curve name */
  private final Map<String, String> _idToForwardCurveName;
  /** Map of id name to forward curve calculation method name */
  private final Map<String, String> _idToForwardCurveCalculationMethodName;
  /** The priority of this set of defaults */
  private final PriorityClass _priority;

  /** The value requirement names for which these defaults apply */
  private static final String[] s_valueNames = new String[] {
    ValueRequirementNames.PRESENT_VALUE,
    ValueRequirementNames.VEGA_QUOTE_MATRIX,
    ValueRequirementNames.VALUE_VEGA,
    ValueRequirementNames.IMPLIED_VOLATILITY,
    ValueRequirementNames.YIELD_CURVE_NODE_SENSITIVITIES,
    ValueRequirementNames.FORWARD,
    ValueRequirementNames.SPOT,
    ValueRequirementNames.VALUE_DELTA,
    ValueRequirementNames.VALUE_GAMMA,
    ValueRequirementNames.VALUE_VOMMA,
    ValueRequirementNames.VALUE_VANNA,
    ValueRequirementNames.VALUE_RHO,
    ValueRequirementNames.VALUE_CARRY_RHO,
    ValueRequirementNames.VALUE_THETA,
    ValueRequirementNames.VALUE_DUAL_DELTA
  };

  /**
   * @param priority The priority class of {@link DefaultPropertyFunction} instances, allowing them to be ordered relative to each other, not null
   * @param perIdConfig Defaults values of curve configuration, discounting curve, surface name and interpolation method per id, not null
   */
  public EquityOptionInterpolatedBlackLognormalDefaults(final String priority, final String... perIdConfig) {
    super(FinancialSecurityTypes.EQUITY_INDEX_OPTION_SECURITY.or(FinancialSecurityTypes.EQUITY_BARRIER_OPTION_SECURITY).or(FinancialSecurityTypes.EQUITY_OPTION_SECURITY), true);
    ArgumentChecker.notNull(priority, "priority");
    ArgumentChecker.notNull(perIdConfig, "per id configuration");
    _priority = PriorityClass.valueOf(priority);

    final int nPairs = perIdConfig.length;
    ArgumentChecker.isTrue(nPairs % 7 == 0, "Must have discounting name, discounting curve config, surface name, surface interpolation method, forward curve name" +
      "and forward curve calculation method per id");
    _idToDiscountingCurveName = Maps.newHashMap();
    _idToDiscountingCurveConfig = Maps.newHashMap();
    _idToSurfaceName = Maps.newHashMap();
    _idToSurfaceInterpolatorName = Maps.newHashMap();
    _idToForwardCurveName = Maps.newHashMap();
    _idToForwardCurveCalculationMethodName = Maps.newHashMap();
    for (int i = 0; i < perIdConfig.length; i += 7) {
      final String id = perIdConfig[i].toUpperCase();
      _idToDiscountingCurveName.put(id, perIdConfig[i + 1]);
      _idToDiscountingCurveConfig.put(id, perIdConfig[i + 2]);
      _idToSurfaceName.put(id, perIdConfig[i + 3]);
      _idToSurfaceInterpolatorName.put(id, perIdConfig[i + 4]);
      _idToForwardCurveName.put(id, perIdConfig[i + 5]);
      _idToForwardCurveCalculationMethodName.put(id, perIdConfig[i + 6]);
    }
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    final Security eqSec = target.getSecurity();
    final String id = getId(eqSec);
    return _idToDiscountingCurveName.containsKey(id);
  }

  @Override
  protected void getDefaults(final PropertyDefaults defaults) {
    for (final String valueName : s_valueNames) {
      defaults.addValuePropertyName(valueName, EquityOptionFunction.PROPERTY_DISCOUNTING_CURVE_CONFIG);
      defaults.addValuePropertyName(valueName, EquityOptionFunction.PROPERTY_DISCOUNTING_CURVE_NAME);
      defaults.addValuePropertyName(valueName, EquityOptionFunction.PROPERTY_FORWARD_CURVE_NAME);
      defaults.addValuePropertyName(valueName, ForwardCurveValuePropertyNames.PROPERTY_FORWARD_CURVE_CALCULATION_METHOD);
      defaults.addValuePropertyName(valueName, ValuePropertyNames.SURFACE);
      defaults.addValuePropertyName(valueName, BlackVolatilitySurfacePropertyNamesAndValues.PROPERTY_SMILE_INTERPOLATOR);
    }
  }

  @Override
  protected Set<String> getDefaultValue(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue, final String propertyName) {
    final Set<String> surfaceCalculationMethod = desiredValue.getConstraints().getValues(ValuePropertyNames.SURFACE_CALCULATION_METHOD);
    if (surfaceCalculationMethod != null && surfaceCalculationMethod.size() == 1) {
      if (!Iterables.getOnlyElement(surfaceCalculationMethod).equals(BlackVolatilitySurfacePropertyNamesAndValues.INTERPOLATED_BLACK_LOGNORMAL)) {
        return null;
      }
    }
    final String id = getId(target.getSecurity());
    if (!_idToDiscountingCurveConfig.containsKey(id)) {
      s_logger.error("Could not find defaults for {}", id);
      return null;
    }
    if (EquityOptionFunction.PROPERTY_DISCOUNTING_CURVE_CONFIG.equals(propertyName)) {
      return Collections.singleton(_idToDiscountingCurveConfig.get(id));
    }
    if (EquityOptionFunction.PROPERTY_DISCOUNTING_CURVE_NAME.equals(propertyName)) {
      return Collections.singleton(_idToDiscountingCurveName.get(id));
    }
    if (EquityOptionFunction.PROPERTY_FORWARD_CURVE_NAME.equals(propertyName)) {
      return Collections.singleton(_idToForwardCurveName.get(id));
    }
    if (ForwardCurveValuePropertyNames.PROPERTY_FORWARD_CURVE_CALCULATION_METHOD.equals(propertyName)) {
      return Collections.singleton(_idToForwardCurveCalculationMethodName.get(id));
    }
    if (ValuePropertyNames.SURFACE.equals(propertyName)) {
      return Collections.singleton(_idToSurfaceName.get(id));
    }
    if (BlackVolatilitySurfacePropertyNamesAndValues.PROPERTY_SMILE_INTERPOLATOR.equals(propertyName)) {
      return Collections.singleton(_idToSurfaceInterpolatorName.get(id));
    }
    s_logger.error("Cannot get a default value for {}", propertyName);
    return null;
  }

  @Override
  public PriorityClass getPriority() {
    return _priority;
  }

  @Override
  public String getMutualExclusionGroup() {
    return OpenGammaFunctionExclusions.EQUITY_OPTION_INTERPOLATED_BLACK_LOGNORMAL_DEFAULTS;
  }

  /**
   * @return All ids for which a default is available
   */
  protected Set<String> getAllIds() {
    return _idToDiscountingCurveConfig.keySet();
  }

  /**
   * @param security The security
   * @return The id for the security
   */
  protected abstract String getId(Security security);
}