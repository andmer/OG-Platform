/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.equity.varianceswap;

import java.util.Collections;
import java.util.Set;

import javax.time.calendar.Clock;
import javax.time.calendar.ZonedDateTime;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.analytics.financial.equity.StaticReplicationDataBundle;
import com.opengamma.analytics.financial.instrument.varianceswap.VarianceSwapDefinition;
import com.opengamma.analytics.financial.model.interestrate.curve.ForwardCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.financial.model.volatility.surface.BlackVolatilitySurface;
import com.opengamma.analytics.financial.model.volatility.surface.BlackVolatilitySurfaceStrike;
import com.opengamma.analytics.financial.model.volatility.surface.VolatilitySurface;
import com.opengamma.analytics.financial.varianceswap.VarianceSwap;
import com.opengamma.analytics.util.time.TimeCalculator;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.id.ExternalSchemes;
import com.opengamma.core.value.MarketDataRequirementNames;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.AbstractFunction;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionExecutionContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.value.ComputedValue;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.financial.OpenGammaCompilationContext;
import com.opengamma.financial.analytics.conversion.EquityVarianceSwapConverter;
import com.opengamma.financial.analytics.model.InstrumentTypeProperties;
import com.opengamma.financial.analytics.timeseries.DateConstraint;
import com.opengamma.financial.analytics.timeseries.HistoricalTimeSeriesFunctionUtils;
import com.opengamma.financial.security.equity.EquityVarianceSwapSecurity;
import com.opengamma.id.ExternalId;
import com.opengamma.id.UniqueId;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesResolutionResult;
import com.opengamma.master.historicaltimeseries.HistoricalTimeSeriesResolver;
import com.opengamma.util.ArgumentChecker;

/**
 * Base class for Functions for EquityVarianceSwapSecurity. These functions price using Static Replication
 */
public abstract class EquityVarianceSwapStaticReplicationFunction extends AbstractFunction.NonCompiledInvoker {
  private final String _valueRequirementName;
  private EquityVarianceSwapConverter _converter;

  /** CalculationMethod constraint used in configuration to choose this model */
  public static final String CALCULATION_METHOD = "StaticReplication";
  /** Method may be Strike or Moneyness TODO Confirm */
  public static final String STRIKE_PARAMETERIZATION_METHOD = "StrikeParameterizationMethod";

  public EquityVarianceSwapStaticReplicationFunction(final String valueRequirementName) {
    ArgumentChecker.notNull(valueRequirementName, "value requirement name");
    _valueRequirementName = valueRequirementName;
  }

  @Override
  public Set<ComputedValue> execute(final FunctionExecutionContext executionContext, final FunctionInputs inputs, final ComputationTarget target, final Set<ValueRequirement> desiredValues) {
    final ValueRequirement desiredValue = desiredValues.iterator().next();
    final String curveName = desiredValue.getConstraint(ValuePropertyNames.CURVE);
    final String curveCalculationConfig = desiredValue.getConstraint(ValuePropertyNames.CURVE_CALCULATION_CONFIG);
    final String surfaceName = desiredValue.getConstraint(ValuePropertyNames.SURFACE);
    // 1. Build the analytic derivative to be priced
    final EquityVarianceSwapSecurity security = (EquityVarianceSwapSecurity) target.getSecurity();

    final Clock snapshotClock = executionContext.getValuationClock();
    final ZonedDateTime now = snapshotClock.zonedDateTime().minusYears(2); //TODO remove me - just for testing

    final VarianceSwapDefinition defn = security.accept(_converter);
    final HistoricalTimeSeries timeSeries = (HistoricalTimeSeries) inputs.getValue(ValueRequirementNames.HISTORICAL_TIME_SERIES);
    final VarianceSwap deriv = defn.toDerivative(now, timeSeries.getTimeSeries());

    // 2. Build up the market data bundle
    final Object volSurfaceObject = inputs.getValue(getVolatilitySurfaceRequirement(security, surfaceName));
    if (volSurfaceObject == null) {
      throw new OpenGammaRuntimeException("Could not get Volatility Surface");
    }
    final VolatilitySurface volSurface = (VolatilitySurface) volSurfaceObject;
    //TODO no choice of other surfaces
    final BlackVolatilitySurface<?> blackVolSurf = new BlackVolatilitySurfaceStrike(volSurface.getSurface());

    final Object discountObject = inputs.getValue(getDiscountCurveRequirement(security, curveName, curveCalculationConfig));
    if (discountObject == null) {
      throw new OpenGammaRuntimeException("Could not get Discount Curve");
    }
    if (!(discountObject instanceof YieldCurve)) { //TODO: make it more generic
      throw new IllegalArgumentException("Can only handle YieldCurve");
    }
    final YieldCurve discountCurve = (YieldCurve) discountObject;

    final Object spotObject = inputs.getValue(getSpotRequirement(security));
    if (spotObject == null) {
      throw new OpenGammaRuntimeException("Could not get Underlying's Spot value");
    }
    final double spot = (Double) spotObject;

    final double expiry = TimeCalculator.getTimeBetween(executionContext.getValuationClock().zonedDateTime(), security.getLastObservationDate());
    final double discountFactor = discountCurve.getDiscountFactor(expiry);
    ArgumentChecker.isTrue(Double.doubleToLongBits(discountFactor) != 0, "The discount curve has returned a zero value for a discount bond. Check rates.");
    final ForwardCurve forwardCurve = new ForwardCurve(spot, discountCurve.getCurve()); //TODO change this

    final StaticReplicationDataBundle market = new StaticReplicationDataBundle(blackVolSurf, discountCurve, forwardCurve);
    final ValueSpecification resultSpec = getValueSpecification(target, curveName, curveCalculationConfig, surfaceName);
    // 3. Compute and return the value (ComputedValue)
    return computeValues(resultSpec, inputs, deriv, market);
  }

