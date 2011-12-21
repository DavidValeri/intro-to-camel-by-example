package example.jug.camel.logic;

import org.example.model.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.jug.camel.model.Record;

public class DefaultRecordProcessor implements RecordProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultRecordProcessor.class);
	
	@Override
	public void processRecord(Record record) throws ExternalServiceException {
		LOG.debug("Processing record: {}", record.getId());
		
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			throw new ExternalServiceException(e);
		}
		LOG.info("Processed record: {}", record.getId());
	}

	@Override
	public Record transform(RecordType recordType) throws Exception {
		LOG.debug("Transforming record: {}", recordType.getId());
		Record record = new Record();
		record.setId(recordType.getId());
		record.setDate(recordType.getDate().toGregorianCalendar().getTime());
		LOG.info("Transformed record: {}", recordType.getId());
		return record;
	}
}
