package com.opengamma.masterdb.security.hibernate.forward;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.beans.*;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.masterdb.security.hibernate.*;
import com.opengamma.masterdb.security.hibernate.future.FutureBundleBean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;

/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
@BeanDefinition
public abstract class CommodityForwardSecurityBean extends SecurityBean {


  @PropertyDefinition
  private ExpiryBean _expiry;
  @PropertyDefinition
  private CurrencyBean _currency;
  @PropertyDefinition
  private Double _unitAmount;
  @PropertyDefinition
  private UnitBean _unitName;
  @PropertyDefinition
  private Double _unitNumber;
  @PropertyDefinition
  private ExternalIdBean _underlying;
  @PropertyDefinition
  private Set<FutureBundleBean> _basket;
  @PropertyDefinition
  private ZonedDateTimeBean _firstDeliveryDate;
  @PropertyDefinition
  private ZonedDateTimeBean _lastDeliveryDate;
  @PropertyDefinition
  private ContractCategoryBean _category;

  public CommodityForwardSecurityBean() {
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * Visitor.
   */
  public static interface Visitor<T> {
    T visitAgricultureForwardType(AgricultureForwardSecurityBean bean);

    T visitEnergyForwardType(EnergyForwardSecurityBean bean);

    T visitMetalForwardType(MetalForwardSecurityBean bean);
  }

  abstract public <T> T accept(final Visitor<T> visitor);
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CommodityForwardSecurityBean}.
   * @return the meta-bean, not null
   */
  public static CommodityForwardSecurityBean.Meta meta() {
    return CommodityForwardSecurityBean.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(CommodityForwardSecurityBean.Meta.INSTANCE);
  }

