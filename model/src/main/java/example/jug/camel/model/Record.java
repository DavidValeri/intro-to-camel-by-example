package example.jug.camel.model;

import java.util.GregorianCalendar;

public class Record {
	
	private String id;
	private GregorianCalendar date;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public GregorianCalendar getDate() {
		return date;
	}
	
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
}
