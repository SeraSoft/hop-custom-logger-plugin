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
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.engine.IPipelineEngine;

/** Transform That contains the basic skeleton needed to create your own plugin */
@ExtensionPoint(
    id = "PipelineLoggingExtensionPoint",
    extensionPointId = "PipelineStartThreads",
    description = "Handle custom logging for a pipeline")
public class PipelineLoggingExtensionPoint extends BaseLoggingExtensionPoint
    implements IExtensionPoint<IPipelineEngine<PipelineMeta>> {

  @Override
  public void callExtensionPoint(
      ILogChannel logChannel, IVariables variables, IPipelineEngine<PipelineMeta> pipelineEngine)
      throws HopException {

    // Check if logging is enabled
    if (Utils.isEmpty(variables.getVariable(Defaults.VAR_CUSTOM_LOGGING_ENABLED))
            || variables.getVariable(Defaults.VAR_CUSTOM_LOGGING_ENABLED).equals("N")) {
      return;
    }

    if (variables.getVariable(Defaults.VAR_LOGGER_SET) == null) {
      initEventListener(variables);
      if (this.ls.loggingEventListenerInit(variables)) {
        variables.setVariable(Defaults.VAR_LOGGER_SET, "Y");
        String itemName = pipelineEngine.getPipelineMeta().getName();
        variables.setVariable(Defaults.VAR_MAIN_PROCESS_NAME, itemName);

        HopLogStore.getAppender().addLoggingEventListener(ls);

        pipelineEngine.addExecutionFinishedListener(
            pipelineEventRef -> {
              if (variables.getVariable(Defaults.VAR_MAIN_PROCESS_NAME).equals(itemName)) {
                HopLogStore.getAppender().removeLoggingEventListener(ls);
              }
            });
      }
    }
  }
}
