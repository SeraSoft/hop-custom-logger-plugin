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

  public static final String VARIABLE_CUSTOM_LOGGING_ENABLED = "CUSTOM_LOGGING_ENABLED_VAR";
  public static final String PROCESS_IDENTIFIER_PARAM_NAME = "p_base_process_name";
  public static final String PROCESS_CODE_PARAM_NAME = "p_process_code";
  public static final String PROCESS_ID_VAR_NAME = "v_process_id";

  public static final String LMS_EXECUTION_TAG_ATTRIBUTE_NAME = "execution_tag";

  // Hop workflow/pipeline parameter to contain output directory name. If specified it overrides the
  // LOG_OUTPUT_DIRECTORY_VAR_NAME variable
  public static final String LOG_OUTPUT_DIRECTORY_PARAM_NAME = "p_logfile_dir";

  // Hop variable to contain output directory name
  public static final String LOG_OUTPUT_DIRECTORY_VAR_NAME = "FILE_LOGGING_OUTPUT_DIR";

  // Set to Y if we want to always append the rows to the log file stream
  public static final String FILE_LOGGER_APPEND_FLAG_VAR_NAME = "FILE_LOGGING_APPEND_FLAG";
  public static final String STREAM_TYPE = "HopEvents";

  // Hidden variable to synchronize the fact that a custom logger has already been set
  public static final String LOGGER_SET_VAR = "LOGGER_SET";

  // Hidden variable to track the name of the first object that Hop started being it a workflow or a
  // pipeline. Needed to exactly know which actor will remove the logger listener when everything
  // will complete
  public static final String MAIN_PROCESS_NAME_VAR = "MAIN_PROCESS_NAME";
  public static final String LMS_PORT_VAR_NAME = "LMS_PORT";
  public static final String LMS_HOST_VAR_NAME = "LMS_HOST";

  // Overrides the system's domain name/ip if specified. Used in Gelf logging listener
  public static final String SOURCE_SYS_VAR_NAME = "SOURCE_SYS_NAME";
}
