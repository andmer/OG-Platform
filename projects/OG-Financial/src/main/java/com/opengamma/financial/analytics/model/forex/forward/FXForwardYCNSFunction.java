/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.forex.forward;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.analytics.financial.calculator.MarketQuoteSensitivityCalculator;
import com.opengamma.analytics.financial.curve.interestrate.sensitivity.ParameterSensitivityCalculator;
import com.opengamma.analytics.financial.forex.derivative.Forex;
import com.opengamma.analytics.financial.forex.method.MultipleCurrencyInterestRateCurveSensitivity;
import com.opengamma.analytics.financial.interestrate.PresentValueCurveSensitivityIRSCalculator;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.core.config.ConfigSource;
import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetSpecification;
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
import com.opengamma.financial.OpenGammaExecutionContext;
import com.opengamma.financial.analytics.ircurve.InterpolatedYieldCurveSpecificationWithSecurities;
import com.opengamma.financial.analytics.ircurve.calcconfig.ConfigDBCurveCalculationConfigSource;
import com.opengamma.financial.analytics.ircurve.calcconfig.MultiCurveCalculationConfig;
import com.opengamma.financial.analytics.model.FunctionUtils;
import com.opengamma.financial.analytics.model.YieldCurveFunctionUtils;
import com.opengamma.financial.analytics.model.YieldCurveNodeSensitivitiesHelper;
import com.opengamma.financial.analytics.model.curve.interestrate.FXImpliedYieldCurveFunction;
import com.opengamma.financial.analytics.model.curve.interestrate.MultiYieldCurvePropertiesAndDefaults;
import com.opengamma.financial.analytics.model.forex.ForexVisitors;
import com.opengamma.financial.currency.CurrencyPair;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.util.money.Currency;
import com.opengamma.util.tuple.DoublesPair;

/**
 *
 */
public class FXForwardYCNSFunction extends FXForwardSingleValuedFunction {
  private static final Logger s_logger = LoggerFactory.getLogger(FXForwardYCNSFunction.class);
  private static final MarketQuoteSensitivityCalculator CALCULATOR =
      new MarketQuoteSensitivityCalculator(new ParameterSensitivityCalculator(PresentValueCurveSensitivityIRSCalculator.getInstance()));

  public FXForwardYCNSFunction() {
    super(ValueRequirementNames.YIELD_CURVE_NODE_SENSITIVITIES);
  }

