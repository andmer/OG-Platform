/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.component.tool;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.beans.MetaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.component.ComponentInfo;
import com.opengamma.component.ComponentManager;
import com.opengamma.component.ComponentRepository;
import com.opengamma.component.ComponentServer;
import com.opengamma.component.factory.ComponentInfoAttributes;
import com.opengamma.component.rest.RemoteComponentServer;
import com.opengamma.financial.tool.ToolContext;

/**
 * Utilities that assist with obtaining a tool context.
 * <p>
 * This is a thread-safe static utility class.
 */
public final class ToolContextUtils {

  private static final Logger s_logger = LoggerFactory.getLogger(ToolContextUtils.class);

  /**
   * The default classifier chain for selecting components from a server
   */
  private static final List<String> DEFAULT_CLASSIFIER_CHAIN =
      Arrays.asList("main", "combined", "shared", "central", "default");

  /**
   * Restricted constructor.
   */
  private ToolContextUtils() {
  }

  //-------------------------------------------------------------------------
  /**
   * Uses a {@code ComponentManager} or a {@code ComponentServer} to start and load a {@code ToolContext}.
   * <p>
   * The context should be closed after use.
   * 
   * @param configResourceLocation  the location of the context resource file, not null
   * @param toolContextClazz  the type of tool context to return, not null
   * @return the context, not null
   */
  public static ToolContext getToolContext(String configResourceLocation, Class<? extends ToolContext> toolContextClazz) {
    return getToolContext(configResourceLocation, toolContextClazz, DEFAULT_CLASSIFIER_CHAIN);
  }

  //-------------------------------------------------------------------------
  /**
   * Uses a {@code ComponentManager} or a {@code ComponentServer} to start and load a {@code ToolContext}.
   * <p>
   * The context should be closed after use.
   *
   * @param configResourceLocation  the location of the context resource file, not null
   * @param toolContextClazz  the type of tool context to return, not null
   * @param classifierChain  the classifier chain to use when determining which components to select
   * @return the context, not null
   */
  public static ToolContext getToolContext(String configResourceLocation, Class<? extends ToolContext> toolContextClazz, List<String> classifierChain) {
    configResourceLocation = configResourceLocation.trim();
    
    if (configResourceLocation.startsWith("http://")) {
      return createToolContextByHttp(configResourceLocation, toolContextClazz, classifierChain);
      
    } else {  // use local file
      ComponentManager manager = new ComponentManager("toolcontext");
      manager.start(configResourceLocation);
      ComponentRepository repo = manager.getRepository();
      return repo.getInstance(ToolContext.class, "tool");
    }
  }

  private static ToolContext createToolContextByHttp(String configResourceLocation, Class<? extends ToolContext> toolContextClazz, List<String> classifierChain) {
    configResourceLocation = StringUtils.stripEnd(configResourceLocation, "/");
    if (configResourceLocation.endsWith("/jax") == false) {
      configResourceLocation += "/jax";
    }
    
    // Get the remote component server using the supplied URI
    RemoteComponentServer remoteComponentServer = new RemoteComponentServer(URI.create(configResourceLocation));
    ComponentServer componentServer = remoteComponentServer.getComponentServer();
    
    // Attempt to build a tool context of the specified type
    ToolContext toolContext;
    try {
      toolContext = toolContextClazz.newInstance();
    } catch (Throwable t) {
      return null;
    }
    
    // Populate the tool context from the remote component server
    for (MetaProperty<?> metaProperty : toolContext.metaBean().metaPropertyIterable()) {
      if (!metaProperty.name().equals("contextManager")) {
        try {
          ComponentInfo componentInfo = getComponentInfo(componentServer, classifierChain, metaProperty.propertyType());
          if (componentInfo == null) {
            s_logger.warn("Unable to populate tool context '" + metaProperty.name() +
                "', no appropriate component found on the server");
            continue;
          }
          String clazzName = componentInfo.getAttribute(ComponentInfoAttributes.REMOTE_CLIENT_JAVA);
          if (clazzName == null) {
            s_logger.warn("Unable to populate tool context '" + metaProperty.name() +
                "', no remote access class found");
            continue;
          }
          Class<?> clazz = Class.forName(clazzName);
          metaProperty.set(toolContext, clazz.getConstructor(URI.class).newInstance(componentInfo.getUri()));
          s_logger.info("Populated tool context '" + metaProperty.name() + "' with " + metaProperty.get(toolContext));
        } catch (Throwable ex) {
          s_logger.warn("Unable to populate tool context '" + metaProperty.name() + "': " + ex.getMessage());
        }
      }
    }
    return toolContext;
  }

  private static ComponentInfo getComponentInfo(ComponentServer componentServer, List<String> preferenceList, Class<?> type) {
    Map<String, ComponentInfo> infos = componentServer.getComponentInfoMap(type);
    if (preferenceList != null) {
      for (String preference : preferenceList) {
        ComponentInfo componentInfo = infos.get(preference);
        if (componentInfo != null) {
          return componentInfo;
        }
      }
    }
    infos.remove("test");
    if (infos.size() == 0) {
      return null;
    }
    if (infos.size() > 1) {
      s_logger.warn("Multiple remote components match: " + type.getSimpleName() + "::" + infos.keySet());
      return null;
    }
    return infos.values().iterator().next();
  }

}