  @Override
  public CommodityForwardSecurityBean.Meta metaBean() {
    return CommodityForwardSecurityBean.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1289159373:  // expiry
        return getExpiry();
      case 575402001:  // currency
        return getCurrency();
      case 1673913084:  // unitAmount
        return getUnitAmount();
      case -292854225:  // unitName
        return getUnitName();
      case 2053402093:  // unitNumber
        return getUnitNumber();
      case -1770633379:  // underlying
        return getUnderlying();
      case -1396196922:  // basket
        return getBasket();
      case 1755448466:  // firstDeliveryDate
        return getFirstDeliveryDate();
      case -233366664:  // lastDeliveryDate
        return getLastDeliveryDate();
      case 50511102:  // category
        return getCategory();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1289159373:  // expiry
        setExpiry((ExpiryBean) newValue);
        return;
      case 575402001:  // currency
        setCurrency((CurrencyBean) newValue);
        return;
      case 1673913084:  // unitAmount
        setUnitAmount((Double) newValue);
        return;
      case -292854225:  // unitName
        setUnitName((UnitBean) newValue);
        return;
      case 2053402093:  // unitNumber
        setUnitNumber((Double) newValue);
        return;
      case -1770633379:  // underlying
        setUnderlying((ExternalIdBean) newValue);
        return;
      case -1396196922:  // basket
        setBasket((Set<FutureBundleBean>) newValue);
        return;
      case 1755448466:  // firstDeliveryDate
        setFirstDeliveryDate((ZonedDateTimeBean) newValue);
        return;
      case -233366664:  // lastDeliveryDate
        setLastDeliveryDate((ZonedDateTimeBean) newValue);
        return;
      case 50511102:  // category
        setCategory((ContractCategoryBean) newValue);
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
      CommodityForwardSecurityBean other = (CommodityForwardSecurityBean) obj;
      return JodaBeanUtils.equal(getExpiry(), other.getExpiry()) &&
          JodaBeanUtils.equal(getCurrency(), other.getCurrency()) &&
          JodaBeanUtils.equal(getUnitAmount(), other.getUnitAmount()) &&
          JodaBeanUtils.equal(getUnitName(), other.getUnitName()) &&
          JodaBeanUtils.equal(getUnitNumber(), other.getUnitNumber()) &&
          JodaBeanUtils.equal(getUnderlying(), other.getUnderlying()) &&
          JodaBeanUtils.equal(getBasket(), other.getBasket()) &&
          JodaBeanUtils.equal(getFirstDeliveryDate(), other.getFirstDeliveryDate()) &&
          JodaBeanUtils.equal(getLastDeliveryDate(), other.getLastDeliveryDate()) &&
          JodaBeanUtils.equal(getCategory(), other.getCategory()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getExpiry());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCurrency());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnitAmount());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnitName());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnitNumber());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUnderlying());
    hash += hash * 31 + JodaBeanUtils.hashCode(getBasket());
    hash += hash * 31 + JodaBeanUtils.hashCode(getFirstDeliveryDate());
    hash += hash * 31 + JodaBeanUtils.hashCode(getLastDeliveryDate());
    hash += hash * 31 + JodaBeanUtils.hashCode(getCategory());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the expiry.
   * @return the value of the property
   */
  public ExpiryBean getExpiry() {
    return _expiry;
  }

  /**
   * Sets the expiry.
   * @param expiry  the new value of the property
   */
  public void setExpiry(ExpiryBean expiry) {
    this._expiry = expiry;
  }

  /**
   * Gets the the {@code expiry} property.
   * @return the property, not null
   */
  public final Property<ExpiryBean> expiry() {
    return metaBean().expiry().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the currency.
   * @return the value of the property
   */
  public CurrencyBean getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency.
   * @param currency  the new value of the property
   */
  public void setCurrency(CurrencyBean currency) {
    this._currency = currency;
  }

  /**
   * Gets the the {@code currency} property.
   * @return the property, not null
   */
  public final Property<CurrencyBean> currency() {
    return metaBean().currency().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unitAmount.
   * @return the value of the property
   */
  public Double getUnitAmount() {
    return _unitAmount;
  }

  /**
   * Sets the unitAmount.
   * @param unitAmount  the new value of the property
   */
  public void setUnitAmount(Double unitAmount) {
    this._unitAmount = unitAmount;
  }

  /**
   * Gets the the {@code unitAmount} property.
   * @return the property, not null
   */
  public final Property<Double> unitAmount() {
    return metaBean().unitAmount().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unitName.
   * @return the value of the property
   */
  public UnitBean getUnitName() {
    return _unitName;
  }

  /**
   * Sets the unitName.
   * @param unitName  the new value of the property
   */
  public void setUnitName(UnitBean unitName) {
    this._unitName = unitName;
  }

  /**
   * Gets the the {@code unitName} property.
   * @return the property, not null
   */
  public final Property<UnitBean> unitName() {
    return metaBean().unitName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the unitNumber.
   * @return the value of the property
   */
  public Double getUnitNumber() {
    return _unitNumber;
  }

  /**
   * Sets the unitNumber.
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
   * Gets the underlying.
   * @return the value of the property
   */
  public ExternalIdBean getUnderlying() {
    return _underlying;
  }

  /**
   * Sets the underlying.
   * @param underlying  the new value of the property
   */
  public void setUnderlying(ExternalIdBean underlying) {
    this._underlying = underlying;
  }

  /**
   * Gets the the {@code underlying} property.
   * @return the property, not null
   */
  public final Property<ExternalIdBean> underlying() {
    return metaBean().underlying().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the basket.
   * @return the value of the property
   */
  public Set<FutureBundleBean> getBasket() {
    return _basket;
  }

  /**
   * Sets the basket.
   * @param basket  the new value of the property
   */
  public void setBasket(Set<FutureBundleBean> basket) {
    this._basket = basket;
  }

  /**
   * Gets the the {@code basket} property.
   * @return the property, not null
   */
  public final Property<Set<FutureBundleBean>> basket() {
    return metaBean().basket().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the firstDeliveryDate.
   * @return the value of the property
   */
  public ZonedDateTimeBean getFirstDeliveryDate() {
    return _firstDeliveryDate;
  }

  /**
   * Sets the firstDeliveryDate.
   * @param firstDeliveryDate  the new value of the property
   */
  public void setFirstDeliveryDate(ZonedDateTimeBean firstDeliveryDate) {
    this._firstDeliveryDate = firstDeliveryDate;
  }

  /**
   * Gets the the {@code firstDeliveryDate} property.
   * @return the property, not null
   */
  public final Property<ZonedDateTimeBean> firstDeliveryDate() {
    return metaBean().firstDeliveryDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the lastDeliveryDate.
   * @return the value of the property
   */
  public ZonedDateTimeBean getLastDeliveryDate() {
    return _lastDeliveryDate;
  }

  /**
   * Sets the lastDeliveryDate.
   * @param lastDeliveryDate  the new value of the property
   */
  public void setLastDeliveryDate(ZonedDateTimeBean lastDeliveryDate) {
    this._lastDeliveryDate = lastDeliveryDate;
  }

  /**
   * Gets the the {@code lastDeliveryDate} property.
   * @return the property, not null
   */
  public final Property<ZonedDateTimeBean> lastDeliveryDate() {
    return metaBean().lastDeliveryDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the category.
   * @return the value of the property
   */
  public ContractCategoryBean getCategory() {
    return _category;
  }

  /**
   * Sets the category.
   * @param category  the new value of the property
   */
  public void setCategory(ContractCategoryBean category) {
    this._category = category;
  }

  /**
   * Gets the the {@code category} property.
   * @return the property, not null
   */
  public final Property<ContractCategoryBean> category() {
    return metaBean().category().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CommodityForwardSecurityBean}.
   */
  public static class Meta extends SecurityBean.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code expiry} property.
     */
    private final MetaProperty<ExpiryBean> _expiry = DirectMetaProperty.ofReadWrite(
        this, "expiry", CommodityForwardSecurityBean.class, ExpiryBean.class);
    /**
     * The meta-property for the {@code currency} property.
     */
    private final MetaProperty<CurrencyBean> _currency = DirectMetaProperty.ofReadWrite(
        this, "currency", CommodityForwardSecurityBean.class, CurrencyBean.class);
    /**
     * The meta-property for the {@code unitAmount} property.
     */
    private final MetaProperty<Double> _unitAmount = DirectMetaProperty.ofReadWrite(
        this, "unitAmount", CommodityForwardSecurityBean.class, Double.class);
    /**
     * The meta-property for the {@code unitName} property.
     */
    private final MetaProperty<UnitBean> _unitName = DirectMetaProperty.ofReadWrite(
        this, "unitName", CommodityForwardSecurityBean.class, UnitBean.class);
    /**
     * The meta-property for the {@code unitNumber} property.
     */
    private final MetaProperty<Double> _unitNumber = DirectMetaProperty.ofReadWrite(
        this, "unitNumber", CommodityForwardSecurityBean.class, Double.class);
    /**
     * The meta-property for the {@code underlying} property.
     */
    private final MetaProperty<ExternalIdBean> _underlying = DirectMetaProperty.ofReadWrite(
        this, "underlying", CommodityForwardSecurityBean.class, ExternalIdBean.class);
    /**
     * The meta-property for the {@code basket} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Set<FutureBundleBean>> _basket = DirectMetaProperty.ofReadWrite(
        this, "basket", CommodityForwardSecurityBean.class, (Class) Set.class);
    /**
     * The meta-property for the {@code firstDeliveryDate} property.
     */
    private final MetaProperty<ZonedDateTimeBean> _firstDeliveryDate = DirectMetaProperty.ofReadWrite(
        this, "firstDeliveryDate", CommodityForwardSecurityBean.class, ZonedDateTimeBean.class);
    /**
     * The meta-property for the {@code lastDeliveryDate} property.
     */
    private final MetaProperty<ZonedDateTimeBean> _lastDeliveryDate = DirectMetaProperty.ofReadWrite(
        this, "lastDeliveryDate", CommodityForwardSecurityBean.class, ZonedDateTimeBean.class);
    /**
     * The meta-property for the {@code category} property.
     */
    private final MetaProperty<ContractCategoryBean> _category = DirectMetaProperty.ofReadWrite(
        this, "category", CommodityForwardSecurityBean.class, ContractCategoryBean.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "expiry",
        "currency",
        "unitAmount",
        "unitName",
        "unitNumber",
        "underlying",
        "basket",
        "firstDeliveryDate",
        "lastDeliveryDate",
        "category");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1289159373:  // expiry
          return _expiry;
        case 575402001:  // currency
          return _currency;
        case 1673913084:  // unitAmount
          return _unitAmount;
        case -292854225:  // unitName
          return _unitName;
        case 2053402093:  // unitNumber
          return _unitNumber;
        case -1770633379:  // underlying
          return _underlying;
        case -1396196922:  // basket
          return _basket;
        case 1755448466:  // firstDeliveryDate
          return _firstDeliveryDate;
        case -233366664:  // lastDeliveryDate
          return _lastDeliveryDate;
        case 50511102:  // category
          return _category;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CommodityForwardSecurityBean> builder() {
      throw new UnsupportedOperationException("CommodityForwardSecurityBean is an abstract class");
    }

    @Override
    public Class<? extends CommodityForwardSecurityBean> beanType() {
      return CommodityForwardSecurityBean.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code expiry} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExpiryBean> expiry() {
      return _expiry;
    }

    /**
     * The meta-property for the {@code currency} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<CurrencyBean> currency() {
      return _currency;
    }

    /**
     * The meta-property for the {@code unitAmount} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> unitAmount() {
      return _unitAmount;
    }

    /**
     * The meta-property for the {@code unitName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UnitBean> unitName() {
      return _unitName;
    }

    /**
     * The meta-property for the {@code unitNumber} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> unitNumber() {
      return _unitNumber;
    }

    /**
     * The meta-property for the {@code underlying} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalIdBean> underlying() {
      return _underlying;
    }

    /**
     * The meta-property for the {@code basket} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Set<FutureBundleBean>> basket() {
      return _basket;
    }

    /**
     * The meta-property for the {@code firstDeliveryDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ZonedDateTimeBean> firstDeliveryDate() {
      return _firstDeliveryDate;
    }

    /**
     * The meta-property for the {@code lastDeliveryDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ZonedDateTimeBean> lastDeliveryDate() {
      return _lastDeliveryDate;
    }

    /**
     * The meta-property for the {@code category} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ContractCategoryBean> category() {
      return _category;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