  @Override
  protected Set<ComputedValue> getResult(final Forex fxForward, final YieldCurveBundle data, final ComputationTarget target, final Set<ValueRequirement> desiredValues,
      final FunctionInputs inputs, final ValueSpecification spec, final FunctionExecutionContext executionContext) {
    final FXForwardSecurity security = (FXForwardSecurity) target.getSecurity();
    final ValueRequirement desiredValue = Iterables.getOnlyElement(desiredValues);
    final String payCurveName = desiredValue.getConstraint(ValuePropertyNames.PAY_CURVE);
    final String receiveCurveName = desiredValue.getConstraint(ValuePropertyNames.RECEIVE_CURVE);
    final String payCurveCalculationConfigName = desiredValue.getConstraint(ValuePropertyNames.PAY_CURVE_CALCULATION_CONFIG);
    final String receiveCurveCalculationConfigName = desiredValue.getConstraint(ValuePropertyNames.RECEIVE_CURVE_CALCULATION_CONFIG);
    final String curveCurrency = desiredValue.getConstraint(ValuePropertyNames.CURVE_CURRENCY);
    final String curveName = desiredValue.getConstraint(ValuePropertyNames.CURVE);
    final Object curveSensitivitiesObject = inputs.getValue(ValueRequirementNames.FX_CURVE_SENSITIVITIES);
    final String resultCurveConfigName;
    if (!(curveName.equals(payCurveName) || curveName.equals(receiveCurveName))) {
      s_logger.error("Curve name {} did not match either pay curve name {} or receive curve name {}", new Object[] {curveName, payCurveName, receiveCurveName});
      return null;
    }
    final String payCurrency = security.getPayCurrency().getCode();
    if (curveCurrency.equals(payCurrency)) {
      resultCurveConfigName = payCurveCalculationConfigName;
    } else {
      resultCurveConfigName = receiveCurveCalculationConfigName;
    }
    if (curveSensitivitiesObject == null) {
      throw new OpenGammaRuntimeException("Could not get curve sensitivities");
    }
    final Object curveSpecObject = inputs.getValue(ValueRequirementNames.YIELD_CURVE_SPEC);
    if (curveSpecObject == null) {
      throw new OpenGammaRuntimeException("Could not get yield curve spec");
    }
    final ConfigSource configSource = OpenGammaExecutionContext.getConfigSource(executionContext);
    final ConfigDBCurveCalculationConfigSource curveCalculationConfigSource = new ConfigDBCurveCalculationConfigSource(configSource);
    final MultiCurveCalculationConfig resultCurveCalculationConfig = curveCalculationConfigSource.getConfig(resultCurveConfigName);
    final String calculationMethod = resultCurveCalculationConfig.getCalculationMethod();
    final String fullCurveName = curveName + "_" + curveCurrency;
    final InterpolatedYieldCurveSpecificationWithSecurities curveSpec = (InterpolatedYieldCurveSpecificationWithSecurities) curveSpecObject;
    final MultipleCurrencyInterestRateCurveSensitivity curveSensitivities = (MultipleCurrencyInterestRateCurveSensitivity) curveSensitivitiesObject;
    final Map<String, List<DoublesPair>> sensitivitiesForCurrency = curveSensitivities.getSensitivity(Currency.of(curveCurrency)).getSensitivities();
    final YieldCurveBundle dataForCurrency = new YieldCurveBundle();
    dataForCurrency.setCurve(fullCurveName, data.getCurve(fullCurveName));
    return getResult(inputs, resultCurveConfigName, calculationMethod, fullCurveName, dataForCurrency, curveSpec, sensitivitiesForCurrency, spec);
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue) {
    final ValueProperties constraints = desiredValue.getConstraints();
    final Set<String> curveNames = constraints.getValues(ValuePropertyNames.CURVE);
    if (curveNames == null || curveNames.size() != 1) {
      s_logger.error("Did not specify a curve name for requirement {}", desiredValue);
      return null;
    }
    final Set<String> currencies = constraints.getValues(ValuePropertyNames.CURVE_CURRENCY);
    if (currencies == null || currencies.size() != 1) {
      s_logger.error("Did not specify a currency for requirement {}", desiredValue);
      return null;
    }
    final Set<String> payCurveNames = constraints.getValues(ValuePropertyNames.PAY_CURVE);
    if (payCurveNames == null || payCurveNames.size() != 1) {
      return null;
    }
    final Set<String> receiveCurveNames = constraints.getValues(ValuePropertyNames.RECEIVE_CURVE);
    if (receiveCurveNames == null || receiveCurveNames.size() != 1) {
      return null;
    }
    final Set<String> payCurveConfigNames = constraints.getValues(ValuePropertyNames.PAY_CURVE_CALCULATION_CONFIG);
    if (payCurveConfigNames == null || payCurveConfigNames.size() != 1) {
      return null;
    }
    final Set<String> receiveCurveConfigNames = constraints.getValues(ValuePropertyNames.RECEIVE_CURVE_CALCULATION_CONFIG);
    if (receiveCurveConfigNames == null || receiveCurveConfigNames.size() != 1) {
      return null;
    }
    final String payCurveName = payCurveNames.iterator().next();
    final String receiveCurveName = receiveCurveNames.iterator().next();
    final String payCurveCalculationConfig = payCurveConfigNames.iterator().next();
    final String receiveCurveCalculationConfig = receiveCurveConfigNames.iterator().next();
    final String currency = currencies.iterator().next();
    final String curveName = curveNames.iterator().next();
    final FinancialSecurity security = (FinancialSecurity) target.getSecurity();
    final Currency payCurrency = security.accept(ForexVisitors.getPayCurrencyVisitor());
    final Currency receiveCurrency = security.accept(ForexVisitors.getReceiveCurrencyVisitor());
    final ValueRequirement payFundingCurve = YieldCurveFunctionUtils.getCurveRequirementForFXForward(ComputationTargetSpecification.of(payCurrency), payCurveName, payCurveCalculationConfig, true);
    final ValueRequirement receiveFundingCurve = YieldCurveFunctionUtils.getCurveRequirementForFXForward(ComputationTargetSpecification.of(receiveCurrency),
        receiveCurveName, receiveCurveCalculationConfig, false);
    final String resultCurrency, resultCurveName, resultCurveConfigName;
    if (!(curveName.equals(payCurveName) || curveName.equals(receiveCurveName))) {
      s_logger.info("Curve name {} did not match either pay curve name {} or receive curve name {}", new Object[] {curveName, payCurveName, receiveCurveName});
      return null;
    }
    if (currency.equals(payCurrency.getCode())) {
      resultCurrency = payCurrency.getCode();
      resultCurveName = payCurveName;
      resultCurveConfigName = payCurveCalculationConfig;
    } else if (currency.equals(receiveCurrency.getCode())) {
      resultCurrency = receiveCurrency.getCode();
      resultCurveName = receiveCurveName;
      resultCurveConfigName = receiveCurveCalculationConfig;
    } else {
      return null;
    }
    final Set<ValueRequirement> requirements = new HashSet<>();
    requirements.add(payFundingCurve);
    requirements.add(receiveFundingCurve);
    final ConfigSource configSource = OpenGammaCompilationContext.getConfigSource(context);
    final ConfigDBCurveCalculationConfigSource curveCalculationConfigSource = new ConfigDBCurveCalculationConfigSource(configSource);
    final MultiCurveCalculationConfig resultCurveCalculationConfig = curveCalculationConfigSource.getConfig(resultCurveConfigName);
    if (resultCurveCalculationConfig == null) {
      s_logger.error("Could not find curve calculation configuration named " + resultCurveConfigName + " for currency " + resultCurrency);
      return null;
    }
    final String resultCurveCalculationMethod = resultCurveCalculationConfig.getCalculationMethod();
    requirements.add(getCurveSensitivitiesRequirement(payCurveName, payCurveCalculationConfig, receiveCurveName, receiveCurveCalculationConfig, target));
    if (resultCurveCalculationMethod.equals(FXImpliedYieldCurveFunction.FX_IMPLIED)) {
      s_logger.error("Cannot handle curves calculated using the FX implied method");
      return null;
    }
    requirements.add(getCurveSpecRequirement(resultCurrency, resultCurveName));
    requirements.add(getJacobianRequirement(Currency.of(resultCurrency), resultCurveConfigName, resultCurveCalculationConfig.getCalculationMethod()));
    if (resultCurveCalculationMethod.equals(MultiYieldCurvePropertiesAndDefaults.PRESENT_VALUE_STRING)) {
      requirements.add(getCouponSensitivitiesRequirement(Currency.of(resultCurrency), resultCurveConfigName));
    }
    requirements.add(new ValueRequirement(ValueRequirementNames.CURRENCY_PAIRS, ComputationTargetSpecification.NULL));
    return requirements;
  }

