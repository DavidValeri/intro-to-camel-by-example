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
package com.fusesource.example.camel.ingest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.spi.IdempotentRepository;
import org.example.model.ObjectFactory;

import com.fusesource.example.camel.logic.NonRecoverableExternalServiceException;
import com.fusesource.example.camel.logic.RecoverableExternalServiceException;


public class SimpleFileIngestorRouteBuilder extends RouteBuilder {

	protected static final String ROUTE_ID_BASE = SimpleFileIngestorRouteBuilder.class
			.getPackage().getName() + ".fileIngestor";
	
	public static final String READ_FILE_ROUTE_ID = ROUTE_ID_BASE
			+ ".readFile";
	
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
	
	public static final Map<String, String> NAMESPACES;
	
	static {
		Map<String, String> tempNamespaces = new HashMap<String, String>();
		tempNamespaces.put("example", "http://www.example.org/model");
		
		NAMESPACES = Collections.unmodifiableMap(tempNamespaces);
	}
	
	private String sourceDirPath;
	private String doneDirPath;
	private String failDirPath;
	private String alternatePersistEndpointUri;
	private IdempotentRepository<String> idempotentRepository;
	
	@Override
	public void configure() throws Exception {
	    JaxbDataFormat jbdf = new JaxbDataFormat();
        jbdf.setContextPath(ObjectFactory.class.getPackage().getName());
		
     // Poll for file
        //   Log file info "Processing file: ${header.CamelFilePath}"
        //   Validate against XSD (on classpath at org/example/model/model.xsd)
        //   Split on XML nodes /example:aggregateRecord/example:record"
        //     Unmarshal XML
        //     log record info "Handling record ${body.id}."
        //     Transform sub-routine
        //     Remove duplicates using record ID "${in.body.id}"
        //     Process sub-routine
        //     Persist sub-routine
        // Move completed file to a done folder and move files with a failure to a failed folder.
        
        // Transform sub-routine
        //   Call recordProcessor method transform
        
        // Process sub-routine
        //   Call recordProcessor method processRecord
        //     If recoverable exception retry once and log info
        //     If non-recoverable exception, log info.
       
        // Persist sub-routine
        //   Using a Tx defined by "JDBC_PROPAGATION_REQUIRES_NEW", save the record
        //     if SQL error, retry once and log info
	}

	public String getSourceDirPath() {
		return sourceDirPath;
	}

	public void setSourceDirPath(String sourceDirPath) {
		this.sourceDirPath = sourceDirPath;
	}

	public String getDoneDirPath() {
		return doneDirPath;
	}

	public void setDoneDirPath(String doneDirPath) {
		this.doneDirPath = doneDirPath;
	}

	public String getFailDirPath() {
		return failDirPath;
	}

	public void setFailDirPath(String failDirPath) {
		this.failDirPath = failDirPath;
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

    protected String getFileSourceUri() {
		return "file://" + sourceDirPath + "?moveFailed=" + failDirPath + "&move=" + doneDirPath;
	}
	
	protected String getPersistEndpointUri() {
        if (alternatePersistEndpointUri != null) {
            return alternatePersistEndpointUri;
        } else {
            return "ibatis:com.fusesource.example.camel.process.insertRecord?statementType=Insert";
        }
    }
}
