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
import java.util.List;
import org.apache.commons.vfs2.FileObject;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.logging.*;
import org.apache.hop.core.vfs.HopVfs;
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
   * @param outputDir
   * @param append
   * @param baseProcessName
   * @throws HopException
   */
  public FileLoggingEventListener(
      String baseProcessName,
      String executionTag,
      String processId,
      String outputDir,
      boolean append)
      throws HopException {
    this(null, baseProcessName, executionTag, processId, outputDir, append);
  }

  /**
   * Log only lines belonging to the specified log channel ID or one of it's children
   * (grandchildren) to the specified file.
   *
   * @param logChannelId
   * @param outputDir
   * @param append
   * @param baseProcessName
   * @throws HopException
   */
  public FileLoggingEventListener(
      String logChannelId,
      String baseProcessName,
      String executionTag,
      String processId,
      String outputDir,
      boolean append)
      throws HopException {
    this.logChannelId = logChannelId;
    this.filename = outputDir + File.separator + baseProcessName + "_"  + executionTag + ".log" ;
    this.layout = new HopLoglineFormatter(baseProcessName, executionTag, processId, true);
    this.exception = null;

    file = HopVfs.getFileObject(filename);
    outputStream = null;
    try {
      outputStream = HopVfs.getOutputStream(file, append);
    } catch (Exception e) {
      throw new HopException(
          "Unable to create a logging event listener to write to file '" + filename + "'", e);
    }
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
