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
  protected String processName;
  protected String processCode;
  protected String executionTagValue;

  public boolean loggingEventListenerInit(IVariables variables) throws HopException {

    String processNameParam = variables.getVariable(Defaults.VAR_PARM_PROCESS_IDENTIFIER_NAME);
    if (!Utils.isEmpty(processNameParam)) {
      throw new HopException("CUSTOM_LOGGER_PARAM_PROC_IDENTIFIER variable has not been set!");
    }

    this.processName = variables.getVariable(processNameParam);
    if (!Utils.isEmpty(processNameParam)) {
      throw new HopException(processNameParam + " parameter value has not been set!");
    }

    String processCodeParam = variables.getVariable(Defaults.VAR_PARM_PROCESS_CODE_NAME);
    if (!Utils.isEmpty(processCodeParam)) {
      this.processCode = variables.getVariable(processCodeParam);
    }

    this.executionTagValue = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());

    return postEventListenerInit(variables);
  }

  protected abstract boolean postEventListenerInit(IVariables variables) throws HopException;
}
