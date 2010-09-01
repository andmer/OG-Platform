/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.master.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.opengamma.util.test.DBTest;

/**
 * Test DbSecurityMaster.
 */
public class DbSecurityMasterTest extends DBTest {

  private static final Logger s_logger = LoggerFactory.getLogger(DbSecurityMasterTest.class);

  private DbSecurityMaster _secMaster;

  public DbSecurityMasterTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
    s_logger.info("running testcases for {}", databaseType);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    final String contextLocation = "config/test-master-context.xml";
    final ApplicationContext context = new FileSystemXmlApplicationContext(contextLocation);
    _secMaster = (DbSecurityMaster) context.getBean(getDatabaseType()+"DbSecurityMaster");
  }

  @After
  public void tearDown() throws Exception {
    super.tearDown();
    _secMaster = null;
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_basics() throws Exception {
    assertNotNull(_secMaster);
    assertEquals(true, _secMaster.getIdentifierScheme().equals("DbSec"));
    assertNotNull(_secMaster.getJdbcTemplate());
    assertNotNull(_secMaster.getTimeSource());
    assertNotNull(_secMaster.getDbHelper());
    assertNotNull(_secMaster.getWorkers());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals("DbSecurityMaster[DbSec]", _secMaster.toString());
  }

}