  @Override
  protected ValueProperties.Builder getResultProperties(final ComputationTarget target) {
    final ValueProperties.Builder properties = super.getResultProperties(target)
        .withAny(ValuePropertyNames.CURVE_CURRENCY)
        .withAny(ValuePropertyNames.CURVE);
    return properties;
  }

  @Override
  protected ValueProperties.Builder getResultProperties(final ComputationTarget target, final String payCurve, final String payCurveCalculationConfig,
      final String receiveCurve, final String receiveCurveCalculationConfig, final CurrencyPair baseQuotePair) {
    final ValueProperties.Builder properties = super.getResultProperties(target, payCurve, receiveCurve, payCurveCalculationConfig, receiveCurveCalculationConfig, baseQuotePair)
        .withAny(ValuePropertyNames.CURVE_CURRENCY)
        .withAny(ValuePropertyNames.CURVE);
    return properties;
  }

  @Override
  protected ValueProperties.Builder getResultProperties(final ComputationTarget target, final ValueRequirement desiredValue) {
    final String curveName = desiredValue.getConstraint(ValuePropertyNames.CURVE);
    final String currency = desiredValue.getConstraint(ValuePropertyNames.CURVE_CURRENCY);
    final ValueProperties.Builder properties = super.getResultProperties(target, desiredValue)
        .with(ValuePropertyNames.CURVE_CURRENCY, currency)
        .with(ValuePropertyNames.CURVE, curveName);
    return properties;
  }

  private static ValueRequirement getCurveSpecRequirement(final String currency, final String curveName) {
    final ValueProperties properties = ValueProperties.builder()
        .with(ValuePropertyNames.CURVE, curveName).get();
    return new ValueRequirement(ValueRequirementNames.YIELD_CURVE_SPEC, ComputationTargetSpecification.of(Currency.of(currency)), properties);
  }

