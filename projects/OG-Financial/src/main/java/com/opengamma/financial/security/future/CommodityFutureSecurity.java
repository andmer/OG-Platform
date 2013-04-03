/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.future;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.util.money.Currency;
import com.opengamma.util.time.Expiry;

/**
 * An abstract base class for commodity securities..
 */
@BeanDefinition
public abstract class CommodityFutureSecurity extends FutureSecurity {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * Quantity of unit.
   */
  @PropertyDefinition
  private Double _unitNumber;
  /**
   * Name of the unit.
   */
  @PropertyDefinition
  private String _unitName;

  /**
   * Creates an empty instance.
   * <p>
   * The security details should be set before use.
   */
  public CommodityFutureSecurity() {
    super();
  }

  public CommodityFutureSecurity(Expiry expiry, String tradingExchange, String settlementExchange, Currency currency, double unitAmount, String category) {
    super(expiry, tradingExchange, settlementExchange, currency, unitAmount, category);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CommodityFutureSecurity}.
   * @return the meta-bean, not null
   */
  public static CommodityFutureSecurity.Meta meta() {
    return CommodityFutureSecurity.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(CommodityFutureSecurity.Meta.INSTANCE);
  }

  @Override
  public CommodityFutureSecurity.Meta metaBean() {
    return CommodityFutureSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 2053402093:  // unitNumber
        return getUnitNumber();
      case -292854225:  // unitName
        return getUnitName();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 2053402093:  // unitNumber
        setUnitNumber((Double) newValue);
        return;
      case -292854225:  // unitName
        setUnitName((String) newValue);
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
      CommodityFutureSecurity other = (CommodityFutureSecurity) obj;
      return JodaBeanUtils.equal(getUnitNumber(), other.getUnitNumber()) &&
          JodaBeanUtils.equal(getUnitName(), other.getUnitName()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnitNumber());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnitName());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets quantity of unit.
   * @return the value of the property
   */
  public Double getUnitNumber() {
    return _unitNumber;
  }

  /**
   * Sets quantity of unit.
   * @param unitNumber  the new value of the property
   */
  public void setUnitNumber(Double unitNumber) {
    this._unitNumber = unitNumber;
  }

  /**
   * Gets the the {@code unitNumber} property.
   * @return the property, not null
   */
  public final Property<Double> unitNumber() {
    return metaBean().unitNumber().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets name of the unit.
   * @return the value of the property
   */
  public String getUnitName() {
    return _unitName;
  }

  /**
   * Sets name of the unit.
   * @param unitName  the new value of the property
   */
  public void setUnitName(String unitName) {
    this._unitName = unitName;
  }

  /**
   * Gets the the {@code unitName} property.
   * @return the property, not null
   */
  public final Property<String> unitName() {
    return metaBean().unitName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CommodityFutureSecurity}.
   */
  public static class Meta extends FutureSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code unitNumber} property.
     */
    private final MetaProperty<Double> _unitNumber = DirectMetaProperty.ofReadWrite(
        this, "unitNumber", CommodityFutureSecurity.class, Double.class);
    /**
     * The meta-property for the {@code unitName} property.
     */
    private final MetaProperty<String> _unitName = DirectMetaProperty.ofReadWrite(
        this, "unitName", CommodityFutureSecurity.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "unitNumber",
        "unitName");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2053402093:  // unitNumber
          return _unitNumber;
        case -292854225:  // unitName
          return _unitName;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CommodityFutureSecurity> builder() {
      throw new UnsupportedOperationException("CommodityFutureSecurity is an abstract class");
    }

    @Override
    public Class<? extends CommodityFutureSecurity> beanType() {
      return CommodityFutureSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code unitNumber} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> unitNumber() {
      return _unitNumber;
    }

    /**
     * The meta-property for the {@code unitName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> unitName() {
      return _unitName;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
