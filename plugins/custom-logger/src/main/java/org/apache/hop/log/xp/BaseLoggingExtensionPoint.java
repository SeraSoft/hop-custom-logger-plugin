/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.log.xp;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.log.Defaults;
import org.apache.hop.log.listeners.ICustomLoggingEventListener;

import java.util.UUID;

public class BaseLoggingExtensionPoint {

  protected ICustomLoggingEventListener ls;

  public boolean initEventListener(IVariables variables) throws HopException {


    String loggerClass = variables.getVariable(Defaults.VAR_CUSTOM_LOGGING_LISTENER_CLASS);
    if (loggerClass != null) {
      try {
        Class leClass = Class.forName(loggerClass);
        ls = (ICustomLoggingEventListener) leClass.newInstance();
      } catch (ClassNotFoundException e) {
        throw new HopException("Class " + loggerClass + " not found. Check that custom logger plugin is correctly deployed");
      } catch (InstantiationException e) {
        throw new HopException("Unable to instantiate class " + loggerClass);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new HopException("CUSTOM_LOGGING_LISTENER_CLASS variable not present or value not specified!");
    }
    // Generate process identifier
    String processIdentifier = UUID.randomUUID().toString();

    return true;
  }

  protected void setProcessId(IVariables variables) {

    String processIdVarName = variables.getVariable(Defaults.PROCESS_ID_VAR_NAME);
    String processIdParam = variables.getVariable(processIdVarName, "p_process_id");
    String generatedProcId = UUID.randomUUID().toString();
    variables.setVariable(processIdParam, generatedProcId);

  }
}
