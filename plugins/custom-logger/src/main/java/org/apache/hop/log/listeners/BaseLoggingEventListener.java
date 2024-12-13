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

package org.apache.hop.log.listeners;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.log.Defaults;

public abstract class BaseLoggingEventListener {

  protected String processIdentifierValueParam;
  protected String processIdenfifierAttrNameParam;
  protected String executionTimestampAttrName;
  protected String executionTimestampValue;
  protected String processId;

  public boolean loggingEventListenerInit(IVariables variables) throws HopException {

    String loggerClassName = variables.getVariable(Defaults.VAR_CUSTOM_LOGGING_LISTENER_CLASS);

    if (!loggerClassName.equals("org.apache.hop.log.listeners")) {
      String processIdentifierAttrNameParam =
          variables.getVariable(Defaults.PROC_IDENTIFIER_ATTRIBUTE_NAME);
      this.processIdenfifierAttrNameParam =
          variables.getVariable(
              processIdentifierAttrNameParam, "p_logger_proc_identifier_attribute");


      String executionTimestampAttrName = variables.getVariable(Defaults.EXEC_TIMESTAMP_ATTR_NAME);
       this.executionTimestampAttrName =
          variables.getVariable(executionTimestampAttrName, "execution_timestamp");
    }

    String processIdentifierValueParam =
        variables.getVariable(Defaults.PROC_IDENTIFIER_VALUE_VAR_NAME);
    if (Utils.isEmpty(processIdentifierValueParam)) {
      throw new HopException(Defaults.PROC_IDENTIFIER_VALUE_VAR_NAME + " variable value has not been set!");
    }

    this.processIdentifierValueParam =
        variables.getVariable(processIdentifierValueParam);

    this.executionTimestampValue = (new SimpleDateFormat("yyyyMMddHHmmssss")).format(new Date());

    String processIdVarName = variables.getVariable(Defaults.PROCESS_ID_VAR_NAME);
    String processIdParam = variables.getVariable(processIdVarName, "p_process_id");
    this.processId = variables.getVariable(processIdParam);

    return postEventListenerInit(variables);
  }

  protected abstract boolean postEventListenerInit(IVariables variables) throws HopException;
}
