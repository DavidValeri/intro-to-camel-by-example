package example.jug.camel.ingest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.example.model.ObjectFactory;

import example.jug.camel.logic.NonRecoverableExternalServiceException;
import example.jug.camel.logic.RecoverableExternalServiceException;

public class SimpleFileIngestorRouteBuilder extends RouteBuilder {

	protected static final String ROUTE_ID_BASE = SimpleFileIngestorRouteBuilder.class
			.getPackage().getName() + ".fileIngestor";
	
	protected static final String READ_FILE_ROUTE_ID = ROUTE_ID_BASE
			+ ".readFile";
	
	protected static final String HANDLE_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".handleRecord";

    protected static final String HANDLE_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + HANDLE_RECORD_ROUTE_ID;
	
	protected static final String TRANSFORM_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".transformRecord";

    protected static final String TRANSFORM_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + TRANSFORM_RECORD_ROUTE_ID;

    protected static final String PROCESS_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".processRecord";

    protected static final String PROCESS_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + PROCESS_RECORD_ROUTE_ID;

    protected static final String PERSIST_RECORD_ROUTE_ID = ROUTE_ID_BASE
            + ".persistRecord";

    protected static final String PERSIST_RECORD_ROUTE_ENDPOINT_URI = "direct:"
            + PERSIST_RECORD_ROUTE_ID;
	
	protected static final Map<String, String> NAMESPACES;
	
	static {
		Map<String, String> tempNamespaces = new HashMap<String, String>();
		tempNamespaces.put("example", "http://www.example.org/model");
		
		NAMESPACES = Collections.unmodifiableMap(tempNamespaces);
	}
	
	private String sourceDirPath;
	private String doneDirPath;
	private String failDirPath;
	private String alternatePersistEndpointUri;
	
	@Override
	public void configure() throws Exception {
	    JaxbDataFormat jbdf = new JaxbDataFormat();
        jbdf.setContextPath(ObjectFactory.class.getPackage().getName());
		
		from(getFileSourceUri())
			.routeId(READ_FILE_ROUTE_ID)
			.log(LoggingLevel.INFO, "Processing file: ${header.CamelFilePath}")
			.to("validator:org/example/model/model.xsd")
			.split()
				.xpath("/example:aggregateRecord/example:record", NAMESPACES)
				.to(HANDLE_RECORD_ROUTE_ENDPOINT_URI);
		
        from(HANDLE_RECORD_ROUTE_ENDPOINT_URI)
            .routeId(HANDLE_RECORD_ROUTE_ID)
            .unmarshal(jbdf)
            .log(LoggingLevel.INFO, "Handling record ${body.id}.")
            .to(TRANSFORM_RECORD_ROUTE_ENDPOINT_URI)
            .to(PROCESS_RECORD_ROUTE_ENDPOINT_URI)
            .to(PERSIST_RECORD_ROUTE_ENDPOINT_URI);
    
        from(TRANSFORM_RECORD_ROUTE_ENDPOINT_URI)
            .routeId(TRANSFORM_RECORD_ROUTE_ID)
            .to("bean:recordProcessor?method=transform");
    
        from(PROCESS_RECORD_ROUTE_ENDPOINT_URI)
            .routeId(PROCESS_RECORD_ROUTE_ID)
            .onException(RecoverableExternalServiceException.class)
                .maximumRedeliveries(1)
                .redeliveryDelay(1000l)
                .logRetryAttempted(true)
                .logRetryStackTrace(true)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
            .end()
            .onException(NonRecoverableExternalServiceException.class)
                .log(LoggingLevel.ERROR,
                        "Terminal error processing ${in.body}.  Failing-fast."
                                + " ${exception.stacktrace}")
            .end()
            .to("bean:recordProcessor?method=processRecord");
    
        from(PERSIST_RECORD_ROUTE_ENDPOINT_URI)
            .routeId(PERSIST_RECORD_ROUTE_ID)
            .onException(SQLException.class)
                .maximumRedeliveries(1)
                .redeliveryDelay(1000l)
                .logRetryAttempted(true)
                .logRetryStackTrace(true)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
            .end()
            .transacted("JDBC_PROPAGATION_REQUIRES_NEW")
            .to(getPersistEndpointUri());
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
	
	protected String getFileSourceUri() {
		return "file://" + sourceDirPath + "?moveFailed=" + failDirPath + "&move=" + doneDirPath;
	}
	
	protected String getPersistEndpointUri() {
        if (alternatePersistEndpointUri != null) {
            return alternatePersistEndpointUri;
        } else {
            return "ibatis:example.jug.camel.process.insertRecord?statementType=insert";
        }
    }
}
