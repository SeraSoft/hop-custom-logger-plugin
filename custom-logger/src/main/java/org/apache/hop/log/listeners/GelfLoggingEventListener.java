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

import java.net.InetSocketAddress;
import java.util.List;
import org.apache.hop.core.Const;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.logging.*;
import org.apache.hop.core.util.Utils;
import org.apache.hop.core.variables.IVariables;
import org.apache.hop.log.Defaults;
import org.apache.hop.log.util.HopLoglineFormatter;
import org.graylog2.gelfclient.*;
import org.graylog2.gelfclient.transport.GelfTransport;

public class GelfLoggingEventListener implements IHopLoggingEventListener {

  private String executionTag;
  private String processId;
  private String logChannelId;
  private String processName;
  private String sourceSys;
  private String hostname;
  private String port;
  private HopLoglineFormatter layout;
  private GelfConfiguration config;
  private GelfTransport transport;
  private HopException exception;

  /**
   * Log all log lines to the specified file
   *
   * @throws HopException
   */
  public GelfLoggingEventListener() throws HopException {
    this(null);
  }

  /**
   * Log only lines belonging to the specified log channel ID or one of it's children
   * (grandchildren) to the specified file.
   *
   * @throws HopException
   */
  public GelfLoggingEventListener(String logChannelId) throws HopException {
    this.logChannelId = logChannelId;
  }

  public boolean loggingEventListenerInit(IVariables variables) throws HopException {

    this.processName = variables.getVariable(Defaults.PROCESS_IDENTIFIER_PARAM_NAME);
    this.executionTag = variables.getVariable(Defaults.LMS_EXECUTION_TAG_ATTRIBUTE_NAME);
    this.processId = variables.getVariable(Defaults.PROCESS_ID_VAR_NAME);

    this.layout = new HopLoglineFormatter(processName, executionTag, processId, true);

    this.hostname = variables.getVariable(Defaults.LMS_HOST_VAR_NAME);
    this.port = variables.getVariable(Defaults.LMS_PORT_VAR_NAME);

    if (Utils.isEmpty(this.hostname) || Utils.isEmpty(this.port)) {
      return false;
    }

    this.sourceSys =
        (variables.getVariable(Defaults.SOURCE_SYS_VAR_NAME) != null
            ? variables.getVariable(Defaults.SOURCE_SYS_VAR_NAME)
            : Const.getHostnameReal());

    this.config =
        new GelfConfiguration(new InetSocketAddress(this.port, Integer.parseInt(this.port)))
            .transport(GelfTransports.UDP)
            .queueSize(512)
            .connectTimeout(5000)
            .reconnectDelay(1000)
            .tcpNoDelay(true)
            .sendBufferSize(32768);
    this.transport = GelfTransports.create(this.config);

    return true;
  }

  @Override
  public void eventAdded(HopLoggingEvent event) {

    boolean blocking = true;
    try {
      Object messageObject = event.getMessage();
      if (messageObject instanceof LogMessage) {
        boolean sendMessage;

        if (logChannelId == null) {
          sendMessage = true;
        } else {
          LogMessage message = (LogMessage) messageObject;
          // This should be fast enough cause cached.
          List<String> logChannelChildren =
              LoggingRegistry.getInstance().getLogChannelChildren(logChannelId);
          // This could be non-optimal, consider keeping the list sorted in the logging registry
          sendMessage = Const.indexOfString(message.getLogChannelId(), logChannelChildren) >= 0;
        }

        LogLevel logLevel = ((LogMessage) messageObject).getLevel();

        if (sendMessage) {
          GelfMessageBuilder msgBuilder = new GelfMessageBuilder("", this.sourceSys);
          GelfMessage msg =
              msgBuilder
                  .message(((LogMessage) messageObject).getMessage())
                  .additionalField("_stream_type", Defaults.STREAM_TYPE)
                  .additionalField("_process_name", this.processName)
                  // TODO: Temporarily commented because is not generated as the very first thing so
                  // there are lines that has this field missing
                  // .additionalField("_process_id", this.processId)
                  .additionalField("_execution_tag", this.executionTag)
                  .additionalField("_path", ((LogMessage) messageObject).getSubject())
                  .fullMessage(layout.format(event))
                  .level(
                      logLevel.equals(LogLevel.ERROR)
                          ? GelfMessageLevel.ERROR
                          : GelfMessageLevel.INFO)
                  .timestamp(event.timeStamp)
                  .build();
          if (blocking) {
            // Blocks until there is capacity in the queue
            transport.send(msg);
          } else {
            // Returns false if there isn't enough room in the queue
            boolean enqueued = transport.trySend(msg);
          }
        }
      }
    } catch (InterruptedException e) {
      exception =
          new HopException(
              "Unable to send message to Graylog Server '" + hostname + ":" + port + "'", e);
    }
  }
}
