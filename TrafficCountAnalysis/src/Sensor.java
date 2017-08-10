import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;

public class Sensor {
	private int id;
	private String name;
	private Coordinate coordinate;
	private List<TrafficMeasure> measures;
	private int averageTraffic;
	private Map<Integer, Integer> mapHourTraffic;
	
	
	public Sensor(int id, String name, Coordinate coordinate, List<TrafficMeasure> measures) {
		this.id = id;
		this.name = name;
		this. coordinate = coordinate;
		this.measures = measures;
		averageTraffic=0;
		mapHourTraffic =  new LinkedHashMap<>();
	}
	
	
	public Sensor(int id, String name, Coordinate coordinate) {
		this.id = id;
		this.name = name;
		this. coordinate = coordinate;
		this.measures = new ArrayList<>();
		averageTraffic=0;
		mapHourTraffic =  new LinkedHashMap<>();
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	public List<TrafficMeasure> getMeasures() {
		return measures;
	}

	
	public void addMeasure(TrafficMeasure measure) {
		measures.add(measure);
	}
	
	public void setAverageTraffic(int averageTraffic) {
		this.averageTraffic = averageTraffic;
	}
	
	public int getAverageTraffic() {
		return averageTraffic;
	}
	
	public Map<Integer, Integer> getMapHourTraffic() {
		return mapHourTraffic;
	}
	
	public void setMapHourTraffic(Map<Integer, Integer> mapHourTraffic) {
		this.mapHourTraffic = mapHourTraffic;
	}
}
