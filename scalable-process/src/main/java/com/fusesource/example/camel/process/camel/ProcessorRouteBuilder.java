/**
  * Copyright 2012 FuseSource
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.fusesource.example.camel.process.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.spi.IdempotentRepository;
import org.example.model.ObjectFactory;

import com.fusesource.example.camel.logic.NonRecoverableExternalServiceException;
import com.fusesource.example.camel.logic.RecoverableExternalServiceException;


public class ProcessorRouteBuilder extends RouteBuilder {

    protected static final String ROUTE_ID_BASE = ProcessorRouteBuilder.class
            .getPackage().getName() + ".processor";

    public static final String HANDLE_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".handleRecord";

    public static final String HANDLE_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + HANDLE_RECORD_ROUTE_ID;

    public static final String TRANSFORM_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".transformRecord";

    public static final String TRANSFORM_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + TRANSFORM_RECORD_ROUTE_ID;

    public static final String PROCESS_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".processRecord";

    public static final String PROCESS_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + PROCESS_RECORD_ROUTE_ID;

    public static final String PERSIST_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".persistRecord";

    public static final String PERSIST_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + PERSIST_RECORD_ROUTE_ID;
    
    public static final String ERROR_ROUTE_ROUTE_ID = ROUTE_ID_BASE + "error";
    
    public static final String ERROR_ROUTE_ENDPOINT_URI = "direct:" + ERROR_ROUTE_ROUTE_ID;

    private String recordsQueueName;
    private String alternatePersistEndpointUri;
    private IdempotentRepository<String> idempotentRepository;
    private int maxConcurrentConsumers;
    private String errorQueueName;

    @Override
    public void configure() throws Exception {
        JaxbDataFormat jbdf = new JaxbDataFormat();
        jbdf.setContextPath(ObjectFactory.class.getPackage().getName());
        
        // From JMX queue
        //   Using a TX defined by "JMS_PROPAGATION_REQUIRED"
        //   Unmarshal XML
        //   Log record info "Handling record ${body.id}."
        //   Transform sub-routine
        //   Remove duplicates using record ID "${in.body.id}"
        //   Process sub-routine
        //   Persist sub-routine
        
        // Transform sub-routine
        //   Call recordProcessor method transform
        
        // Process sub-routine
        //   Call recordProcessor method processRecord
        //     If recoverable exception retry once and log info
        //     If non-recoverable exception, log info and send to error sub-routine
        //       "Terminal error processing ${in.body}.  Failing-fast by forwarding to error destination. ${exception.stacktrace}"
        
        // Persist sub-routine
        //   Using a Tx defined by "JDBC_PROPAGATION_REQUIRES_NEW", save the record
    }

    public String getRecordsQueueName() {
        return recordsQueueName;
    }

    public void setRecordsQueueName(String recordsQueueName) {
        this.recordsQueueName = recordsQueueName;
    }

    public String getAlternatePersistEndpointUri() {
        return alternatePersistEndpointUri;
    }

    public void setAlternatePersistEndpointUri(
            String alternatePersistEndpointUri) {
        this.alternatePersistEndpointUri = alternatePersistEndpointUri;
    }
    
    public IdempotentRepository<String> getIdempotentRepository() {
        return idempotentRepository;
    }

    public void setIdempotentRepository(
            IdempotentRepository<String> idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
    }

    public int getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    public void setMaxConcurrentConsumers(int maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }
    
    public String getErrorQueueName() {
        return errorQueueName;
    }

    public void setErrorQueueName(String errorQueueName) {
        this.errorQueueName = errorQueueName;
    }

    protected String getPersistEndpointUri() {
        if (alternatePersistEndpointUri != null) {
            return alternatePersistEndpointUri;
        } else {
            return "ibatis:com.fusesource.example.camel.process.insertRecord?statementType=Insert";
        }
    }

    protected String getHandleRecordSourceUri() {
        return "activemq:queue:" + recordsQueueName
                + "?maxConcurrentConsumers=" + maxConcurrentConsumers;
    }
    
    protected String getErrorDestinationUri() {
        return "activemq:queue:" + errorQueueName;
    }
}
