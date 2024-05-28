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
 *
 */

package org.apache.hop.log;

public class Defaults {

  public static final String VAR_CUSTOM_LOGGING_ENABLED = "CUSTOM_LOGGING_ENABLED";
  // Custom logger classname to be enabled
  public static final String VAR_CUSTOM_LOGGING_LISTENER_CLASS = "LOGGING_LISTENER_CLASS";
  // Name of the variable/parameter that will contain the process identifier value
  public static final String PROC_IDENTIFIER_VALUE_VAR_NAME = "PROC_IDENTIFIER_VALUE_VAR_NAME";
  // Name of the attribute that will contain the process identifier attribute name
  public static final String PROC_IDENTIFIER_ATTRIBUTE_NAME = "PROC_IDENTIFIER_ATTR_NAME";
  // Name of the attribute that will contain the execution timestamp. Identify the execution start instance
  public static final String EXEC_TIMESTAMP_ATTR_NAME = "EXEC_TIMESTAMP_ATTR_NAME";

  public static final String PROCESS_ID_VAR_NAME = "PROCESS_ID_VAR_NAME";


  // Hop workflow/pipeline parameter to contain output directory name. If specified it overrides the
  // LOG_OUTPUT_DIRECTORY_VAR_NAME variable
  public static final String PARM_LOG_OUTPUT_DIRECTORY = "p_logfile_dir";


  // Variable referenced by Hop tp get the current execution's filename
  public static final String VAR_LOG_FILENAME = "v_logfilename";

  // Hop variable to contain output directory name
  public static final String VAR_LOG_OUTPUT_DIRECTORY = "FILE_LOGGING_OUTPUT_DIR";
  // Set to Y if we want to always append the rows to the log file stream
  public static final String VAR_FILE_LOGGER_APPEND = "FILE_LOGGING_APPEND";
  public static final String VAR_LMS_PORT = "GRAYLOG_LMS_PORT";
  public static final String VAR_LMS_HOST = "GRAYLOG_LMS_HOST";

  // Overrides the system's domain name/ip if specified. Used in Gelf logging listener
  public static final String VAR_SOURCE_SYS_NAME = "GRAYLOG_SOURCE_SYS_NAME";

  // Hidden variable to synchronize the fact that a custom logger has already been set
  public static final String VAR_LOGGER_SET = "LOGGER_SET";
  // Hidden variable to track the name of the first object that Hop started being it a workflow or a
  // pipeline. Needed to exactly know which actor will remove the logger listener when everything
  // will complete
  public static final String VAR_MAIN_PROCESS_NAME = "MAIN_PROCESS_NAME";
}
