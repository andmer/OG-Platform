/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.holiday.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.time.calendar.LocalDate;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.core.holiday.Holiday;
import com.opengamma.core.holiday.HolidayType;
import com.opengamma.id.ExternalId;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueId;
import com.opengamma.util.money.Currency;

/**
 * Simple implementation of {@code Holiday}.
 * <p>
 * This is the simplest possible implementation of the {@link Holiday} interface.
 * <p>
 * This class is mutable and not thread-safe.
 * It is intended to be used in the engine via the read-only {@code Holiday} interface.
 */
@BeanDefinition
public class SimpleHoliday extends DirectBean
    implements Holiday, MutableUniqueIdentifiable, Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The unique identifier of the holiday.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;
  /**
   * The type of the holiday.
   */
  @PropertyDefinition
  private HolidayType _type;
  /**
   * The region external identifier bundle, used when this is a holiday of type BANK.
   * This must be null if the type is not BANK.
   */
  @PropertyDefinition
  private ExternalId _regionExternalId;
  /**
   * The exchange external identifier bundle, used when this is a holiday of type SETTLEMENT or TRADING.
   * This must be null if the type is not SETTLEMENT or TRADING.
   */
  @PropertyDefinition
  private ExternalId _exchangeExternalId;
  /**
   * The currency, used when this is a holiday of type CURRENCY.
   * This must be null if the type is not CURRENCY.
   */
  @PropertyDefinition
  private Currency _currency;
  /**
   * The list of dates that the target (currency/region/exchange) is on holiday, not null.
   */
  @PropertyDefinition
  private final List<LocalDate> _holidayDates = new ArrayList<LocalDate>();

  /**
   * Creates an instance.
   */
  public SimpleHoliday() {
  }

  //-------------------------------------------------------------------------
  /**
   * Adds a holiday date to the list.
   * 
   * @param date  the date to add, not null
   */
  public void addHolidayDate(final LocalDate date) {
    getHolidayDates().add(date);
    Collections.sort(getHolidayDates());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SimpleHoliday}.
   * @return the meta-bean, not null
   */
  public static SimpleHoliday.Meta meta() {
    return SimpleHoliday.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(SimpleHoliday.Meta.INSTANCE);
  }

  @Override
  public SimpleHoliday.Meta metaBean() {
    return SimpleHoliday.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        return getUniqueId();
      case 3575610:  // type
        return getType();
      case -62093222:  // regionExternalId
        return getRegionExternalId();
      case 323354825:  // exchangeExternalId
        return getExchangeExternalId();
      case 575402001:  // currency
        return getCurrency();
      case -367347:  // holidayDates
        return getHolidayDates();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        setUniqueId((UniqueId) newValue);
        return;
      case 3575610:  // type
        setType((HolidayType) newValue);
        return;
      case -62093222:  // regionExternalId
        setRegionExternalId((ExternalId) newValue);
        return;
      case 323354825:  // exchangeExternalId
        setExchangeExternalId((ExternalId) newValue);
        return;
      case 575402001:  // currency
        setCurrency((Currency) newValue);
        return;
      case -367347:  // holidayDates
        setHolidayDates((List<LocalDate>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SimpleHoliday other = (SimpleHoliday) obj;
      return JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getType(), other.getType()) &&
          JodaBeanUtils.equal(getRegionExternalId(), other.getRegionExternalId()) &&
          JodaBeanUtils.equal(getExchangeExternalId(), other.getExchangeExternalId()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getHolidayDates(), other.getHolidayDates());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getType());
    hash += hash * 31 + JodaBeanUtils.hashCode(getRegionExternalId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getExchangeExternalId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getHolidayDates());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unique identifier of the holiday.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the unique identifier of the holiday.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of the holiday.
   * @return the value of the property
   */
  public HolidayType getType() {
    return _type;
  }

  /**
   * Sets the type of the holiday.
   * @param type  the new value of the property
   */
  public void setType(HolidayType type) {
    this._type = type;
  }

  /**
   * Gets the the {@code type} property.
   * @return the property, not null
   */
  public final Property<HolidayType> type() {
    return metaBean().type().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the region external identifier bundle, used when this is a holiday of type BANK.
   * This must be null if the type is not BANK.
   * @return the value of the property
   */
  public ExternalId getRegionExternalId() {
    return _regionExternalId;
  }

  /**
   * Sets the region external identifier bundle, used when this is a holiday of type BANK.
   * This must be null if the type is not BANK.
   * @param regionExternalId  the new value of the property
   */
  public void setRegionExternalId(ExternalId regionExternalId) {
    this._regionExternalId = regionExternalId;
  }

  /**
   * Gets the the {@code regionExternalId} property.
   * This must be null if the type is not BANK.
   * @return the property, not null
   */
  public final Property<ExternalId> regionExternalId() {
    return metaBean().regionExternalId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the exchange external identifier bundle, used when this is a holiday of type SETTLEMENT or TRADING.
   * This must be null if the type is not SETTLEMENT or TRADING.
   * @return the value of the property
   */
  public ExternalId getExchangeExternalId() {
    return _exchangeExternalId;
  }

  /**
   * Sets the exchange external identifier bundle, used when this is a holiday of type SETTLEMENT or TRADING.
   * This must be null if the type is not SETTLEMENT or TRADING.
   * @param exchangeExternalId  the new value of the property
   */
  public void setExchangeExternalId(ExternalId exchangeExternalId) {
    this._exchangeExternalId = exchangeExternalId;
  }

  /**
   * Gets the the {@code exchangeExternalId} property.
   * This must be null if the type is not SETTLEMENT or TRADING.
   * @return the property, not null
   */
  public final Property<ExternalId> exchangeExternalId() {
    return metaBean().exchangeExternalId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency, used when this is a holiday of type CURRENCY.
   * This must be null if the type is not CURRENCY.
   * @return the value of the property
   */
  public Currency getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency, used when this is a holiday of type CURRENCY.
   * This must be null if the type is not CURRENCY.
   * @param currency  the new value of the property
   */
  public void setCurrency(Currency currency) {
    this._currency = currency;
  }

  /**
   * Gets the the {@code currency} property.
   * This must be null if the type is not CURRENCY.
   * @return the property, not null
   */
  public final Property<Currency> currency() {
    return metaBean().currency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the list of dates that the target (currency/region/exchange) is on holiday, not null.
   * @return the value of the property
   */
  public List<LocalDate> getHolidayDates() {
    return _holidayDates;
  }

  /**
   * Sets the list of dates that the target (currency/region/exchange) is on holiday, not null.
   * @param holidayDates  the new value of the property
   */
  public void setHolidayDates(List<LocalDate> holidayDates) {
    this._holidayDates.clear();
    this._holidayDates.addAll(holidayDates);
  }

  /**
   * Gets the the {@code holidayDates} property.
   * @return the property, not null
   */
  public final Property<List<LocalDate>> holidayDates() {
    return metaBean().holidayDates().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SimpleHoliday}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", SimpleHoliday.class, UniqueId.class);
    /**
     * The meta-property for the {@code type} property.
     */
    private final MetaProperty<HolidayType> _type = DirectMetaProperty.ofReadWrite(
        this, "type", SimpleHoliday.class, HolidayType.class);
    /**
     * The meta-property for the {@code regionExternalId} property.
     */
    private final MetaProperty<ExternalId> _regionExternalId = DirectMetaProperty.ofReadWrite(
        this, "regionExternalId", SimpleHoliday.class, ExternalId.class);
    /**
     * The meta-property for the {@code exchangeExternalId} property.
     */
    private final MetaProperty<ExternalId> _exchangeExternalId = DirectMetaProperty.ofReadWrite(
        this, "exchangeExternalId", SimpleHoliday.class, ExternalId.class);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<Currency> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", SimpleHoliday.class, Currency.class);
    /**
     * The meta-property for the {@code holidayDates} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<LocalDate>> _holidayDates = DirectMetaProperty.ofReadWrite(
        this, "holidayDates", SimpleHoliday.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "uniqueId",
        "type",
        "regionExternalId",
        "exchangeExternalId",
        "currency",
        "holidayDates");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -294460212:  // uniqueId
          return _uniqueId;
        case 3575610:  // type
          return _type;
        case -62093222:  // regionExternalId
          return _regionExternalId;
        case 323354825:  // exchangeExternalId
          return _exchangeExternalId;
        case 575402001:  // currency
          return _currency;
        case -367347:  // holidayDates
          return _holidayDates;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SimpleHoliday> builder() {
      return new DirectBeanBuilder<SimpleHoliday>(new SimpleHoliday());
    }

    @Override
    public Class<? extends SimpleHoliday> beanType() {
      return SimpleHoliday.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code type} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<HolidayType> type() {
      return _type;
    }

    /**
     * The meta-property for the {@code regionExternalId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> regionExternalId() {
      return _regionExternalId;
    }

    /**
     * The meta-property for the {@code exchangeExternalId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> exchangeExternalId() {
      return _exchangeExternalId;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code holidayDates} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<LocalDate>> holidayDates() {
      return _holidayDates;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
