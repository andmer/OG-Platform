/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.payments.provider;

import static org.testng.AssertJUnit.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import cern.jet.random.engine.MersenneTwister;

import com.opengamma.analytics.financial.instrument.index.IborIndex;
import com.opengamma.analytics.financial.instrument.payment.CapFloorIborDefinition;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CapFloorIbor;
import com.opengamma.analytics.financial.model.interestrate.HullWhiteOneFactorPiecewiseConstantInterestRateModel;
import com.opengamma.analytics.financial.model.interestrate.TestsDataSetHullWhite;
import com.opengamma.analytics.financial.model.interestrate.definition.HullWhiteOneFactorPiecewiseConstantParameters;
import com.opengamma.analytics.financial.montecarlo.provider.HullWhiteMonteCarloMethod;
import com.opengamma.analytics.financial.provider.calculator.hullwhite.PresentValueCurveSensitivityHullWhiteCalculator;
import com.opengamma.analytics.financial.provider.calculator.hullwhite.PresentValueHullWhiteCalculator;
import com.opengamma.analytics.financial.provider.description.HullWhiteOneFactorProviderDiscount;
import com.opengamma.analytics.financial.provider.description.HullWhiteOneFactorProviderInterface;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscount;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderDiscountDataSets;
import com.opengamma.analytics.financial.provider.sensitivity.hullwhite.ParameterSensitivityHullWhiteDiscountInterpolatedFDCalculator;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyParameterSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.parameter.ParameterSensitivityParameterCalculator;
import com.opengamma.analytics.financial.util.AssertSensivityObjects;
import com.opengamma.analytics.math.random.NormalRandomNumberGenerator;
import com.opengamma.analytics.math.statistics.distribution.NormalDistribution;
import com.opengamma.analytics.math.statistics.distribution.ProbabilityDistribution;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.time.DateUtils;

/**
 * Tests on the Hull-White one factor method to price Cap/Floor on Ibor. 
 */
public class CapFloorIborHullWhiteMethodTest {

