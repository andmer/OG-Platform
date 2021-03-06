/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.option;

import java.util.Map;

import javax.time.calendar.ZonedDateTime;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.financial.security.LongShort;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.Expiry;

/**
 * A security for FX options.
 */
@BeanDefinition
public class FXOptionSecurity extends FinancialSecurity {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The security type.
   */
  public static final String SECURITY_TYPE = "FX_OPTION";

  /**
   * The put currency.
   */
  @PropertyDefinition(validate = "notNull")
  private Currency _putCurrency;
  /**
   * The call currency.
   */
  @PropertyDefinition(validate = "notNull")
  private Currency _callCurrency;
  /**
   * The put amount.
   */
  @PropertyDefinition(validate = "notNull")
  private double _putAmount;
  /**
   * The call amount.
   */
  @PropertyDefinition(validate = "notNull")
  private double _callAmount;
  /**
   * The expiry.
   */
  @PropertyDefinition(validate = "notNull")
  private Expiry _expiry;
  /**
   * The settlement date.
   */
  @PropertyDefinition(validate = "notNull")
  private ZonedDateTime _settlementDate;
  /**
   * The long/short type.
   */
  @PropertyDefinition(validate = "notNull")
  private LongShort _longShort = LongShort.LONG;
  /**
   * The exercise type.
   */
  @PropertyDefinition(validate = "notNull")
  private ExerciseType _exerciseType;

  FXOptionSecurity() { //For builder
    super();
  }

  public FXOptionSecurity(Currency putCurrency, Currency callCurrency, double putAmount, double callAmount, Expiry expiry,
      ZonedDateTime settlementDate, boolean isLong, ExerciseType exerciseType) {
    super(SECURITY_TYPE);
    setPutCurrency(putCurrency);
    setCallCurrency(callCurrency);
    setPutAmount(putAmount);
    setCallAmount(callAmount);
    setExpiry(expiry);
    setSettlementDate(settlementDate);
    setLongShort(LongShort.ofLong(isLong));
    setExerciseType(exerciseType);
  }

  //-------------------------------------------------------------------------
  @Override
  public final <T> T accept(FinancialSecurityVisitor<T> visitor) {
    return visitor.visitFXOptionSecurity(this);
  }

  //-------------------------------------------------------------------------
  /**
   * Checks if the long/short type is long.
   * 
   * @return true if long, false if short
   */
  public boolean isLong() {
    return getLongShort().isLong();
  }

  /**
   * Checks if the long/short type is short.
   * 
   * @return true if short, false if long
   */
  public boolean isShort() {
    return getLongShort().isShort();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FXOptionSecurity}.
   * @return the meta-bean, not null
   */
  public static FXOptionSecurity.Meta meta() {
    return FXOptionSecurity.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(FXOptionSecurity.Meta.INSTANCE);
  }

  @Override
  public FXOptionSecurity.Meta metaBean() {
    return FXOptionSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 516393024:  // putCurrency
        return getPutCurrency();
      case 643534991:  // callCurrency
        return getCallCurrency();
      case -984864697:  // putAmount
        return getPutAmount();
      case 1066661974:  // callAmount
        return getCallAmount();
      case -1289159373:  // expiry
        return getExpiry();
      case -295948169:  // settlementDate
        return getSettlementDate();
      case 116685664:  // longShort
        return getLongShort();
      case -466331342:  // exerciseType
        return getExerciseType();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 516393024:  // putCurrency
        setPutCurrency((Currency) newValue);
        return;
      case 643534991:  // callCurrency
        setCallCurrency((Currency) newValue);
        return;
      case -984864697:  // putAmount
        setPutAmount((Double) newValue);
        return;
      case 1066661974:  // callAmount
        setCallAmount((Double) newValue);
        return;
      case -1289159373:  // expiry
        setExpiry((Expiry) newValue);
        return;
      case -295948169:  // settlementDate
        setSettlementDate((ZonedDateTime) newValue);
        return;
      case 116685664:  // longShort
        setLongShort((LongShort) newValue);
        return;
      case -466331342:  // exerciseType
        setExerciseType((ExerciseType) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_putCurrency, "putCurrency");
    JodaBeanUtils.notNull(_callCurrency, "callCurrency");
    JodaBeanUtils.notNull(_putAmount, "putAmount");
    JodaBeanUtils.notNull(_callAmount, "callAmount");
    JodaBeanUtils.notNull(_expiry, "expiry");
    JodaBeanUtils.notNull(_settlementDate, "settlementDate");
    JodaBeanUtils.notNull(_longShort, "longShort");
    JodaBeanUtils.notNull(_exerciseType, "exerciseType");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FXOptionSecurity other = (FXOptionSecurity) obj;
      return JodaBeanUtils.equal(getPutCurrency(), other.getPutCurrency()) &&
          JodaBeanUtils.equal(getCallCurrency(), other.getCallCurrency()) &&
          JodaBeanUtils.equal(getPutAmount(), other.getPutAmount()) &&
          JodaBeanUtils.equal(getCallAmount(), other.getCallAmount()) &&
          JodaBeanUtils.equal(getExpiry(), other.getExpiry()) &&
          JodaBeanUtils.equal(getSettlementDate(), other.getSettlementDate()) &&
          JodaBeanUtils.equal(getLongShort(), other.getLongShort()) &&
          JodaBeanUtils.equal(getExerciseType(), other.getExerciseType()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getPutCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCallCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getPutAmount());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCallAmount());
    hash += hash * 31 + JodaBeanUtils.hashCode(getExpiry());
    hash += hash * 31 + JodaBeanUtils.hashCode(getSettlementDate());
    hash += hash * 31 + JodaBeanUtils.hashCode(getLongShort());
    hash += hash * 31 + JodaBeanUtils.hashCode(getExerciseType());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the put currency.
   * @return the value of the property, not null
   */
  public Currency getPutCurrency() {
    return _putCurrency;
  }