  protected abstract Set<ComputedValue> computeValues(final ValueSpecification resultSpec, final FunctionInputs inputs, final VarianceSwap derivative, final StaticReplicationDataBundle market);

  protected ValueSpecification getValueSpecification(final ComputationTarget target) {
    final ValueProperties properties = getValueProperties(target);
    return new ValueSpecification(_valueRequirementName, target.toSpecification(), properties);
  }

  protected ValueSpecification getValueSpecification(final ComputationTarget target, final String curveName, final String curveCalculationConfig, final String surfaceName) {
    final ValueProperties properties = getValueProperties(target, curveName, curveCalculationConfig, surfaceName);
    return new ValueSpecification(_valueRequirementName, target.toSpecification(), properties);
  }

  protected ValueProperties getValueProperties(final ComputationTarget target) {
    final EquityVarianceSwapSecurity security = (EquityVarianceSwapSecurity) target.getSecurity();
    return createValueProperties()
        .with(ValuePropertyNames.CURRENCY, security.getCurrency().getCode())
        .with(ValuePropertyNames.CALCULATION_METHOD, CALCULATION_METHOD)
        .withAny(ValuePropertyNames.CURVE)
        .withAny(ValuePropertyNames.CURVE_CALCULATION_CONFIG)
        .withAny(ValuePropertyNames.SURFACE).get();
  }

  protected ValueProperties getValueProperties(final ComputationTarget target, final String curveName, final String curveCalculationConfig, final String surfaceName) {
    final EquityVarianceSwapSecurity security = (EquityVarianceSwapSecurity) target.getSecurity();
    return createValueProperties()
        .with(ValuePropertyNames.CURRENCY, security.getCurrency().getCode())
        .with(ValuePropertyNames.CALCULATION_METHOD, CALCULATION_METHOD)
        .with(ValuePropertyNames.CURVE, curveName)
        .with(ValuePropertyNames.CURVE_CALCULATION_CONFIG, curveCalculationConfig)
        .with(ValuePropertyNames.SURFACE, surfaceName).get();
  }

  private ValueRequirement getSpotRequirement(final EquityVarianceSwapSecurity security) {
    final ExternalId id = security.getSpotUnderlyingId();
    return new ValueRequirement(MarketDataRequirementNames.MARKET_VALUE, ComputationTargetType.PRIMITIVE, UniqueId.of(id.getScheme().getName(), id.getValue()));
  }

