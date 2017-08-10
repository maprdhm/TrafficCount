import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Main {
	
	public static void main(String[] args) {
		Map<Integer, Sensor> sensors = TrafficFileReader.readSensors("sensors.txt");
		System.out.println(sensors.size()+" sensor(s) detected");
		List<TrafficMeasure> measures = TrafficFileReader.readMeasuresFiles(sensors, new File("2013"));
		System.out.println(measures.size()+" measures found");
		
		//countNegativeValues(measures);
		
		//computeAvgDay(sensors, measures);
		
		computeAvgDayByHour(sensors, measures);
	}

	private static void computeAvgDayByHour(Map<Integer, Sensor> sensors, List<TrafficMeasure> measures) {
		for(Sensor sensor: sensors.values()){
			Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
			Map<Integer, Integer> mapHourCount=new LinkedHashMap<>();
			
			for(TrafficMeasure measure : sensor.getMeasures()){
				//Monday, Tuesday an Thursday
				if(measure.getDate().getDayOfWeek()==1 ||measure.getDate().getDayOfWeek()==2 ||measure.getDate().getDayOfWeek()==4){
					if(measure.getDate().getDayOfYear() != new DateTime(2013,5,9,0,0,0).getDayOfYear() && 
						measure.getDate().getDayOfYear() != new DateTime(2013,5,20,0,0,0).getDayOfYear()){
						if(measure.getDebit()>-1 && measure.getRate()>-1 && measure.getDebit() <10000){
							if(measure.getIdSensor()<570 || measure.getIdSensor()>575){
								int hour = measure.getDate().getHourOfDay();
								if(hour%2 ==0)
									hour ++;
	
								if(map.containsKey(hour))
									map.put(hour, map.get(hour) + measure.getDebit());
								else map.put(hour, measure.getDebit());
								
								if(mapHourCount.containsKey(hour))
									mapHourCount.put(hour, mapHourCount.get(hour)+1);
								else mapHourCount.put(hour, 1);
							}
						}
					}
				}
			}
			
			for(Integer hour : map.keySet()){
				if(mapHourCount.get(hour) >= 5){
					map.put(hour, map.get(hour)/mapHourCount.get(hour));
					sensor.setMapHourTraffic(map);
				}
			}
		}
		
		writeSensorAvgHour(sensors);	
	}
	

	private static void writeSensorAvgHour(Map<Integer, Sensor> sensors) {
		FileWriter ffw = null;
		try {
			int cptHour=1;
			
			while(cptHour <24){
				String fileName = "sensorsCountJulienHour_"+ cptHour +".txt";
				ffw = new FileWriter(fileName);
				
				for (Sensor sensor : sensors.values()){
					if(sensor.getMapHourTraffic().containsKey(cptHour)){
						String s = sensor.getCoordinate().x + "\t" + sensor.getCoordinate().y + "\t"+ sensor.getMapHourTraffic().get(cptHour);
						ffw.write(s);
						ffw.write("\n");
					}
				}
				ffw.close();
				cptHour += 2;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	//Foreach sensor compute an average dayly count
	private static void computeAvgDay(Map<Integer, Sensor> sensors, List<TrafficMeasure> measures) {
		for(Sensor sensor: sensors.values()){
			int dayCount = 0;
			int vehiculeCount = 0;
			for(TrafficMeasure measure : sensor.getMeasures()){
				//Monday, Tuesday an Thursday
				if(measure.getDate().getDayOfWeek()==1 ||measure.getDate().getDayOfWeek()==2 ||measure.getDate().getDayOfWeek()==4){
					if(measure.getDate().getDayOfYear() != new DateTime(2013,5,9,0,0,0).getDayOfYear() && 
						measure.getDate().getDayOfYear() != new DateTime(2013,5,20,0,0,0).getDayOfYear()){
						if(measure.getDebit()>-1 && measure.getRate()>-1 && measure.getDebit() <10000 && measure.getRate() <100){
							dayCount++;
							vehiculeCount += measure.getDebit();
						}
					}
				}
			}
			if(dayCount>=5){
				int avg = vehiculeCount/dayCount;
				sensor.setAverageTraffic(avg);
			}
		}
		writeSensorAvg(sensors);
	}

	//Write sensor count in file
	private static void writeSensorAvg(Map<Integer, Sensor> sensors) {
		FileWriter ffw;
		try {
			ffw = new FileWriter("sensorsCountJulien.txt");
			for (Sensor sensor : sensors.values()){
				if(sensor.getAverageTraffic()!=0){
					String s = sensor.getCoordinate().x + "\t" + sensor.getCoordinate().y + "\t"+ sensor.getAverageTraffic();
					ffw.write(s);
					ffw.write("\n");
				}
			}
			ffw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//Count negative values measured by date and display a graph
	private static void countNegativeValues(List<TrafficMeasure> measures ) {

		    DateTime[] xData = new DateTime[200];
		    Double[] yData = new Double[200];
		 
		    Map<DateTime, Double> data = new TreeMap<DateTime, Double>();
		    
		    for(int i=0; i<measures.size(); i++) {
		    	if(measures.get(i).getDebit()==-1 || measures.get(i).getRate()==-1){
			    	if(data.containsKey(measures.get(i).getDate()))
			    		data.put(measures.get(i).getDate(), data.get(measures.get(i).getDate())+1);
			    	else 
			    		data.put(measures.get(i).getDate(), 1.0);
		    	}
		    }
		    
		    xData = data.keySet().toArray(new DateTime[0]);
		    for (int i=0; i<xData.length; i++){
		    	yData[i] = data.get(xData[i]);
		    }
		    
		 // Create Chart
		    XYChart chart = new XYChartBuilder().width(800).height(600).title("Negative values").xAxisTitle("date").yAxisTitle("negative values").build();

		    // Customize Chart
		    chart.getStyler().setPlotBackgroundColor(Color.WHITE);
		    chart.getStyler().setPlotGridLinesColor(new Color(255, 255, 255));
		    chart.getStyler().setChartBackgroundColor(Color.WHITE);
		    chart.getStyler().setLegendBackgroundColor(Color.WHITE);
		    chart.getStyler().setChartFontColor(Color.BLACK);
		    chart.getStyler().setChartTitleBoxBackgroundColor(Color.LIGHT_GRAY);
		    chart.getStyler().setChartTitleBoxVisible(true);
		    chart.getStyler().setChartTitleBoxBorderColor(Color.BLACK);
		    chart.getStyler().setPlotGridLinesVisible(false);
		 
		    chart.getStyler().setAxisTickPadding(20);
		    chart.getStyler().setAxisTickMarkLength(15);
		    chart.getStyler().setPlotMargin(20);
		 
		    chart.getStyler().setChartTitleFont(new Font(Font.MONOSPACED, Font.BOLD, 24));
		    chart.getStyler().setLegendFont(new Font(Font.SERIF, Font.PLAIN, 18));
		    chart.getStyler().setLegendPosition(LegendPosition.InsideSE);
		    chart.getStyler().setLegendSeriesLineLength(12);
		    chart.getStyler().setAxisTitleFont(new Font(Font.SANS_SERIF, Font.ITALIC, 18));
		    chart.getStyler().setAxisTickLabelsFont(new Font(Font.SERIF, Font.PLAIN, 11));
		    chart.getStyler().setDatePattern("dd-MMM");
		    chart.getStyler().setDecimalPattern("#0.0");
		  
		    // generates linear data
		    List<Date> xDatas = new ArrayList<Date>();
		    List<Double> yDatas = new ArrayList<Double>();
		 
		    Date date = null;
		    for (int i = 0; i < xData.length; i++) {
			      date = xData[i].toDate();
			      xDatas.add(date);
			      yDatas.add(yData[i]);
			}
		 
		    // Series
		    XYSeries series = chart.addSeries("Negative values by date", xDatas, yDatas);
		    series.setLineColor(XChartSeriesColors.RED);
		    series.setMarker(SeriesMarkers.NONE);
		    series.setLineStyle(SeriesLines.SOLID);
		 
		    // Show it
		    new SwingWrapper(chart).displayChart();
		  }
	
}