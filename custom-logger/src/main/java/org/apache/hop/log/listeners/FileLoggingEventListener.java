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

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.vfs2.FileObject;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.logging.*;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.core.vfs.HopVfs;
import org.apache.hop.log.Defaults;
import org.apache.hop.log.util.HopLoglineFormatter;

public class FileLoggingEventListener implements IHopLoggingEventListener {

  private String filename;
  private FileObject file;

  public FileObject getFile() {
    return file;
  }

  private OutputStream outputStream;
  private HopLoglineFormatter layout;

  private HopException exception;
  private String logChannelId;

  /**
   * Log all log lines to the specified file
   *
   * @throws HopException
   */
  public FileLoggingEventListener() throws HopException {
    this(null);
  }

  /**
   * Log only lines belonging to the specified log channel ID or one of it's children
   * (grandchildren) to the specified file.
   *
   * @param logChannelId
   * @throws HopException
   */
  public FileLoggingEventListener(String logChannelId) throws HopException {
    this.logChannelId = logChannelId;
  }

  public boolean loggingEventListenerInit(IVariables variables) throws HopException {

    String processName = variables.getVariable(Defaults.PROCESS_IDENTIFIER_PARAM_NAME);
    String executionTag = variables.getVariable(Defaults.LMS_EXECUTION_TAG_ATTRIBUTE_NAME);
    String processId = variables.getVariable(Defaults.PROCESS_ID_VAR_NAME);


    String outputDir =
        variables.getVariable(Defaults.LOG_OUTPUT_DIRECTORY_PARAM_NAME) != null
            ? variables.getVariable(Defaults.LOG_OUTPUT_DIRECTORY_PARAM_NAME)
            : variables.getVariable(Defaults.LOG_OUTPUT_DIRECTORY_VAR_NAME);
    if (outputDir == null) return false;
    this.filename = outputDir + File.separator + processName + "_" + executionTag + ".log";

    boolean append =
            (variables.getVariable(Defaults.FILE_LOGGER_APPEND_FLAG_VAR_NAME) == null
                    ? true
                    : variables.getVariable(Defaults.FILE_LOGGER_APPEND_FLAG_VAR_NAME).equals("Y"));
    this.layout = new HopLoglineFormatter(processName, executionTag, processId, true);
    this.exception = null;

    file = HopVfs.getFileObject(filename);
    outputStream = null;
    try {
      outputStream = HopVfs.getOutputStream(file, append);
    } catch (Exception e) {
      throw new HopException(
          "Unable to create a logging event listener to write to file '" + filename + "'", e);
    }

    if (executionTag == null) {
      executionTag = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
      variables.setVariable(Defaults.LMS_EXECUTION_TAG_ATTRIBUTE_NAME, executionTag);
    }

    return true;
  }

  @Override
  public void eventAdded(HopLoggingEvent event) {

    try {
      Object messageObject = event.getMessage();
      if (messageObject instanceof LogMessage) {
        boolean logToFile = false;

        if (logChannelId == null) {
          logToFile = true;
        } else {
          LogMessage message = (LogMessage) messageObject;
          // This should be fast enough cause cached.
          List<String> logChannelChildren =
              LoggingRegistry.getInstance().getLogChannelChildren(logChannelId);
          // This could be non-optimal, consider keeping the list sorted in the logging registry
          logToFile = Const.indexOfString(message.getLogChannelId(), logChannelChildren) >= 0;
        }

        if (logToFile) {
          String logText = layout.format(event);
          outputStream.write(logText.getBytes());
          outputStream.write(Const.CR.getBytes());
          outputStream.flush();
        }
      }
    } catch (Exception e) {
      exception =
          new HopException("Unable to write to logging event to file '" + filename + "'", e);
    }
  }
}