  // Note that createValueProperties is _not_ used - use will mean the engine can't find the requirement
  private ValueRequirement getDiscountCurveRequirement(final EquityVarianceSwapSecurity security, final String curveName, final String curveCalculationConfig) {
    final ValueProperties properties = ValueProperties.builder()
        .with(ValuePropertyNames.CURVE, curveName)
        .with(ValuePropertyNames.CURVE_CALCULATION_CONFIG, curveCalculationConfig).get();
    return new ValueRequirement(ValueRequirementNames.YIELD_CURVE, ComputationTargetType.PRIMITIVE, security.getCurrency().getUniqueId(), properties);
  }

  private ValueRequirement getVolatilitySurfaceRequirement(final EquityVarianceSwapSecurity security, final String surfaceName) {
    final ValueProperties properties = ValueProperties.builder().with(ValuePropertyNames.SURFACE, surfaceName)
        .with(InstrumentTypeProperties.PROPERTY_SURFACE_INSTRUMENT_TYPE, InstrumentTypeProperties.EQUITY_OPTION)
        .get();
    final ExternalId id = security.getSpotUnderlyingId();
    final UniqueId newId = id.getScheme().equals(ExternalSchemes.BLOOMBERG_TICKER) ? UniqueId.of(ExternalSchemes.BLOOMBERG_TICKER_WEAK.getName(), id.getValue()) :
        UniqueId.of(id.getScheme().getName(), id.getValue());
    return new ValueRequirement(ValueRequirementNames.INTERPOLATED_VOLATILITY_SURFACE, ComputationTargetType.PRIMITIVE, newId, properties);
  }

  private ValueRequirement getTimeSeriesRequirement(final FunctionCompilationContext context, final EquityVarianceSwapSecurity security) {
    final HistoricalTimeSeriesResolver resolver = OpenGammaCompilationContext.getHistoricalTimeSeriesResolver(context);
    final HistoricalTimeSeriesResolutionResult timeSeries = resolver.resolve(security.getSpotUnderlyingId().toBundle(), null, null, null, MarketDataRequirementNames.MARKET_VALUE, null);
    if (timeSeries == null) {
      return null;
    }
    return HistoricalTimeSeriesFunctionUtils.createHTSRequirement(timeSeries, MarketDataRequirementNames.MARKET_VALUE, DateConstraint.EARLIEST_START, true, DateConstraint.VALUATION_TIME, true);
  }

  @Override
  public void init(final FunctionCompilationContext context) {
    final HolidaySource holidaySource = OpenGammaCompilationContext.getHolidaySource(context);
    _converter = new EquityVarianceSwapConverter(holidaySource);

  }

  @Override
  public ComputationTargetType getTargetType() {
    return ComputationTargetType.SECURITY;
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    return target.getSecurity() instanceof EquityVarianceSwapSecurity;
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    final ValueProperties constraints = desiredValue.getConstraints();
    final Set<String> curveNames = constraints.getValues(ValuePropertyNames.CURVE);
    if (curveNames == null || curveNames.size() != 1) {
      return null;
    }
    final Set<String> curveCalculationConfigs = constraints.getValues(ValuePropertyNames.CURVE_CALCULATION_CONFIG);
    if (curveCalculationConfigs == null || curveCalculationConfigs.size() != 1) {
      return null;
    }
    final Set<String> surfaceNames = constraints.getValues(ValuePropertyNames.SURFACE);
    if (surfaceNames == null || surfaceNames.size() != 1) {
      return null;
    }
    final String curveName = Iterables.getOnlyElement(curveNames);
    final String curveCalculationConfig = Iterables.getOnlyElement(curveCalculationConfigs);
    final String surfaceName = Iterables.getOnlyElement(surfaceNames);
    final EquityVarianceSwapSecurity security = (EquityVarianceSwapSecurity) target.getSecurity();
    final Set<ValueRequirement> requirements = Sets.newHashSetWithExpectedSize(4);
    requirements.add(getDiscountCurveRequirement(security, curveName, curveCalculationConfig));
    requirements.add(getSpotRequirement(security));
    requirements.add(getVolatilitySurfaceRequirement(security, surfaceName));
    final ValueRequirement requirement = getTimeSeriesRequirement(context, security);
    if (requirement == null) {
      return null;
    }
    requirements.add(requirement);
    return requirements;
  }

  @Override
  public Set<ValueSpecification> getResults(final FunctionCompilationContext context, final ComputationTarget target) {
    return Collections.singleton(getValueSpecification(target));
  }
}
