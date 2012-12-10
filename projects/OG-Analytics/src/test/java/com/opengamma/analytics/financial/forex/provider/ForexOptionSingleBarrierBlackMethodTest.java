/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.forex.provider;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.forex.definition.ForexDefinition;
import com.opengamma.analytics.financial.forex.definition.ForexOptionVanillaDefinition;
import com.opengamma.analytics.financial.forex.derivative.ForexOptionSingleBarrier;
import com.opengamma.analytics.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.analytics.financial.forex.method.FXMatrix;
import com.opengamma.analytics.financial.forex.method.PresentValueForexBlackVolatilitySensitivity;
import com.opengamma.analytics.financial.model.option.definition.Barrier;
import com.opengamma.analytics.financial.model.option.definition.Barrier.BarrierType;
import com.opengamma.analytics.financial.model.option.definition.Barrier.KnockType;
import com.opengamma.analytics.financial.model.option.definition.Barrier.ObservationType;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackBarrierPriceFunction;
import com.opengamma.analytics.financial.model.volatility.surface.SmileDeltaTermStructureParametersStrikeInterpolation;
import com.opengamma.analytics.financial.provider.calculator.blackforex.CurrencyExposureForexBlackSmileCalculator;
import com.opengamma.analytics.financial.provider.calculator.blackforex.PresentValueCurveSensitivityForexBlackSmileCalculator;
import com.opengamma.analytics.financial.provider.calculator.blackforex.PresentValueForexBlackSmileCalculator;
import com.opengamma.analytics.financial.provider.calculator.blackforex.PresentValueForexVolatilitySensitivityForexBlackSmileCalculator;
import com.opengamma.analytics.financial.provider.description.ForexBlackSmileProviderDiscount;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.sensitivity.blackforex.ParameterSensitivityForexBlackSmileCalculator;
import com.opengamma.analytics.financial.provider.sensitivity.blackforex.ParameterSensitivityForexBlackSmileDiscountInterpolatedFDCalculator;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyParameterSensitivity;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.analytics.financial.util.AssertSensivityObjects;
import com.opengamma.analytics.util.time.TimeCalculator;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.CurrencyAmount;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.tuple.DoublesPair;
import com.opengamma.util.tuple.ObjectsPair;
import com.opengamma.util.tuple.Pair;
import com.opengamma.util.tuple.Triple;

/**
 * Tests related to the Black world pricing method for single barrier Forex option.
 */
public class ForexOptionSingleBarrierBlackMethodTest {

  private static final MulticurveProviderDiscount MULTICURVES = MulticurveProviderDiscountForexDataSets.createMulticurvesForex();

  public static final String NOT_USED = "Not used";
  public static final String[] NOT_USED_2 = {NOT_USED, NOT_USED};

