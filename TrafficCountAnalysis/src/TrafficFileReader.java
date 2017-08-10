import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.vividsolutions.jts.geom.Coordinate;

public class TrafficFileReader {

	public static Map<Integer, Sensor> readSensors(String fileName) {
		BufferedReader br = null;
		FileReader fr = null;
		Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] splitString = sCurrentLine.split("\t");
				String[] coordsString = splitString[1].split(" ");
				Sensor sensor = new Sensor(Integer.parseInt(splitString[0]),null, new Coordinate(Double.parseDouble(coordsString[0]), Double.parseDouble(coordsString[1])));
				sensors.put(sensor.getId(), sensor);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return sensors;
	}
	
	
	public static List<TrafficMeasure> readMeasuresFiles(Map<Integer, Sensor> sensors, File folder) {
    	BufferedReader br = null;
		FileReader fr = null;
		List<TrafficMeasure> measures = new ArrayList<>();
		
		
		List<Integer> sensorNotReferenced = new ArrayList<>();
	    for (final File fileEntry : folder.listFiles()) {
			try {
				fr = new FileReader(fileEntry);
				br = new BufferedReader(fr);
				
				//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				String sCurrentLine;
				sCurrentLine = br.readLine();
				while ((sCurrentLine = br.readLine()) != null) {
					String[] splitString = sCurrentLine.split("\t");
					
					if(sensors.get(Integer.parseInt(splitString[0])) == null){
						if(!sensorNotReferenced.contains(Integer.parseInt(splitString[0]))){
							System.out.println(fileEntry.getName()+ " "+splitString[0]);
							sensorNotReferenced.add(Integer.parseInt(splitString[0]));
						}
					}
					else {
						if(sensors.get(Integer.parseInt(splitString[0])).getName() == null)
							sensors.get(Integer.parseInt(splitString[0])).setName(splitString[1]);
						
						//if(splitString[4].length() <= 10) { // day data
						if(splitString[4].length() > 10) {
							DateTime date = new DateTime(formatter.parse(splitString[4]));
							
							if(date.isAfter(new DateTime(2013,4,29,23,59)) && date.isBefore(new DateTime(2013,6,2,23,59))){
								TrafficMeasure measure = new TrafficMeasure(Integer.parseInt(splitString[0]),
										Integer.parseInt(splitString[2]), 
										Integer.parseInt(splitString[3]), 
										date);
								measures.add(measure);
								sensors.get(Integer.parseInt(splitString[0])).addMeasure(measure);
							}
						}
					}
				}
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		    System.out.println(sensorNotReferenced.size()+ " sensors not referenced");
	    }

	    return measures;
	}
}
