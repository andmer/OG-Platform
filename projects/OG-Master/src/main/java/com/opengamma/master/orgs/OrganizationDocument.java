/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.orgs;

import java.io.Serializable;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.id.ExternalId;
import com.opengamma.id.UniqueId;
import com.opengamma.master.AbstractDocument;
import com.opengamma.util.PublicSPI;

/**
 * A document used to pass a organization into and out of the organization master.
 */
@PublicSPI
@BeanDefinition
public class OrganizationDocument extends AbstractDocument implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The organization object held by the document.
   */
  @PropertyDefinition
  private ManageableOrganization _organization;
  /**
   * The portfolio unique identifier.
   * This field is managed by the master but must be set for updates.
   */
  @PropertyDefinition
  private UniqueId _uniqueId;

  /**
   * The provider external identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   */
  @PropertyDefinition
  private ExternalId _providerId;

  /**
   * Creates an instance.
   */
  public OrganizationDocument() {
  }

  public OrganizationDocument(ManageableOrganization organization) {
    _organization = organization;
    setUniqueId(organization.getUniqueId());
  }

  //-------------------------------------------------------------------------
  @Override
  public ManageableOrganization getValue() {
    return getOrganization();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code OrganizationDocument}.
   * @return the meta-bean, not null
   */
  public static OrganizationDocument.Meta meta() {
    return OrganizationDocument.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(OrganizationDocument.Meta.INSTANCE);
  }

  @Override
  public OrganizationDocument.Meta metaBean() {
    return OrganizationDocument.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1178922291:  // organization
        return getOrganization();
      case -294460212:  // uniqueId
        return getUniqueId();
      case 205149932:  // providerId
        return getProviderId();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1178922291:  // organization
        setOrganization((ManageableOrganization) newValue);
        return;
      case -294460212:  // uniqueId
        setUniqueId((UniqueId) newValue);
        return;
      case 205149932:  // providerId
        setProviderId((ExternalId) newValue);
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
      OrganizationDocument other = (OrganizationDocument) obj;
      return JodaBeanUtils.equal(getOrganization(), other.getOrganization()) &&
          JodaBeanUtils.equal(getUniqueId(), other.getUniqueId()) &&
          JodaBeanUtils.equal(getProviderId(), other.getProviderId()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getOrganization());
    hash += hash * 31 + JodaBeanUtils.hashCode(getUniqueId());
    hash += hash * 31 + JodaBeanUtils.hashCode(getProviderId());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the organization object held by the document.
   * @return the value of the property
   */
  public ManageableOrganization getOrganization() {
    return _organization;
  }

  /**
   * Sets the organization object held by the document.
   * @param organization  the new value of the property
   */
  public void setOrganization(ManageableOrganization organization) {
    this._organization = organization;
  }

  /**
   * Gets the the {@code organization} property.
   * @return the property, not null
   */
  public final Property<ManageableOrganization> organization() {
    return metaBean().organization().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio unique identifier.
   * This field is managed by the master but must be set for updates.
   * @return the value of the property
   */
  public UniqueId getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the portfolio unique identifier.
   * This field is managed by the master but must be set for updates.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueId uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * This field is managed by the master but must be set for updates.
   * @return the property, not null
   */
  public final Property<UniqueId> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the provider external identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @return the value of the property
   */
  public ExternalId getProviderId() {
    return _providerId;
  }

  /**
   * Sets the provider external identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @param providerId  the new value of the property
   */
  public void setProviderId(ExternalId providerId) {
    this._providerId = providerId;
  }

  /**
   * Gets the the {@code providerId} property.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @return the property, not null
   */
  public final Property<ExternalId> providerId() {
    return metaBean().providerId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code OrganizationDocument}.
   */
  public static class Meta extends AbstractDocument.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code organization} property.
     */
    private final MetaProperty<ManageableOrganization> _organization = DirectMetaProperty.ofReadWrite(
        this, "organization", OrganizationDocument.class, ManageableOrganization.class);
    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueId> _uniqueId = DirectMetaProperty.ofReadWrite(
        this, "uniqueId", OrganizationDocument.class, UniqueId.class);
    /**
     * The meta-property for the {@code providerId} property.
     */
    private final MetaProperty<ExternalId> _providerId = DirectMetaProperty.ofReadWrite(
        this, "providerId", OrganizationDocument.class, ExternalId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "organization",
        "uniqueId",
        "providerId");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1178922291:  // organization
          return _organization;
        case -294460212:  // uniqueId
          return _uniqueId;
        case 205149932:  // providerId
          return _providerId;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends OrganizationDocument> builder() {
      return new DirectBeanBuilder<OrganizationDocument>(new OrganizationDocument());
    }

    @Override
    public Class<? extends OrganizationDocument> beanType() {
      return OrganizationDocument.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code organization} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ManageableOrganization> organization() {
      return _organization;
    }

    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueId> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code providerId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> providerId() {
      return _providerId;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