  private static ValueRequirement getCurveSensitivitiesRequirement(final String payCurveName, final String payCurveCalculationConfig, final String receiveCurveName,
      final String receiveCurveCalculationConfig, final ComputationTarget target) {
    final ValueProperties properties = ValueProperties.builder()
        .with(ValuePropertyNames.PAY_CURVE, payCurveName)
        .with(ValuePropertyNames.RECEIVE_CURVE, receiveCurveName)
        .with(ValuePropertyNames.PAY_CURVE_CALCULATION_CONFIG, payCurveCalculationConfig)
        .with(ValuePropertyNames.RECEIVE_CURVE_CALCULATION_CONFIG, receiveCurveCalculationConfig).get();
    return new ValueRequirement(ValueRequirementNames.FX_CURVE_SENSITIVITIES, target.toSpecification(), properties);
  }

  private static ValueRequirement getJacobianRequirement(final Currency currency, final String curveCalculationConfigName, final String curveCalculationMethod) {
    final ValueProperties properties = ValueProperties.builder()
        .with(ValuePropertyNames.CURVE_CALCULATION_CONFIG, curveCalculationConfigName)
        .with(ValuePropertyNames.CURVE_CALCULATION_METHOD, curveCalculationMethod).get();
    return new ValueRequirement(ValueRequirementNames.YIELD_CURVE_JACOBIAN, ComputationTargetSpecification.of(currency), properties);
  }

  private static ValueRequirement getCouponSensitivitiesRequirement(final Currency currency, final String curveCalculationConfigName) {
    final ValueProperties properties = ValueProperties.builder()
        .with(ValuePropertyNames.CURVE_CALCULATION_CONFIG, curveCalculationConfigName)
        .with(ValuePropertyNames.CURVE_CALCULATION_METHOD, MultiYieldCurvePropertiesAndDefaults.PRESENT_VALUE_STRING).get();
    return new ValueRequirement(ValueRequirementNames.PRESENT_VALUE_COUPON_SENSITIVITY, ComputationTargetSpecification.of(currency), properties);
  }

  private static Set<ComputedValue> getResult(final FunctionInputs inputs, final String curveCalculationConfig, final String calculationMethod,
      final String fullCurveName, final YieldCurveBundle interpolatedCurveForCurrency, final InterpolatedYieldCurveSpecificationWithSecurities curveSpec,
      final Map<String, List<DoublesPair>> sensitivitiesForCurrency, final ValueSpecification spec) {
    final Object jacobianObject = inputs.getValue(ValueRequirementNames.YIELD_CURVE_JACOBIAN);
    if (jacobianObject == null) {
      throw new OpenGammaRuntimeException("Could not get " + ValueRequirementNames.YIELD_CURVE_JACOBIAN);
    }
    final double[][] array = FunctionUtils.decodeJacobian(jacobianObject);
    final DoubleMatrix2D jacobian = new DoubleMatrix2D(array);
    if (calculationMethod.equals(MultiYieldCurvePropertiesAndDefaults.PAR_RATE_STRING)) {
      final DoubleMatrix1D result = CALCULATOR.calculateFromParRate(sensitivitiesForCurrency, interpolatedCurveForCurrency, jacobian);
      return YieldCurveNodeSensitivitiesHelper.getInstrumentLabelledSensitivitiesForCurve(fullCurveName, interpolatedCurveForCurrency, result, curveSpec, spec);
    }
    final Object couponSensitivityObject = inputs.getValue(ValueRequirementNames.PRESENT_VALUE_COUPON_SENSITIVITY);
    if (couponSensitivityObject == null) {
      throw new OpenGammaRuntimeException("Could not get " + ValueRequirementNames.PRESENT_VALUE_COUPON_SENSITIVITY);
    }
    final DoubleMatrix1D couponSensitivity = (DoubleMatrix1D) couponSensitivityObject;
    final DoubleMatrix1D result = CALCULATOR.calculateFromPresentValue(sensitivitiesForCurrency, interpolatedCurveForCurrency, couponSensitivity, jacobian);
    return YieldCurveNodeSensitivitiesHelper.getInstrumentLabelledSensitivitiesForCurve(fullCurveName, interpolatedCurveForCurrency, result, curveSpec, spec);
  }
}
