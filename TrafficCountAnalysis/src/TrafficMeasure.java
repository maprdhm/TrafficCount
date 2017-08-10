import org.joda.time.DateTime;

public class TrafficMeasure {
	private int idSensor;
	private int debit;
	private int rate;
	private DateTime date;

	
	public TrafficMeasure(int idSensor, int debit, int rate, DateTime date2) {
		this.idSensor = idSensor;
		this.debit = debit;
		this.rate = rate;
		this.date = date2;
	}
	
	public int getIdSensor() {
		return idSensor;
	}
	
	public int getDebit() {
		return debit;
	}
	
	public int getRate() {
		return rate;
	}
	
	public DateTime getDate() {
		return date;
	}
}
