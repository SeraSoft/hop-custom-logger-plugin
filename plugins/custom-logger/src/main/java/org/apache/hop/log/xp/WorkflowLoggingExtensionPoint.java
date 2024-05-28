/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.log.xp;

import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.extension.ExtensionPoint;
import org.apache.hop.core.extension.IExtensionPoint;
import org.apache.hop.core.logging.HopLogStore;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.log.Defaults;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.engine.IWorkflowEngine;

/** Transform That contains the basic skeleton needed to create your own plugin */
@ExtensionPoint(
    id = "WorkflowLoggingExtensionPoint",
    extensionPointId = "WorkflowStart",
    description = "Handle custom logging for a pipeline")
public class WorkflowLoggingExtensionPoint extends BaseLoggingExtensionPoint
    implements IExtensionPoint<IWorkflowEngine<WorkflowMeta>> {

  @Override
  public void callExtensionPoint(
      ILogChannel logChannel, IVariables variables, IWorkflowEngine<WorkflowMeta> workflowEngine)
      throws HopException {

    // Check if logging is enabled
    if (Utils.isEmpty(variables.getVariable(Defaults.VAR_CUSTOM_LOGGING_ENABLED))
        || variables.getVariable(Defaults.VAR_CUSTOM_LOGGING_ENABLED).equals("N")) {
      return;
    }

    if (variables.getVariable(Defaults.VAR_LOGGER_SET) == null) {

      initEventListener(variables);
      setProcessId(variables);

      if (ls.loggingEventListenerInit(variables)) {
        variables.setVariable(Defaults.VAR_LOGGER_SET, "Y");
        String itemName = workflowEngine.getWorkflowName();
        variables.setVariable(Defaults.VAR_MAIN_PROCESS_NAME, itemName);

        HopLogStore.getAppender().addLoggingEventListener(ls);

        workflowEngine.addWorkflowFinishedListener(
            workflowMetaIWorkflowEngine -> {
              if (variables.getVariable(Defaults.VAR_MAIN_PROCESS_NAME).equals(itemName)) {
                HopLogStore.getAppender().removeLoggingEventListener(ls);
              }
            });
      } else {
        logChannel.logBasic(
            "WARNING! The custom logging event listener is not properly initialized and it will not be enabled.");
      }
    }
  }
}