  /**
   * Sets the put currency.
   * @param putCurrency  the new value of the property, not null
   */
  public void setPutCurrency(Currency putCurrency) {
    JodaBeanUtils.notNull(putCurrency, "putCurrency");
    this._putCurrency = putCurrency;
  }

  /**
   * Gets the the {@code putCurrency} property.
   * @return the property, not null
   */
  public final Property<Currency> putCurrency() {
    return metaBean().putCurrency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the call currency.
   * @return the value of the property, not null
   */
  public Currency getCallCurrency() {
    return _callCurrency;
  }

  /**
   * Sets the call currency.
   * @param callCurrency  the new value of the property, not null
   */
  public void setCallCurrency(Currency callCurrency) {
    JodaBeanUtils.notNull(callCurrency, "callCurrency");
    this._callCurrency = callCurrency;
  }

  /**
   * Gets the the {@code callCurrency} property.
   * @return the property, not null
   */
  public final Property<Currency> callCurrency() {
    return metaBean().callCurrency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the put amount.
   * @return the value of the property, not null
   */
  public double getPutAmount() {
    return _putAmount;
  }

  /**
   * Sets the put amount.
   * @param putAmount  the new value of the property, not null
   */
  public void setPutAmount(double putAmount) {
    JodaBeanUtils.notNull(putAmount, "putAmount");
    this._putAmount = putAmount;
  }

  /**
   * Gets the the {@code putAmount} property.
   * @return the property, not null
   */
  public final Property<Double> putAmount() {
    return metaBean().putAmount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the call amount.
   * @return the value of the property, not null
   */
  public double getCallAmount() {
    return _callAmount;
  }

  /**
   * Sets the call amount.
   * @param callAmount  the new value of the property, not null
   */
  public void setCallAmount(double callAmount) {
    JodaBeanUtils.notNull(callAmount, "callAmount");
    this._callAmount = callAmount;
  }

  /**
   * Gets the the {@code callAmount} property.
   * @return the property, not null
   */
  public final Property<Double> callAmount() {
    return metaBean().callAmount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiry.
   * @return the value of the property, not null
   */
  public Expiry getExpiry() {
    return _expiry;
  }

  /**
   * Sets the expiry.
   * @param expiry  the new value of the property, not null
   */
  public void setExpiry(Expiry expiry) {
    JodaBeanUtils.notNull(expiry, "expiry");
    this._expiry = expiry;
  }

  /**
   * Gets the the {@code expiry} property.
   * @return the property, not null
   */
  public final Property<Expiry> expiry() {
    return metaBean().expiry().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the settlement date.
   * @return the value of the property, not null
   */
  public ZonedDateTime getSettlementDate() {
    return _settlementDate;
  }

  /**
   * Sets the settlement date.
   * @param settlementDate  the new value of the property, not null
   */
  public void setSettlementDate(ZonedDateTime settlementDate) {
    JodaBeanUtils.notNull(settlementDate, "settlementDate");
    this._settlementDate = settlementDate;
  }

  /**
   * Gets the the {@code settlementDate} property.
   * @return the property, not null
   */
  public final Property<ZonedDateTime> settlementDate() {
    return metaBean().settlementDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the long/short type.
   * @return the value of the property, not null
   */
  public LongShort getLongShort() {
    return _longShort;
  }

  /**
   * Sets the long/short type.
   * @param longShort  the new value of the property, not null
   */
  public void setLongShort(LongShort longShort) {
    JodaBeanUtils.notNull(longShort, "longShort");
    this._longShort = longShort;
  }

  /**
   * Gets the the {@code longShort} property.
   * @return the property, not null
   */
  public final Property<LongShort> longShort() {
    return metaBean().longShort().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the exercise type.
   * @return the value of the property, not null
   */
  public ExerciseType getExerciseType() {
    return _exerciseType;
  }

  /**
   * Sets the exercise type.
   * @param exerciseType  the new value of the property, not null
   */
  public void setExerciseType(ExerciseType exerciseType) {
    JodaBeanUtils.notNull(exerciseType, "exerciseType");
    this._exerciseType = exerciseType;
  }

  /**
   * Gets the the {@code exerciseType} property.
   * @return the property, not null
   */
  public final Property<ExerciseType> exerciseType() {
    return metaBean().exerciseType().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FXOptionSecurity}.
   */
  public static class Meta extends FinancialSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code putCurrency} property.
     */
    private final MetaProperty<Currency> _putCurrency = DirectMetaProperty.ofReadWrite(
        this, "putCurrency", FXOptionSecurity.class, Currency.class);
    /**
     * The meta-property for the {@code callCurrency} property.
     */
    private final MetaProperty<Currency> _callCurrency = DirectMetaProperty.ofReadWrite(
        this, "callCurrency", FXOptionSecurity.class, Currency.class);
    /**
     * The meta-property for the {@code putAmount} property.
     */
    private final MetaProperty<Double> _putAmount = DirectMetaProperty.ofReadWrite(
        this, "putAmount", FXOptionSecurity.class, Double.TYPE);
    /**
     * The meta-property for the {@code callAmount} property.
     */
    private final MetaProperty<Double> _callAmount = DirectMetaProperty.ofReadWrite(
        this, "callAmount", FXOptionSecurity.class, Double.TYPE);
    /**
     * The meta-property for the {@code expiry} property.
     */
    private final MetaProperty<Expiry> _expiry = DirectMetaProperty.ofReadWrite(
        this, "expiry", FXOptionSecurity.class, Expiry.class);
    /**
     * The meta-property for the {@code settlementDate} property.
     */
    private final MetaProperty<ZonedDateTime> _settlementDate = DirectMetaProperty.ofReadWrite(
        this, "settlementDate", FXOptionSecurity.class, ZonedDateTime.class);
    /**
     * The meta-property for the {@code longShort} property.
     */
    private final MetaProperty<LongShort> _longShort = DirectMetaProperty.ofReadWrite(
        this, "longShort", FXOptionSecurity.class, LongShort.class);
    /**
     * The meta-property for the {@code exerciseType} property.
     */
    private final MetaProperty<ExerciseType> _exerciseType = DirectMetaProperty.ofReadWrite(
        this, "exerciseType", FXOptionSecurity.class, ExerciseType.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "putCurrency",
        "callCurrency",
        "putAmount",
        "callAmount",
        "expiry",
        "settlementDate",
        "longShort",
        "exerciseType");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 516393024:  // putCurrency
          return _putCurrency;
        case 643534991:  // callCurrency
          return _callCurrency;
        case -984864697:  // putAmount
          return _putAmount;
        case 1066661974:  // callAmount
          return _callAmount;
        case -1289159373:  // expiry
          return _expiry;
        case -295948169:  // settlementDate
          return _settlementDate;
        case 116685664:  // longShort
          return _longShort;
        case -466331342:  // exerciseType
          return _exerciseType;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FXOptionSecurity> builder() {
      return new DirectBeanBuilder<FXOptionSecurity>(new FXOptionSecurity());
    }

    @Override
    public Class<? extends FXOptionSecurity> beanType() {
      return FXOptionSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code putCurrency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> putCurrency() {
      return _putCurrency;
    }

    /**
     * The meta-property for the {@code callCurrency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> callCurrency() {
      return _callCurrency;
    }

    /**
     * The meta-property for the {@code putAmount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> putAmount() {
      return _putAmount;
    }

    /**
     * The meta-property for the {@code callAmount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> callAmount() {
      return _callAmount;
    }

    /**
     * The meta-property for the {@code expiry} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Expiry> expiry() {
      return _expiry;
    }

    /**
     * The meta-property for the {@code settlementDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ZonedDateTime> settlementDate() {
      return _settlementDate;
    }

    /**
     * The meta-property for the {@code longShort} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LongShort> longShort() {
      return _longShort;
    }

    /**
     * The meta-property for the {@code exerciseType} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExerciseType> exerciseType() {
      return _exerciseType;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