  private static final Currency EUR = Currency.EUR;
  private static final Currency USD = Currency.USD;
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2011, 6, 13);

  private static final SmileDeltaTermStructureParametersStrikeInterpolation SMILE_TERM = ForexSmileProviderDataSets.smile5points(REFERENCE_DATE);
  private static final SmileDeltaTermStructureParametersStrikeInterpolation SMILE_TERM_FLAT = ForexSmileProviderDataSets.smileFlat(REFERENCE_DATE);
  private static final ForexBlackSmileProviderDiscount SMILE_MULTICURVES = new ForexBlackSmileProviderDiscount(MULTICURVES, SMILE_TERM, Pair.of(EUR, USD));
  private static final ForexBlackSmileProviderDiscount SMILE_FLAT_MULTICURVES = new ForexBlackSmileProviderDiscount(MULTICURVES, SMILE_TERM_FLAT, Pair.of(EUR, USD));

  private static final FXMatrix FX_MATRIX = MULTICURVES.getFxRates();
  private static final double SPOT = FX_MATRIX.getFxRate(EUR, USD);
  // General
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final int SETTLEMENT_DAYS = 0; // CHANGE TO 0 FOR COMPARISON TO VANILLA'S
  // Option
  private static final double STRIKE = 1.45;
  private static final boolean IS_CALL = true;
  private static final boolean IS_LONG = true;
  private static final double NOTIONAL = 100000000;
  private static final Barrier BARRIER_KI = new Barrier(KnockType.IN, BarrierType.DOWN, ObservationType.CLOSE, 1.35);
  private static final Barrier BARRIER_KO = new Barrier(KnockType.OUT, BarrierType.DOWN, ObservationType.CLOSE, 1.35);
  private static final double REBATE = 50000;
  private static final ZonedDateTime OPTION_PAY_DATE = ScheduleCalculator.getAdjustedDate(REFERENCE_DATE, Period.ofMonths(39), BUSINESS_DAY, CALENDAR);
  private static final ZonedDateTime OPTION_EXPIRY_DATE = ScheduleCalculator.getAdjustedDate(OPTION_PAY_DATE, -SETTLEMENT_DAYS, CALENDAR);
  private static final ForexDefinition FOREX_DEFINITION = new ForexDefinition(EUR, USD, OPTION_PAY_DATE, NOTIONAL, STRIKE);
  private static final ForexOptionVanillaDefinition VANILLA_LONG_DEFINITION = new ForexOptionVanillaDefinition(FOREX_DEFINITION, OPTION_EXPIRY_DATE, IS_CALL, IS_LONG);
  private static final ForexOptionVanilla VANILLA_LONG = VANILLA_LONG_DEFINITION.toDerivative(REFERENCE_DATE, NOT_USED_2);
  private static final ForexOptionSingleBarrier OPTION_BARRIER = new ForexOptionSingleBarrier(VANILLA_LONG, BARRIER_KI, REBATE);
  private static final ForexOptionVanillaDefinition VANILLA_SHORT_DEFINITION = new ForexOptionVanillaDefinition(FOREX_DEFINITION, OPTION_EXPIRY_DATE, IS_CALL, !IS_LONG);
  private static final ForexOptionVanilla VANILLA_SHORT = VANILLA_SHORT_DEFINITION.toDerivative(REFERENCE_DATE, NOT_USED_2);
  private static final ForexOptionSingleBarrier BARRIER_SHORT = new ForexOptionSingleBarrier(VANILLA_SHORT, BARRIER_KI, REBATE);
  // Methods and curves
  private static final ForexOptionVanillaBlackSmileMethod METHOD_VANILLA = ForexOptionVanillaBlackSmileMethod.getInstance();
  private static final ForexOptionSingleBarrierBlackMethod METHOD_BARRIER = ForexOptionSingleBarrierBlackMethod.getInstance();
  private static final BlackBarrierPriceFunction BLACK_BARRIER_FUNCTION = BlackBarrierPriceFunction.getInstance();

  private static final PresentValueForexBlackSmileCalculator PVFBC = PresentValueForexBlackSmileCalculator.getInstance();
  private static final PresentValueCurveSensitivityForexBlackSmileCalculator PVCSFBC = PresentValueCurveSensitivityForexBlackSmileCalculator.getInstance();
  private static final PresentValueForexVolatilitySensitivityForexBlackSmileCalculator PVFVFBC = PresentValueForexVolatilitySensitivityForexBlackSmileCalculator.getInstance();
  private static final CurrencyExposureForexBlackSmileCalculator CEFBC = CurrencyExposureForexBlackSmileCalculator.getInstance();

  private static final double SHIFT_FD = 1.0E-6;
  private static final ParameterSensitivityForexBlackSmileCalculator PS_FBS_C = new ParameterSensitivityForexBlackSmileCalculator(PVCSFBC);
  private static final ParameterSensitivityForexBlackSmileDiscountInterpolatedFDCalculator PS_FBS_FDC = new ParameterSensitivityForexBlackSmileDiscountInterpolatedFDCalculator(PVFBC, SHIFT_FD);

  // The following Barrier Option is essentially just a vanilla
  private static final Barrier BARRIER_IMPOSSIBLE_DOWN = new Barrier(KnockType.OUT, BarrierType.DOWN, ObservationType.CLOSE, 1e-8);
  private static final Barrier BARRIER_IMPOSSIBLE_UP = new Barrier(KnockType.OUT, BarrierType.UP, ObservationType.CLOSE, 1e8);
  private static final ForexOptionSingleBarrier BARRIER_LIKE_VANILLA_DOWN = new ForexOptionSingleBarrier(VANILLA_LONG, BARRIER_IMPOSSIBLE_DOWN, 0.0);
  private static final ForexOptionSingleBarrier BARRIER_LIKE_VANILLA_UP = new ForexOptionSingleBarrier(VANILLA_LONG, BARRIER_IMPOSSIBLE_UP, 0.0);

  private static final double TOLERANCE_PV = 1.0E-2;
  private static final double TOLERANCE_PV_DELTA = 1.0E+0;

  @Test
  /** Comparison with the underlying vanilla option (the vanilla option is more expensive). */
  public void comparisonOfBarrierPricersWithVeryHighAndVeryLowBarrierLevels() {
    final MultipleCurrencyAmount priceBarrierUp = METHOD_BARRIER.presentValue(BARRIER_LIKE_VANILLA_UP, SMILE_MULTICURVES);
    final MultipleCurrencyAmount priceBarrierDown = METHOD_BARRIER.presentValue(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES);
    assertTrue("PV : Knock-Out Barrier with unreachably HIGH level doesn't match one with unreachably LOWlevel", Math.abs(priceBarrierDown.getAmount(USD) / priceBarrierUp.getAmount(USD) - 1) < 1e-4);
  }

  @Test
  /**
   * In-Out Parity. To get this exact, we must set the payment date equal to expiry date. 
   * The treatment of the vanilla is: Z(t,T_pay) * FwdPrice(t,T_exp) where as
   * the treatment of the barrier is: Price(t,T_exp) (roughly :))
   */
  public void testKnockInOutParity() {
    // Local version where expiry is OPTION_PAY_DATE
    final ForexOptionVanillaDefinition vanillaDefn = new ForexOptionVanillaDefinition(FOREX_DEFINITION, OPTION_PAY_DATE, IS_CALL, IS_LONG);
    final ForexOptionVanilla vanillaExpiryEqualsPay = vanillaDefn.toDerivative(REFERENCE_DATE, NOT_USED_2);

    final ForexOptionSingleBarrier knockOut = new ForexOptionSingleBarrier(vanillaExpiryEqualsPay, BARRIER_KO, 0.0);
    final ForexOptionSingleBarrier knockIn = new ForexOptionSingleBarrier(vanillaExpiryEqualsPay, BARRIER_KI, 0.0);
    final ForexOptionSingleBarrier impossibleKnockOut = new ForexOptionSingleBarrier(vanillaExpiryEqualsPay, BARRIER_IMPOSSIBLE_DOWN, 0.0);

    final double priceVanilla = METHOD_VANILLA.presentValue(vanillaExpiryEqualsPay, SMILE_MULTICURVES).getAmount(USD);

    final double priceBarrierKO = METHOD_BARRIER.presentValue(knockOut, SMILE_MULTICURVES).getAmount(USD);
    final double priceBarrierKI = METHOD_BARRIER.presentValue(knockIn, SMILE_MULTICURVES).getAmount(USD);

    // First, check that KO+KI on arbitrary Barrier match the price of the KnockIn on an impossibly low barrier
    final double priceBarrierDown = METHOD_BARRIER.presentValue(impossibleKnockOut, SMILE_MULTICURVES).getAmount(USD);
    assertTrue("PV : Knock-Out + Knock-In Barriers do not sum to the underlying vanilla (as approximated by a barrierOption with an impossibly low barrier",
        Math.abs((priceBarrierKO + priceBarrierKI) / priceBarrierDown - 1.0) < 1e-4);
    assertTrue("PV : Knock-Out + Knock-In Barriers do not sum to the underlying vanilla", ((priceBarrierKO + priceBarrierKI) / priceVanilla - 1.0) < 1e-4);
  }

  @Test
  /**
   * Comparison with the underlying vanilla option (the vanilla option is more expensive).
   */
  public void comparisonVanilla() {
    final MultipleCurrencyAmount priceVanilla = METHOD_VANILLA.presentValue(VANILLA_LONG, SMILE_MULTICURVES);
    final MultipleCurrencyAmount priceBarrier = METHOD_BARRIER.presentValue(OPTION_BARRIER, SMILE_MULTICURVES);
    assertTrue("Barriers are cheaper than vanilla", priceVanilla.getAmount(USD) > priceBarrier.getAmount(USD));
  }

  @Test
  /**
   * Tests present value with a direct computation.
   */
  public void presentValue() {
    final MultipleCurrencyAmount priceMethod = METHOD_BARRIER.presentValue(OPTION_BARRIER, SMILE_MULTICURVES);
    final double payTime = VANILLA_LONG.getUnderlyingForex().getPaymentTime();
    final double dfDomestic = MULTICURVES.getDiscountFactor(VANILLA_LONG.getCurrency2(), payTime);
    final double dfForeign = MULTICURVES.getDiscountFactor(VANILLA_LONG.getCurrency1(), payTime);
    final double rateDomestic = -Math.log(dfDomestic) / payTime;
    final double rateForeign = -Math.log(dfForeign) / payTime;
    final double costOfCarry = rateDomestic - rateForeign;
    final double forward = SPOT * Math.exp(-rateForeign * payTime) / Math.exp(-rateDomestic * payTime);
    final double volatility = SMILE_TERM.getVolatility(new Triple<Double, Double, Double>(VANILLA_LONG.getTimeToExpiry(), STRIKE, forward));
    final double priceComputed = BLACK_BARRIER_FUNCTION.getPrice(VANILLA_LONG, BARRIER_KI, REBATE / NOTIONAL, SPOT, costOfCarry, rateDomestic, volatility) * NOTIONAL;
    assertEquals("Barriers present value", priceComputed, priceMethod.getAmount(USD), TOLERANCE_PV);
  }

  @Test
  /**
   * Test the price scaling and the long/short parity.
   */
  public void scaleLongShortParity() {
    final MultipleCurrencyAmount priceBarrier = METHOD_BARRIER.presentValue(OPTION_BARRIER, SMILE_MULTICURVES);
    final double scale = 10;
    final ForexDefinition fxDefinitionScale = new ForexDefinition(EUR, USD, OPTION_PAY_DATE, NOTIONAL * scale, STRIKE);
    final ForexOptionVanillaDefinition optionDefinitionScale = new ForexOptionVanillaDefinition(fxDefinitionScale, OPTION_EXPIRY_DATE, IS_CALL, IS_LONG);
    final ForexOptionVanilla optionScale = optionDefinitionScale.toDerivative(REFERENCE_DATE, NOT_USED_2);
    final ForexOptionSingleBarrier optionBarrierScale = new ForexOptionSingleBarrier(optionScale, BARRIER_KI, scale * REBATE);
    final MultipleCurrencyAmount priceBarrierScale = METHOD_BARRIER.presentValue(optionBarrierScale, SMILE_MULTICURVES);
    assertEquals("Barriers are cheaper than vanilla", priceBarrier.getAmount(USD) * scale, priceBarrierScale.getAmount(USD), TOLERANCE_PV);
    final MultipleCurrencyAmount priceBarrierShort = METHOD_BARRIER.presentValue(BARRIER_SHORT, SMILE_MULTICURVES);
    assertEquals("Barriers are cheaper than vanilla", -priceBarrier.getAmount(USD), priceBarrierShort.getAmount(USD), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests present value method vs calculator.
   */
  public void presentValueMethodVsCalculator() {
    final MultipleCurrencyAmount pvMethod = METHOD_BARRIER.presentValue(OPTION_BARRIER, SMILE_MULTICURVES);
    final MultipleCurrencyAmount pvCalculator = OPTION_BARRIER.accept(PVFBC, SMILE_MULTICURVES);
    assertEquals("Forex vanilla option: present value Method vs Calculator", pvMethod.getAmount(USD), pvCalculator.getAmount(USD), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests the currency exposure vs a finite difference computation. The computation is with fixed Black volatility (Black world).
   * The volatility used in the shifted price is flat with the volatility equal to the volatility used for the original price.
   */
  public void currencyExposure() {
    final MultipleCurrencyAmount ce = METHOD_BARRIER.currencyExposure(OPTION_BARRIER, SMILE_FLAT_MULTICURVES);
    final double shiftSpotEURUSD = 1E-6;
    final MultipleCurrencyAmount pv = METHOD_BARRIER.presentValue(OPTION_BARRIER, SMILE_FLAT_MULTICURVES);
    final FXMatrix fxMatrixShift = new FXMatrix(EUR, USD, SPOT + shiftSpotEURUSD);
    MulticurveProviderDiscount multicurvesShiftedFX = MULTICURVES.copy();
    multicurvesShiftedFX.setForexMatrix(fxMatrixShift);
    ForexBlackSmileProviderDiscount smileBumpedSpot = new ForexBlackSmileProviderDiscount(multicurvesShiftedFX, SMILE_TERM_FLAT, Pair.of(EUR, USD));
    final MultipleCurrencyAmount pvBumpedSpot = METHOD_BARRIER.presentValue(OPTION_BARRIER, smileBumpedSpot);
    final double ceDomesticFD = (pvBumpedSpot.getAmount(USD) - pv.getAmount(USD));
    assertEquals("Barrier currency exposure: domestic currency", ceDomesticFD, ce.getAmount(EUR) * shiftSpotEURUSD, 2.0E-4);
    final double spotGBPUSD = 1.60;
    final double spotGBPEUR = spotGBPUSD / SPOT;
    final double shiftSpotGBPUSD = 2.0E-6;
    final double spotEURUSDshifted = SPOT + shiftSpotEURUSD;
    final double spotGBPUSDshifted = spotGBPUSD + shiftSpotGBPUSD;
    final double spotGBPEURshifted = spotGBPUSDshifted / spotEURUSDshifted;
    final double pvInGBPBeforeShift = pv.getAmount(USD) / spotGBPUSD;
    final double pvInGBPAfterShift = pvBumpedSpot.getAmount(USD) / spotGBPUSDshifted;
    assertEquals("Barrier currency exposure: all currencies", pvInGBPAfterShift - pvInGBPBeforeShift, ce.getAmount(EUR) * (1 / spotGBPEURshifted - 1 / spotGBPEUR) + ce.getAmount(USD)
        * (1 / spotGBPUSDshifted - 1 / spotGBPUSD), 1.0E-4);
  }

  @Test
  /**
   * Tests currency exposure Method vs Calculator.
   */
  public void currencyExposureMethodVsCalculator() {
    final MultipleCurrencyAmount ceMethod = METHOD_BARRIER.currencyExposure(OPTION_BARRIER, SMILE_MULTICURVES);
    final MultipleCurrencyAmount ceCalculator = OPTION_BARRIER.accept(CEFBC, SMILE_MULTICURVES);
    assertEquals("Forex vanilla option: currency exposure Method vs Calculator", ceMethod.getAmount(EUR), ceCalculator.getAmount(EUR), TOLERANCE_PV);
    assertEquals("Forex vanilla option: currency exposure Method vs Calculator", ceMethod.getAmount(USD), ceCalculator.getAmount(USD), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests the present value curve sensitivity.
   */
  public void presentValueCurveSensitivity() {
    final MultipleCurrencyParameterSensitivity pvpsExact = PS_FBS_C.calculateSensitivity(OPTION_BARRIER, SMILE_FLAT_MULTICURVES, SMILE_FLAT_MULTICURVES.getMulticurveProvider().getAllNames());
    final MultipleCurrencyParameterSensitivity pvpsFD = PS_FBS_FDC.calculateSensitivity(OPTION_BARRIER, SMILE_FLAT_MULTICURVES);
    AssertSensivityObjects.assertEquals("SwaptionPhysicalFixedIborSABRMethod: presentValueCurveSensitivity ", pvpsExact, pvpsFD, TOLERANCE_PV_DELTA);
  }

  @Test
  /**
   * Test the present value curve sensitivity through the method and through the calculator.
   */
  public void presentValueCurveSensitivityMethodVsCalculator() {
    final MultipleCurrencyMulticurveSensitivity pvcsMethod = METHOD_BARRIER.presentValueCurveSensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
    final MultipleCurrencyMulticurveSensitivity pvcsCalculator = OPTION_BARRIER.accept(PVCSFBC, SMILE_MULTICURVES);
    assertEquals("Forex present value curve sensitivity: Method vs Calculator", pvcsMethod, pvcsCalculator);
  }

  @Test
  /**
   * Tests the long/short parity.
   */
  public void longShort() {
    final MultipleCurrencyAmount pvShort = METHOD_BARRIER.presentValue(BARRIER_SHORT, SMILE_MULTICURVES);
    final MultipleCurrencyAmount pvLong = METHOD_BARRIER.presentValue(OPTION_BARRIER, SMILE_MULTICURVES);
    assertEquals("Forex single barrier option: present value long/short parity", pvLong.getAmount(USD), -pvShort.getAmount(USD), TOLERANCE_PV);
    final MultipleCurrencyAmount ceShort = METHOD_BARRIER.currencyExposure(BARRIER_SHORT, SMILE_MULTICURVES);
    final MultipleCurrencyAmount ceLong = METHOD_BARRIER.currencyExposure(OPTION_BARRIER, SMILE_MULTICURVES);
    assertEquals("Forex single barrier option: currency exposure long/short parity", ceLong.getAmount(USD), -ceShort.getAmount(USD), TOLERANCE_PV);
    assertEquals("Forex single barrier option: currency exposure long/short parity", ceLong.getAmount(EUR), -ceShort.getAmount(EUR), TOLERANCE_PV);
    final MultipleCurrencyMulticurveSensitivity pvcsShort = METHOD_BARRIER.presentValueCurveSensitivity(BARRIER_SHORT, SMILE_MULTICURVES);
    final MultipleCurrencyMulticurveSensitivity pvcsLong = METHOD_BARRIER.presentValueCurveSensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
    assertEquals("Forex single barrier option: curve sensitivity long/short parity", pvcsLong.getSensitivity(USD), pvcsShort.getSensitivity(USD).multipliedBy(-1.0));
    final PresentValueForexBlackVolatilitySensitivity pvvsShort = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(BARRIER_SHORT, SMILE_MULTICURVES);
    final PresentValueForexBlackVolatilitySensitivity pvvsLong = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
    assertEquals("Forex single barrier option: volatility sensitivity long/short parity", pvvsLong, pvvsShort.multipliedBy(-1.0));
  }

  @Test
  /**
   * Tests present value volatility sensitivity.
   */
  public void volatilitySensitivity() {
    final PresentValueForexBlackVolatilitySensitivity sensi = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
    final Pair<Currency, Currency> currencyPair = ObjectsPair.of(EUR, USD);
    final DoublesPair point = new DoublesPair(OPTION_BARRIER.getUnderlyingOption().getTimeToExpiry(), STRIKE);
    assertEquals("Forex vanilla option: vega", currencyPair, sensi.getCurrencyPair());
    assertEquals("Forex vanilla option: vega size", 1, sensi.getVega().getMap().entrySet().size());
    assertTrue("Forex vanilla option: vega", sensi.getVega().getMap().containsKey(point));
    final double timeToExpiry = TimeCalculator.getTimeBetween(REFERENCE_DATE, OPTION_EXPIRY_DATE);
    final double payTime = TimeCalculator.getTimeBetween(REFERENCE_DATE, OPTION_PAY_DATE);
    final double dfDomestic = MULTICURVES.getDiscountFactor(USD, payTime);
    final double dfForeign = MULTICURVES.getDiscountFactor(EUR, payTime);
    final double forward = SPOT * dfForeign / dfDomestic;
    final double rateDomestic = -Math.log(dfDomestic) / payTime;
    final double rateForeign = -Math.log(dfForeign) / payTime;
    final double volatility = SMILE_TERM.getVolatility(new Triple<Double, Double, Double>(timeToExpiry, STRIKE, forward));
    final double[] derivatives = new double[5];
    BLACK_BARRIER_FUNCTION.getPriceAdjoint(VANILLA_LONG, BARRIER_KI, REBATE / NOTIONAL, SPOT, rateDomestic - rateForeign, rateDomestic, volatility, derivatives);
    assertEquals("Forex vanilla option: vega", derivatives[4] * NOTIONAL, sensi.getVega().getMap().get(point));
    final PresentValueForexBlackVolatilitySensitivity sensiShort = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(BARRIER_SHORT, SMILE_MULTICURVES);
    assertEquals("Forex vanilla option: vega short", -sensi.getVega().getMap().get(point), sensiShort.getVega().getMap().get(point));
  }

  @Test
  /**
   * Test the present value curve sensitivity through the method and through the calculator.
   */
  public void volatilitySensitivityMethodVsCalculator() {
    final PresentValueForexBlackVolatilitySensitivity pvvsMethod = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
    final PresentValueForexBlackVolatilitySensitivity pvvsCalculator = OPTION_BARRIER.accept(PVFVFBC, SMILE_MULTICURVES);
    assertEquals("Forex present value curve sensitivity: Method vs Calculator", pvvsMethod, pvvsCalculator);
  }

  //  @Test
  //  /**
  //   * Tests present value volatility node sensitivity.
  //   */
  //  public void volatilityNodeSensitivity() {
  //    final PresentValueForexBlackVolatilityNodeSensitivityDataBundle sensi = METHOD_BARRIER.presentValueBlackVolatilityNodeSensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
  //    assertEquals("Forex vanilla option: vega node size", NB_EXP + 1, sensi.getVega().getData().length);
  //    assertEquals("Forex vanilla option: vega node size", NB_STRIKE, sensi.getVega().getData()[0].length);
  //    final Pair<Currency, Currency> currencyPair = ObjectsPair.of(EUR, USD);
  //    assertEquals("Forex vanilla option: vega", currencyPair, sensi.getCurrencyPair());
  //    final PresentValueForexBlackVolatilitySensitivity pointSensitivity = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
  //    final double df = MULTICURVES.getDiscountFactor(USD, TimeCalculator.getTimeBetween(REFERENCE_DATE, OPTION_PAY_DATE));
  //    final double forward = SPOT * MULTICURVES.getDiscountFactor(EUR, TimeCalculator.getTimeBetween(REFERENCE_DATE, OPTION_PAY_DATE)) / df;
  //    final VolatilityAndBucketedSensitivities volAndSensitivities = SMILE_TERM.getVolatilityAndSensitivities(OPTION_BARRIER.getUnderlyingOption().getTimeToExpiry(), STRIKE, forward);
  //    final double[][] nodeWeight = volAndSensitivities.getBucketedSensitivities();
  //    final DoublesPair point = DoublesPair.of(OPTION_BARRIER.getUnderlyingOption().getTimeToExpiry(), STRIKE);
  //    for (int loopexp = 0; loopexp < NB_EXP; loopexp++) {
  //      for (int loopstrike = 0; loopstrike < NB_STRIKE; loopstrike++) {
  //        assertEquals("Forex vanilla option: vega node", nodeWeight[loopexp][loopstrike] * pointSensitivity.getVega().getMap().get(point), sensi.getVega().getData()[loopexp][loopstrike]);
  //      }
  //    }
  //  }

  //  @Test
  //  /**
  //   * Tests present value volatility quote sensitivity.
  //   */
  //  public void volatilityQuoteSensitivity() {
  //    final PresentValueForexBlackVolatilityNodeSensitivityDataBundle sensiStrike = METHOD_BARRIER.presentValueBlackVolatilityNodeSensitivity(OPTION_BARRIER, SMILE_MULTICURVES);
  //    final double[][] sensiQuote = METHOD_BARRIER.presentValueBlackVolatilityNodeSensitivity(OPTION_BARRIER, SMILE_MULTICURVES).quoteSensitivity().getVega();
  //    final double[][] sensiStrikeData = sensiStrike.getVega().getData();
  //    final double[] atm = new double[sensiQuote.length];
  //    for (int loopexp = 0; loopexp < sensiQuote.length; loopexp++) {
  //      for (int loopdelta = 0; loopdelta < DELTA.length; loopdelta++) {
  //        assertEquals("Forex vanilla option: vega quote - RR", sensiQuote[loopexp][1 + loopdelta], -0.5 * sensiStrikeData[loopexp][loopdelta] + 0.5
  //            * sensiStrikeData[loopexp][2 * DELTA.length - loopdelta], 1.0E-10);
  //        assertEquals("Forex vanilla option: vega quote - Strangle", sensiQuote[loopexp][DELTA.length + 1 + loopdelta], sensiStrikeData[loopexp][loopdelta]
  //            + sensiStrikeData[loopexp][2 * DELTA.length - loopdelta], 1.0E-10);
  //        atm[loopexp] += sensiStrikeData[loopexp][loopdelta] + sensiStrikeData[loopexp][2 * DELTA.length - loopdelta];
  //      }
  //      atm[loopexp] += sensiStrikeData[loopexp][DELTA.length];
  //      assertEquals("Forex vanilla option: vega quote", sensiQuote[loopexp][0], atm[loopexp], 1.0E-10); // ATM
  //    }
  //  }

  //  @Test
  //  /**
  //   * Tests present value volatility quote sensitivity: method vs calculator.
  //   */
  //  public void volatilityQuoteSensitivityMethodVsCalculator() {
  //    final double[][] sensiMethod = METHOD_BARRIER.presentValueBlackVolatilityNodeSensitivity(OPTION_BARRIER, SMILE_MULTICURVES).quoteSensitivity().getVega();
  //    final double[][] sensiCalculator = PresentValueBlackVolatilityQuoteSensitivityForexCalculator.getInstance().visit(OPTION_BARRIER, SMILE_MULTICURVES).getVega();
  //    for (int loopexp = 0; loopexp < NB_EXP; loopexp++) {
  //      ArrayAsserts.assertArrayEquals("Forex option - quote sensitivity", sensiMethod[loopexp], sensiCalculator[loopexp], 1.0E-10);
  //    }
  //  }

  @Test
  public void gammaAgainstVanilla() {
    // Create a knock-out barrier with a barrier forever away. Compare this to a vanilla: BARRIER_LIKE_VANILLA_DOWN
    final CurrencyAmount gammaBarrier_p1 = METHOD_BARRIER.gammaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.00001);
    final CurrencyAmount gammaBarrier_1 = METHOD_BARRIER.gammaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.0001);
    final CurrencyAmount gammaBarrier_10 = METHOD_BARRIER.gammaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.001);
    final CurrencyAmount gammaBarrier_100 = METHOD_BARRIER.gammaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.01);
    final CurrencyAmount gammaVanilla = METHOD_VANILLA.gamma(VANILLA_LONG, SMILE_MULTICURVES, true);
    assertEquals("Gamma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", gammaVanilla.getAmount(), gammaBarrier_p1.getAmount(), 1e-8 * NOTIONAL);
    assertTrue("Gamma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", Math.abs(gammaVanilla.getAmount() / gammaBarrier_1.getAmount() - 1.0) < 1e-6);
    assertEquals("Gamma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", gammaBarrier_p1.getAmount(), gammaBarrier_1.getAmount(), 1e-6 * NOTIONAL);
    assertEquals("Gamma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", gammaVanilla.getAmount(), gammaBarrier_1.getAmount(), 1e-7 * NOTIONAL);
    assertEquals("Gamma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", gammaBarrier_1.getAmount(), gammaBarrier_10.getAmount(), 1e-4 * NOTIONAL);
    assertEquals("Gamma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", gammaBarrier_10.getAmount(), gammaBarrier_100.getAmount(), 1e-3 * NOTIONAL);
  }

  @Test
  public void vommaAgainstVanilla() {
    // Create a knock-out barrier with a barrier forever away. Compare this to a vanilla: BARRIER_LIKE_VANILLA_DOWN
    final CurrencyAmount vommaBarrier_p1 = METHOD_BARRIER.vommaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.00001);
    final CurrencyAmount vommaBarrier_1 = METHOD_BARRIER.vommaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.0001);
    final CurrencyAmount vommaBarrier_10 = METHOD_BARRIER.vommaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.001);
    final CurrencyAmount vommaBarrier_100 = METHOD_BARRIER.vommaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.01);
    final CurrencyAmount vommaVanilla = METHOD_VANILLA.vomma(VANILLA_LONG, SMILE_MULTICURVES);

    final PresentValueForexBlackVolatilitySensitivity vegaBarrier = METHOD_BARRIER.presentValueBlackVolatilitySensitivity(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES);
    final PresentValueForexBlackVolatilitySensitivity vegaVanilla = METHOD_VANILLA.presentValueBlackVolatilitySensitivity(VANILLA_LONG, SMILE_MULTICURVES);
    assertEquals("Vega of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vegaVanilla.getVega().toSingleValue(), vegaBarrier.getVega().toSingleValue(), 1e-8 * NOTIONAL);

    assertEquals("Vomma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vommaVanilla.getAmount(), vommaBarrier_p1.getAmount(), 1e-6 * NOTIONAL);
    assertTrue("Vomma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", Math.abs(vommaVanilla.getAmount() / vommaBarrier_1.getAmount() - 1.0) < 1e-6);
    assertEquals("Vomma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vommaBarrier_p1.getAmount(), vommaBarrier_1.getAmount(), 1e-6 * NOTIONAL);
    assertEquals("Vomma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vommaVanilla.getAmount(), vommaBarrier_1.getAmount(), 1e-7 * NOTIONAL);
    assertEquals("Vomma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vommaBarrier_1.getAmount(), vommaBarrier_10.getAmount(), 1e-4 * NOTIONAL);
    assertEquals("Vomma of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vommaBarrier_10.getAmount(), vommaBarrier_100.getAmount(), 1e-3 * NOTIONAL);
  }

  @Test
  public void vannaAgainstVanilla() {
    // Create a knock-out barrier with a barrier forever away. Compare this to a vanilla: BARRIER_LIKE_VANILLA_DOWN
    final CurrencyAmount vannaBarrier_p1 = METHOD_BARRIER.vannaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.00001);
    final CurrencyAmount vannaBarrier_1 = METHOD_BARRIER.vannaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.0001);
    final CurrencyAmount vannaBarrier_10 = METHOD_BARRIER.vannaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.001);
    final CurrencyAmount vannaBarrier_100 = METHOD_BARRIER.vannaFd(BARRIER_LIKE_VANILLA_DOWN, SMILE_MULTICURVES, 0.01);
    final CurrencyAmount vannaVanilla = METHOD_VANILLA.vanna(VANILLA_LONG, SMILE_MULTICURVES);
    assertEquals("Vanna of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", 1.0, vannaVanilla.getAmount() / vannaBarrier_p1.getAmount(), 1e-6);
    assertTrue("Vanna of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", Math.abs(vannaVanilla.getAmount() / vannaBarrier_1.getAmount() - 1.0) < 1e-6);
    assertEquals("Vanna of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vannaBarrier_p1.getAmount(), vannaBarrier_1.getAmount(), 1e-6 * NOTIONAL);
    assertEquals("Vanna of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vannaVanilla.getAmount(), vannaBarrier_1.getAmount(), 1e-7 * NOTIONAL);
    assertEquals("Vanna of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vannaBarrier_1.getAmount(), vannaBarrier_10.getAmount(), 1e-4 * NOTIONAL);
    assertEquals("Vanna of KO Barrier with unreachable barrier doesn't match underlying Vanilla's", vannaBarrier_10.getAmount(), vannaBarrier_100.getAmount(), 1e-3 * NOTIONAL);
  }

  //  @Test
  //  /**
  //   * Compares the methods for computing Vanna. Unfortunately, the different techniques for computing the cross derivative produce wildly different results!
  //   */
  //  public void vannaComparison() {
  //    final double tenbp = 0.001;
  //    final double dVdS = METHOD_BARRIER.dVegaDSpotFD(OPTION_BARRIER, SMILE_MULTICURVES, tenbp).getAmount();
  //    final double dDdsig = METHOD_BARRIER.dDeltaDVolFD(OPTION_BARRIER, SMILE_MULTICURVES, tenbp).getAmount();
  //    final double d2PdSdsig10 = METHOD_BARRIER.d2PriceDSpotDVolFD(BARRIER_SHORT, SMILE_MULTICURVES, tenbp).getAmount();
  //    final double d2PdSdsig100 = METHOD_BARRIER.d2PriceDSpotDVolFD(BARRIER_SHORT, SMILE_MULTICURVES, 0.01).getAmount();
  //    final double d2PdSdsig1 = METHOD_BARRIER.d2PriceDSpotDVolFD(BARRIER_SHORT, SMILE_MULTICURVES, 0.0001).getAmount();
  //    assertTrue(true);
  //  }

  @Test
  /**
   * For the three key 2nd order sensitivities: Gamma, Vomma and Vanna, Finite Difference is used.
   * The tests below check the methods. <p>
   * 
   * There are tests below that compare computing Gamma and Vomma via 2nd order approximations of price to 1st order approximations of the 1st order greek - delta, vega respectively.
   * This works well for the one-dimensional sensitivities above, but not well at all for the cross-derivative, Vanna. Comparison below, and in test above.
   * 
   */
  public void TestOfFiniteDifferenceMethods() {
    final ForexOptionSingleBarrier optionForex = OPTION_BARRIER;
    final double bp10 = 0.001;
    final double relShift = 0.001;
    // repackage for calls to BARRIER_FUNCTION 
    final double payTime = optionForex.getUnderlyingOption().getUnderlyingForex().getPaymentTime();
    final double dfDomestic = MULTICURVES.getDiscountFactor(VANILLA_LONG.getCurrency2(), payTime);
    final double dfForeign = MULTICURVES.getDiscountFactor(VANILLA_LONG.getCurrency1(), payTime);
    final double rateDomestic = -Math.log(dfDomestic) / payTime;
    final double rateForeign = -Math.log(dfForeign) / payTime;
    final double costOfCarry = rateDomestic - rateForeign;
    final double spot = MULTICURVES.getFxRate(optionForex.getCurrency1(), optionForex.getCurrency2());
    final double forward = spot * Math.exp(-rateForeign * payTime) / Math.exp(-rateDomestic * payTime);
    final double foreignAmount = optionForex.getUnderlyingOption().getUnderlyingForex().getPaymentCurrency1().getAmount();
    final double rebateByForeignUnit = optionForex.getRebate() / Math.abs(foreignAmount);
    final double sign = (optionForex.getUnderlyingOption().isLong() ? 1.0 : -1.0);
    final double vol = SMILE_MULTICURVES.getVolatility(optionForex.getCurrency1(), optionForex.getCurrency2(), optionForex.getUnderlyingOption().getTimeToExpiry(), optionForex.getUnderlyingOption()
        .getStrike(), forward);

    // Bump scenarios
    final double volUp = (1.0 + relShift) * vol;
    final double volDown = (1.0 - relShift) * vol;
    final double spotUp = (1.0 + relShift) * spot;
    final double spotDown = (1.0 - relShift) * spot;

    // Prices in scenarios
    final double pxBase = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spot, costOfCarry, rateDomestic, vol);
    final double pxVolUp = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spot, costOfCarry, rateDomestic, volUp);
    final double pxVolDown = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spot, costOfCarry, rateDomestic, volDown);
    final double pxSpotUp = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spotUp, costOfCarry, rateDomestic, vol);
    final double pxSpotDown = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spotDown, costOfCarry, rateDomestic, vol);
    final double pxUpUp = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spotUp, costOfCarry, rateDomestic, volUp);
    final double pxDownDown = BLACK_BARRIER_FUNCTION.getPrice(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spotDown, costOfCarry, rateDomestic, volDown);

    // 1. Compare the analytic vega to the finite difference vega
    // Bump vol and compute *price*

    final double vegaFD = (pxVolUp - pxVolDown) / (2 * relShift * vol);
    final double[] adjoint = new double[5];
    final double pxBaseTest = BLACK_BARRIER_FUNCTION.getPriceAdjoint(optionForex.getUnderlyingOption(), optionForex.getBarrier(), rebateByForeignUnit, spot, costOfCarry, rateDomestic, vol, adjoint);
    final double vegaBlack = adjoint[4];
    assertEquals("Vega: Analytic and FiniteDifference are out.", vegaBlack, vegaFD, bp10);

    // 2. Compare the price from getPrice vs getPriceAdjoint
    assertTrue("Adjoint: Price from getPrice and getPriceAdjoint are out.", Math.abs(pxBase - pxBaseTest) < 1.0e-8 * Math.abs(foreignAmount));

    // 3. Vomma from Vega vs Price
    final double VommaViaVega = METHOD_BARRIER.vommaFd(optionForex, SMILE_MULTICURVES, relShift).getAmount();
    final double VommaViaPrice = (pxVolUp - 2 * pxBase + pxVolDown) / (relShift * vol) / (relShift * vol) * Math.abs(foreignAmount) * sign;
    assertTrue("Vomma: FiniteDifference from 2nd order from Price and 1st order from Vega are out.", Math.abs(VommaViaVega - VommaViaPrice) < Math.abs(foreignAmount * bp10));

    // 4. Compare Analytic to FD Delta
    final double deltaFD = (pxSpotUp - pxSpotDown) / (2 * relShift * spot);
    final double deltaBlack = adjoint[0];
    assertTrue("Delta: Analytic and FiniteDifference are out.", Math.abs(deltaFD - deltaBlack) < bp10);

    // 5. Computing Gamma from Delta vs Price
    final double GammaViaDelta = METHOD_BARRIER.gammaFd(optionForex, SMILE_MULTICURVES, relShift).getAmount();
    final double GammaViaPrice = (pxSpotUp - 2 * pxBase + pxSpotDown) / (relShift * spot) / (relShift * spot) * Math.abs(foreignAmount) * sign;
    assertTrue("Gamma: FiniteDifference from 2nd order from Price and 1st order from Delta are out.", Math.abs(GammaViaPrice - GammaViaDelta) < Math.abs(foreignAmount) * bp10);

    // 6. Computing Vanna - darn cross-derivative

    final double dVdS = METHOD_BARRIER.dVegaDSpotFD(OPTION_BARRIER, SMILE_MULTICURVES, relShift).getAmount();
    final double dDdsig = METHOD_BARRIER.dDeltaDVolFD(OPTION_BARRIER, SMILE_MULTICURVES, relShift).getAmount();
    final double d2PdSdsig = METHOD_BARRIER.d2PriceDSpotDVolFD(OPTION_BARRIER, SMILE_MULTICURVES, relShift).getAmount();
    // This is the last form given here:http: //en.wikipedia.org/wiki/Finite_difference#Finite_difference_in_several_variables
    final double d2PdSdsigAlt = METHOD_BARRIER.d2PriceDSpotDVolFdAlt(OPTION_BARRIER, SMILE_MULTICURVES, relShift).getAmount();
    final double vannaAlt = (2 * pxBase + pxUpUp + pxDownDown - pxSpotUp - pxVolUp - pxSpotDown - pxVolDown) / (2 * relShift * vol * relShift * spot) * Math.abs(foreignAmount) * sign;
    assertTrue("Vanna: Agreement of methods is out", Math.abs(d2PdSdsig - d2PdSdsigAlt) < Math.abs(foreignAmount) * bp10);
  }

}
