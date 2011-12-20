package example.jug.camel.logic;

import org.example.model.RecordType;

import example.jug.camel.model.Record;

public interface RecordProcessor {
	
	void processRecord(Record record) throws ExternalServiceException;
	
	Record transform(RecordType recordType) throws Exception;
}
