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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.extension.ExtensionPoint;
import org.apache.hop.core.extension.IExtensionPoint;
import org.apache.hop.core.logging.HopLogStore;
import org.apache.hop.core.logging.ILogChannel;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.log.Defaults;
import org.apache.hop.log.listeners.GelfLoggingEventListener;
import org.apache.hop.log.util.LoggingCore;
import org.apache.hop.workflow.WorkflowMeta;
import org.apache.hop.workflow.engine.IWorkflowEngine;

/** Transform That contains the basic skeleton needed to create your own plugin */
@ExtensionPoint(
    id = "WorkflowLoggingExtensionPoint",
    extensionPointId = "WorkflowStart",
    description = "Handle custom logging for a pipeline")
public class WorkflowLoggingExtensionPoint
    implements IExtensionPoint<IWorkflowEngine<WorkflowMeta>> {

  @Override
  public void callExtensionPoint(
      ILogChannel logChannel, IVariables variables, IWorkflowEngine<WorkflowMeta> workflowEngine)
      throws HopException {

    // See if logging is enabled
    //
    if (!LoggingCore.isEnabled(workflowEngine)) {
      return;
    }

    String baseProcessName = variables.getVariable(Defaults.PROCESS_IDENTIFIER_VAR_NAME);
    String executionTag = variables.getVariable(Defaults.PROCESS_EXECUTION_TAG);
    if (executionTag == null) {
      executionTag = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      variables.setVariable(Defaults.PROCESS_EXECUTION_TAG, executionTag);
    }

    String processId = variables.getVariable(Defaults.PROCESS_ID_VAR_NAME);
    String outputDir = variables.getVariable(Defaults.LOG_OUTPUT_DIRECTORY_VAR_NAME);

    String itemName = workflowEngine.getWorkflowName();
    if (variables.getVariable(Defaults.LOGGER_SET_VAR) == null) {
      GelfLoggingEventListener ls =
          new GelfLoggingEventListener(
              baseProcessName, executionTag, processId, "localhost", 12201);
      //      FileLoggingEventListener ls =
      //              new FileLoggingEventListener(baseProcessName, executionTag, processId,
      // outputDir, true);

      HopLogStore.getAppender().addLoggingEventListener(ls);
      variables.setVariable(Defaults.LOGGER_SET_VAR, "Y");
      variables.setVariable(Defaults.MAIN_PROCESS_NAME_VAR, itemName);

      workflowEngine.addWorkflowFinishedListener(
          workflowMetaIWorkflowEngine -> {
            if (variables.getVariable(Defaults.MAIN_PROCESS_NAME_VAR).equals(itemName)) {
              HopLogStore.getAppender().removeLoggingEventListener(ls);
            }
          });
    }
  }
}
