package gr.cti.gaia.comfort.checker.service;

import gr.cti.gaia.comfort.checker.dto.HourData;
import net.sparkworks.cargo.common.dto.GroupDTO;
import net.sparkworks.cargo.common.dto.ResourceDTO;
import net.sparkworks.cargo.common.dto.data.QueryTimeRangeResourceDataResultDTO;
import net.sparkworks.cargo.common.dto.data.ResourceDataDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static gr.cti.gaia.comfort.checker.dto.PMVData.SUMMER_CLOTHES;
import static gr.cti.gaia.comfort.checker.dto.PMVData.WINTER_CLOTHES;

@Component
@ConfigurationProperties("comfort")
public class ComfortService {
    
    @Autowired
    GroupService groupService;
    @Autowired
    DataService dataService;
    
    private List<String> paths;
    
    public void setPaths(final List<String> paths) {
        this.paths = paths;
    }
    
    @PostConstruct
    public void init() {
        System.out.println(paths);
        for (final GroupDTO groupDTO : groupService.listAll()) {
            if (StringUtils.equalsAny(groupDTO.getPath(), paths.toArray(new String[]{}))) {
                System.out.println("===================================================================");
                System.out.println(groupDTO);
                final ResourceDTO temperature = groupService.getTemperatureResource(groupDTO);
                final ResourceDTO humidity = groupService.getRelativeHumidityResource(groupDTO);
                final ResourceDTO luminosity = groupService.getLuminosityResource(groupDTO);
                System.out.println(temperature);
                System.out.println(humidity);
                System.out.println(luminosity);
                
                System.out.println("-------------------------------------------------------------------");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                System.out.println(calendar.getTime());
                System.out.println("-------------------------------------------------------------------");
                SortedMap<Long, HourData> dataPoints = new TreeMap<>();
                StringBuilder sb = new StringBuilder();
                do {
                    final long to = calendar.getTimeInMillis();
                    System.out.println(calendar.getTime() + " " + sb.toString());
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    final long from = calendar.getTimeInMillis();
                    {
                        final QueryTimeRangeResourceDataResultDTO data = dataService.getData(temperature, from, to);
                        for (final ResourceDataDTO datum : data.getResults().values().iterator().next().getData()) {
                            if (!dataPoints.containsKey(datum.getTimestamp())) {
                                dataPoints.put(datum.getTimestamp(), HourData.builder().timestamp(datum.getTimestamp()).build());
                            }
                            dataPoints.get(datum.getTimestamp()).setTemperature(datum.getReading());
                        }
                    }
                    {
                        final QueryTimeRangeResourceDataResultDTO data = dataService.getData(humidity, from, to);
                        data.getResults().values().iterator().next().getData().forEach(datum -> {
                            if (!dataPoints.containsKey(datum.getTimestamp())) {
                                dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                            }
                            dataPoints.get(datum.getTimestamp()).setHumidity(datum.getReading());
                        });
                    }
                    {
                        final QueryTimeRangeResourceDataResultDTO data = dataService.getData(luminosity, from, to);
                        data.getResults().values().iterator().next().getData().forEach(datum -> {
                            if (!dataPoints.containsKey(datum.getTimestamp())) {
                                dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                            }
                            dataPoints.get(datum.getTimestamp()).setLuminosity(datum.getReading());
                        });
                    }
                    
                    sb.append(".");
                } while (calendar.get(Calendar.YEAR) > 2017);
                System.out.println();
                System.out.println("-------------------------------------------------------------------");
                
                dataPoints.values().forEach(point -> {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(point.getTimestamp());
                    if (cal.get(Calendar.MONTH) < 3 || cal.get(Calendar.MONTH) > 9) {
                        point.updateComfortLevel(WINTER_CLOTHES);
                    } else {
                        point.updateComfortLevel(SUMMER_CLOTHES);
                    }
                });
                
                final File csvOutputFile = new File(groupDTO.getName() + ".csv");
                try (final PrintWriter pw = new PrintWriter(csvOutputFile)) {
                    pw.append("Year;Month;Day;Hour;Temperature;Humidity;PMV;Luminosity\n");
                    for (final HourData value : dataPoints.values()) {
                        if (value.getTemperature() > 0) {
                            final Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(value.getTimestamp());
                            pw.append(cal.get(Calendar.YEAR) + ";" + cal.get(Calendar.MONTH) + ";" + cal.get(Calendar.DAY_OF_MONTH) + ";" + cal.get(Calendar.HOUR_OF_DAY) + ";" + String.format("%.2f", value.getTemperature()) + ";" + String.format("%.2f", value.getHumidity()) + ";" + value.getComfortLevel() + ";" + String.format("%.2f", value.getLuminosity()) + "\n");
                        }
                    }
                    pw.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
}