  private static final MulticurveProviderDiscount MULTICURVES = MulticurveProviderDiscountDataSets.createMulticurveEurUsd();
  private static final HullWhiteOneFactorPiecewiseConstantParameters HW_PARAMETERS = TestsDataSetHullWhite.createHullWhiteParameters();
  private static final IborIndex EURIBOR3M = MulticurveProviderDiscountDataSets.getIndexesIborMulticurveEurUsd()[0];
  private static final Currency EUR = EURIBOR3M.getCurrency();
  //  private static final Calendar CALENDAR = EURIBOR3M.getCalendar();
  private static final HullWhiteOneFactorProviderDiscount HW_MULTICURVES = new HullWhiteOneFactorProviderDiscount(MULTICURVES, HW_PARAMETERS, EUR);
  public static final String NOT_USED = "Not used";
  public static final String[] NOT_USED_A = {NOT_USED, NOT_USED, NOT_USED};
  // Cap/floor description
  private static final ZonedDateTime FIXING_DATE = DateUtils.getUTCDate(2011, 1, 3);
  private static final double NOTIONAL = 100000000; //100m
  private static final double STRIKE = 0.02;
  private static final boolean IS_CAP = true;
  private static final CapFloorIborDefinition CAP_LONG_DEFINITION = CapFloorIborDefinition.from(NOTIONAL, FIXING_DATE, EURIBOR3M, STRIKE, IS_CAP);
  private static final CapFloorIborDefinition CAP_SHORT_DEFINITION = CapFloorIborDefinition.from(-NOTIONAL, FIXING_DATE, EURIBOR3M, STRIKE, IS_CAP);
  private static final CapFloorIborDefinition PUT_LONG_DEFINITION = CapFloorIborDefinition.from(NOTIONAL, FIXING_DATE, EURIBOR3M, STRIKE, !IS_CAP);
  // To derivative
  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2008, 8, 18);
  private static final CapFloorIbor CAP_LONG = (CapFloorIbor) CAP_LONG_DEFINITION.toDerivative(REFERENCE_DATE, NOT_USED_A);
  private static final CapFloorIbor CAP_SHORT = (CapFloorIbor) CAP_SHORT_DEFINITION.toDerivative(REFERENCE_DATE, NOT_USED_A);
  private static final CapFloorIbor PUT_LONG = (CapFloorIbor) PUT_LONG_DEFINITION.toDerivative(REFERENCE_DATE, NOT_USED_A);

  private static final CapFloorIborHullWhiteMethod METHOD_HW = CapFloorIborHullWhiteMethod.getInstance();
  private static final HullWhiteOneFactorPiecewiseConstantInterestRateModel MODEL = new HullWhiteOneFactorPiecewiseConstantInterestRateModel();

  private static final PresentValueHullWhiteCalculator PVHWC = PresentValueHullWhiteCalculator.getInstance();
  private static final PresentValueCurveSensitivityHullWhiteCalculator PVCSHWC = PresentValueCurveSensitivityHullWhiteCalculator.getInstance();

  private static final double SHIFT = 1.0E-6;

  private static final ParameterSensitivityParameterCalculator<HullWhiteOneFactorProviderInterface> PS_HW_C = new ParameterSensitivityParameterCalculator<HullWhiteOneFactorProviderInterface>(PVCSHWC);
  private static final ParameterSensitivityHullWhiteDiscountInterpolatedFDCalculator PS_HW_FDC = new ParameterSensitivityHullWhiteDiscountInterpolatedFDCalculator(PVHWC, SHIFT);

  private static final int NB_PATH = 12500;
  private static final double TOLERANCE_PV = 1.0E-2;
  private static final double TOLERANCE_PV_DELTA = 1.0E+0; // 0.01 currency unit for 1bp

  @Test
  public void presentValueStandard() {
    double tp = CAP_LONG.getPaymentTime();
    double t0 = CAP_LONG.getFixingPeriodStartTime();
    double t1 = CAP_LONG.getFixingPeriodEndTime();
    double theta = CAP_LONG.getFixingTime();
    double deltaF = CAP_LONG.getFixingAccrualFactor();
    double deltaP = CAP_LONG.getPaymentYearFraction();
    double alpha0 = MODEL.alpha(HW_PARAMETERS, 0.0, theta, tp, t0);
    double alpha1 = MODEL.alpha(HW_PARAMETERS, 0.0, theta, tp, t1);
    double ptp = MULTICURVES.getDiscountFactor(EUR, tp);
    double forward = MULTICURVES.getForwardRate(EURIBOR3M, t0, t1, CAP_LONG.getFixingAccrualFactor());
    double kappa = Math.log((1.0 + deltaF * STRIKE) / (1.0 + deltaF * forward));
    kappa += -(alpha1 * alpha1 - alpha0 * alpha0) / 2.0;
    kappa /= alpha1 - alpha0;
    final ProbabilityDistribution<Double> normal = new NormalDistribution(0, 1);
    double priceExpected = (1.0 + deltaF * forward) * normal.getCDF(-kappa - alpha0) - (1.0 + deltaF * STRIKE) * normal.getCDF(-kappa - alpha1);
    priceExpected *= deltaP / deltaF * ptp;
    priceExpected *= NOTIONAL;
    MultipleCurrencyAmount priceMethod = METHOD_HW.presentValue(CAP_LONG, HW_MULTICURVES);
    assertEquals("Cap/floor: Hull-White pricing", priceExpected, priceMethod.getAmount(EUR), TOLERANCE_PV);
  }

  //TODO: present value in arrears

  @Test
  public void presentValueLongShort() {
    MultipleCurrencyAmount priceLong = METHOD_HW.presentValue(CAP_LONG, HW_MULTICURVES);
    MultipleCurrencyAmount priceShort = METHOD_HW.presentValue(CAP_SHORT, HW_MULTICURVES);
    assertEquals("Cap/floor: Hull-White pricing", priceLong.getAmount(EUR), -priceShort.getAmount(EUR), TOLERANCE_PV);
  }

  @Test
  /**
   * Tests present value curve sensitivity when the valuation date is on trade date.
   */
  public void presentValueCurveSensitivity() {
    final MultipleCurrencyParameterSensitivity pvpsExact = PS_HW_C.calculateSensitivity(CAP_LONG, HW_MULTICURVES, HW_MULTICURVES.getMulticurveProvider().getAllNames());
    final MultipleCurrencyParameterSensitivity pvpsFD = PS_HW_FDC.calculateSensitivity(CAP_LONG, HW_MULTICURVES);
    AssertSensivityObjects.assertEquals("SwaptionPhysicalFixedIborSABRMethod: presentValueCurveSensitivity ", pvpsExact, pvpsFD, TOLERANCE_PV_DELTA);
  }

  @Test
  /**
   * Tests the Hull-White parameters sensitivity.
   */
  public void presentValueHullWhiteSensitivity() {
    presentValueHullWhiteSensitivityInstrument(CAP_LONG);
    presentValueHullWhiteSensitivityInstrument(CAP_SHORT);
    presentValueHullWhiteSensitivityInstrument(PUT_LONG);
  }

  private void presentValueHullWhiteSensitivityInstrument(final CapFloorIbor instrument) {
    double[] hwSensitivity = METHOD_HW.presentValueHullWhiteSensitivity(instrument, HW_MULTICURVES);
    int nbVolatility = HW_PARAMETERS.getVolatility().length;
    double shiftVol = 1.0E-6;
    double[] volatilityBumped = new double[nbVolatility];
    System.arraycopy(HW_PARAMETERS.getVolatility(), 0, volatilityBumped, 0, nbVolatility);
    double[] volatilityTime = new double[nbVolatility - 1];
    System.arraycopy(HW_PARAMETERS.getVolatilityTime(), 1, volatilityTime, 0, nbVolatility - 1);
    double[] pvBumpedPlus = new double[nbVolatility];
    double[] pvBumpedMinus = new double[nbVolatility];
    HullWhiteOneFactorPiecewiseConstantParameters parametersBumped = new HullWhiteOneFactorPiecewiseConstantParameters(HW_PARAMETERS.getMeanReversion(), volatilityBumped, volatilityTime);
    HullWhiteOneFactorProviderDiscount bundleBumped = new HullWhiteOneFactorProviderDiscount(MULTICURVES, parametersBumped, EUR);
    double[] hwSensitivityExpected = new double[hwSensitivity.length];
    for (int loopvol = 0; loopvol < nbVolatility; loopvol++) {
      volatilityBumped[loopvol] += shiftVol;
      parametersBumped.setVolatility(volatilityBumped);
      pvBumpedPlus[loopvol] = METHOD_HW.presentValue(instrument, bundleBumped).getAmount(EUR);
      volatilityBumped[loopvol] -= 2 * shiftVol;
      parametersBumped.setVolatility(volatilityBumped);
      pvBumpedMinus[loopvol] = METHOD_HW.presentValue(instrument, bundleBumped).getAmount(EUR);
      hwSensitivityExpected[loopvol] = (pvBumpedPlus[loopvol] - pvBumpedMinus[loopvol]) / (2 * shiftVol);
      assertEquals("Cap/floor Ibor - Hull-White sensitivity adjoint: derivative " + loopvol + " - difference:" + (hwSensitivityExpected[loopvol] - hwSensitivity[loopvol]),
          hwSensitivityExpected[loopvol], hwSensitivity[loopvol], 1.0E-0);
      volatilityBumped[loopvol] = HW_PARAMETERS.getVolatility()[loopvol];
    }
  }

  @Test(enabled = true)
  /**
   * Compare explicit formula with Monte-Carlo and long/short and payer/receiver parities.
   */
  public void monteCarlo() {
    HullWhiteMonteCarloMethod methodMC;
    methodMC = new HullWhiteMonteCarloMethod(new NormalRandomNumberGenerator(0.0, 1.0, new MersenneTwister()), 10 * NB_PATH);
    // Seed fixed to the DEFAULT_SEED for testing purposes.
    MultipleCurrencyAmount pvExplicit = METHOD_HW.presentValue(CAP_LONG, HW_MULTICURVES);
    MultipleCurrencyAmount pvMC = methodMC.presentValue(CAP_LONG, EUR, HW_MULTICURVES);
    assertEquals("Cap/floor - Hull-White - Monte Carlo", pvExplicit.getAmount(EUR), pvMC.getAmount(EUR), 5.0E+2);
    double pvMCPreviousRun = 136707.032;
    assertEquals("Swaption physical - Hull-White - Monte Carlo", pvMCPreviousRun, pvMC.getAmount(EUR), TOLERANCE_PV);
    methodMC = new HullWhiteMonteCarloMethod(new NormalRandomNumberGenerator(0.0, 1.0, new MersenneTwister()), 10 * NB_PATH);
    MultipleCurrencyAmount pvShortMC = methodMC.presentValue(CAP_SHORT, EUR, HW_MULTICURVES);
    assertEquals("Swaption physical - Hull-White - Monte Carlo", -pvMC.getAmount(EUR), pvShortMC.getAmount(EUR), TOLERANCE_PV);
  }

  @Test(enabled = false)
  /**
   * Performance for a high number of paths.
   */
  public void performance() {
    long startTime, endTime;
    MultipleCurrencyAmount pvExplicit = METHOD_HW.presentValue(CAP_LONG, HW_MULTICURVES);
    HullWhiteMonteCarloMethod methodMC;
    int nbPath = 1000000;
    methodMC = new HullWhiteMonteCarloMethod(new NormalRandomNumberGenerator(0.0, 1.0, new MersenneTwister()), nbPath);
    int nbTest = 10;
    double[] pv = new double[nbTest];
    double[] pvDiff = new double[nbTest];

    startTime = System.currentTimeMillis();
    for (int looptest = 0; looptest < nbTest; looptest++) {
      pv[looptest] = methodMC.presentValue(CAP_LONG, EUR, HW_MULTICURVES).getAmount(EUR);
      pvDiff[looptest] = pv[looptest] - pvExplicit.getAmount(EUR);
    }
    endTime = System.currentTimeMillis();
    System.out.println(nbTest + " pv cap/floor Hull-White MC method (" + nbPath + " paths): " + (endTime - startTime) + " ms. Error: " + pvDiff[0]);
    // Performance note: price: 12-Jun-12: On Mac Pro 3.2 GHz Quad-Core Intel Xeon: 2400 ms for 10 cap with 1,000,000 paths.
  }

}
