package gr.cti.gaia.comfort.checker.service;

import gr.cti.gaia.comfort.checker.dto.HourData;
import lombok.extern.slf4j.Slf4j;
import net.sparkworks.cargo.client.GroupClient;
import net.sparkworks.cargo.common.dto.GroupDTO;
import net.sparkworks.cargo.common.dto.ResourceDTO;
import net.sparkworks.cargo.common.dto.data.QueryTimeRangeResourceDataResultDTO;
import net.sparkworks.cargo.common.dto.data.ResourceDataDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static gr.cti.gaia.comfort.checker.dto.PMVData.SUMMER_CLOTHES;
import static gr.cti.gaia.comfort.checker.dto.PMVData.WINTER_CLOTHES;

@Slf4j
@Component
@ConfigurationProperties("comfort")
public class ComfortService {
    
    private static final int DAYS_OFFSET = +40;
    @Autowired
    GroupService groupService;
    @Autowired
    DataService dataService;
    
    private List<String> paths;
    
    public void setPaths(final List<String> paths) {
        this.paths = paths;
    }
    
    @Autowired
    GroupClient groupClient;
    
    @PostConstruct
    public void init() {
        log.info("{}", paths);
        for (final GroupDTO groupDTO : groupService.listAll()) {
            if (StringUtils.equalsAny(groupDTO.getPath(), paths.toArray(new String[]{}))) {
                final Map<Integer, Map<Integer, Map<Integer, HourData>>> dailyDataEntries = new HashMap<>();
                final Map<Integer, ArrayDeque<HourData>> dayOfWeekDataEntries = new HashMap<>();
    
                log.info("===================================================================");
                log.info("{}", groupDTO);
                final Collection<ResourceDTO> resources = groupService.getResources(groupDTO);
                final ResourceDTO temperature = groupService.getTemperatureResource(groupDTO, resources);
                final ResourceDTO humidity = groupService.getRelativeHumidityResource(groupDTO, resources);
                final ResourceDTO luminosity = groupService.getLuminosityResource(groupDTO, resources);
                final ResourceDTO noise = groupService.getNoiseResource(groupDTO, resources);
                final ResourceDTO motion = groupService.getMotionResource(groupDTO, resources);
                final ResourceDTO pm1 = groupService.getPM1Resource(groupDTO, resources);
                final ResourceDTO pm2 = groupService.getPM25Resource(groupDTO, resources);
                final ResourceDTO pm10 = groupService.getPM10Resource(groupDTO, resources);
                final ResourceDTO powerConsumption = groupService.getPowerConsumptionResource(groupDTO, resources);
                log.info("{}",temperature);
                log.info("{}",humidity);
                log.info("{}",luminosity);
                log.info("{}",noise);
                log.info("{}",motion);
                log.info("{}",powerConsumption);
                
                log.info("-------------------------------------------------------------------");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.YEAR, 2019);
                log.info("{}",calendar.getTime());
                log.info("-------------------------------------------------------------------");
                SortedMap<Long, HourData> dataPoints = new TreeMap<>();
                StringBuilder sb = new StringBuilder();
                do {
                    final long from = calendar.getTimeInMillis();
                    log.info(calendar.getTime() + " " + sb.toString());
                    calendar.add(Calendar.DAY_OF_YEAR, DAYS_OFFSET);
                    final long to = calendar.getTimeInMillis();
                    {
                        if (temperature != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(temperature, from, to);
                            for (final ResourceDataDTO datum : data.getResults().values().iterator().next().getData()) {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().timestamp(datum.getTimestamp()).build());
                                }
                                dataPoints.get(datum.getTimestamp()).setTemperature(datum.getReading());
                            }
                        }
                    }
                    {
                        if (humidity != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(humidity, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setHumidity(datum.getReading());
                            });
                        }
                    }
                    {
                        if (luminosity != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(luminosity, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setLuminosity(datum.getReading());
                            });
                        }
                    }
                    {
                        if (noise != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(noise, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setNoise(datum.getReading());
                            });
                        }
                    }
                    {
                        if (motion != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(motion, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setMotion(datum.getReading());
                            });
                        }
                    }
                    {
                        if (pm1 != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(pm1, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setPm1(datum.getReading());
                            });
                        }
                    }
                    {
                        if (pm2 != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(pm2, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setPm2(datum.getReading());
                            });
                        }
                    }
                    {
                        if (pm10 != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(pm10, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setPm10(datum.getReading());
                            });
                        }
                    }
                    {
                        if (powerConsumption != null) {
                            final QueryTimeRangeResourceDataResultDTO data = dataService.getData(powerConsumption, from, to);
                            data.getResults().values().iterator().next().getData().forEach(datum -> {
                                if (!dataPoints.containsKey(datum.getTimestamp())) {
                                    dataPoints.put(datum.getTimestamp(), HourData.builder().build());
                                }
                                dataPoints.get(datum.getTimestamp()).setTimestamp(datum.getTimestamp());
                                dataPoints.get(datum.getTimestamp()).setPowerConsumption(datum.getReading() / 1000 / 1000);
                            });
                        }
                    }
                    
                    sb.append(".");
                } while (calendar.getTimeInMillis() < System.currentTimeMillis());
                log.info("-------------------------------------------------------------------");
                
                dataPoints.values().forEach(point -> {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(point.getTimestamp());
                    if (cal.get(Calendar.MONTH) < 3 || cal.get(Calendar.MONTH) > 9) {
                        point.updateComfortLevel(WINTER_CLOTHES);
                    } else {
                        point.updateComfortLevel(SUMMER_CLOTHES);
                    }
                });
                
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("data");
                
                int row = 0;
                Row header = sheet.createRow(row++);
                header.createCell(0).setCellValue("Epoch");
                header.createCell(1).setCellValue("Year");
                header.createCell(2).setCellValue("Week");
                header.createCell(3).setCellValue("Month");
                header.createCell(4).setCellValue("Day");
                header.createCell(5).setCellValue("DayOfWeek");
                header.createCell(6).setCellValue("HourOfDay");
                header.createCell(7).setCellValue("Temperature");
                header.createCell(8).setCellValue("Humidity");
                header.createCell(9).setCellValue("PMV");
                header.createCell(10).setCellValue("Luminosity");
                header.createCell(11).setCellValue("Noise");
                header.createCell(12).setCellValue("Motion");
                header.createCell(13).setCellValue("PowerConsumption(kWh)");
                header.createCell(14).setCellValue("expectedOnWeekLevel(kWh)");
                header.createCell(15).setCellValue("expectedOnWeekLevelRate");
                header.createCell(16).setCellValue("lastYear(kWh)");
                header.createCell(17).setCellValue("lastWeek");
                header.createCell(18).setCellValue("last2Weeks");
                header.createCell(19).setCellValue("last4Weeks");
                header.createCell(20).setCellValue("pm1");
                header.createCell(21).setCellValue("pm2.5");
                header.createCell(22).setCellValue("pm10");
                
                for (final HourData value : dataPoints.values()) {
                    //data per day of week
                    if (!dayOfWeekDataEntries.containsKey(value.getDayOfWeek())) {
                        dayOfWeekDataEntries.put(value.getDayOfWeek(), new ArrayDeque<>());
                    }
                    dayOfWeekDataEntries.get(value.getDayOfWeek()).addLast(value);
                    if (dayOfWeekDataEntries.get(value.getDayOfWeek()).size() > 15) {
                        dayOfWeekDataEntries.get(value.getDayOfWeek()).removeFirst();
                    }
                    double expectedOnWeekLevel = getAvg(dayOfWeekDataEntries.get(value.getDayOfWeek()).iterator());
                    double expectedOnWeekLevelRate = 0.0;
                    if (expectedOnWeekLevel > 0) {
                        expectedOnWeekLevelRate = (value.getPowerConsumption() / expectedOnWeekLevel) * 100;
                    }
                    
                    if (!dailyDataEntries.containsKey(value.getYear())) {
                        dailyDataEntries.put(value.getYear(), new HashMap<>());
                    }
                    if (!dailyDataEntries.get(value.getYear()).containsKey(value.getWeek())) {
                        dailyDataEntries.get(value.getYear()).put(value.getWeek(), new HashMap<>());
                    }
                    if (!dailyDataEntries.get(value.getYear()).get(value.getWeek()).containsKey(value.getDayOfWeek())) {
                        dailyDataEntries.get(value.getYear()).get(value.getWeek()).put(value.getDayOfWeek(), value);
                    }
                    
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(value.getTimestamp());
                    Double lastYearConsumption = 0.0;
                    try {
                        lastYearConsumption = dailyDataEntries.get(cal.get(Calendar.YEAR) - 1).get(value.getWeek()).get(value.getDayOfWeek()).getPowerConsumption();
                    } catch (Exception e) {
                    
                    }
                    Double lastWeekConsumption = 0.0;
                    int countLastWeek1 = 0;
                    int countLastWeek2 = 0;
                    int countLastWeek3 = 0;
                    int countLastWeek4 = 0;
                    try {
                        lastWeekConsumption = dailyDataEntries.get(value.getYear()).get(cal.get(Calendar.WEEK_OF_YEAR) - 1).get(value.getDayOfWeek()).getPowerConsumption();
                        countLastWeek1++;
                    } catch (Exception e) {
                        //
                    }
                    Double last2WeekConsumption = 0.0;
                    try {
                        last2WeekConsumption = dailyDataEntries.get(value.getYear()).get(cal.get(Calendar.WEEK_OF_YEAR) - 2).get(value.getDayOfWeek()).getPowerConsumption();
                        countLastWeek2++;
                    } catch (Exception e) {
                        //
                    }
                    Double last3WeekConsumption = 0.0;
                    try {
                        last3WeekConsumption = dailyDataEntries.get(value.getYear()).get(cal.get(Calendar.WEEK_OF_YEAR) - 3).get(value.getDayOfWeek()).getPowerConsumption();
                        countLastWeek3++;
                    } catch (Exception e) {
                        //
                    }
                    Double last4WeekConsumption = 0.0;
                    try {
                        last4WeekConsumption = dailyDataEntries.get(value.getYear()).get(cal.get(Calendar.WEEK_OF_YEAR) - 4).get(value.getDayOfWeek()).getPowerConsumption();
                        countLastWeek4++;
                    } catch (Exception e) {
                    
                    }
                    double week2 = 0;
                    double week4 = 0;
                    try {
                        week2 = (lastWeekConsumption + last2WeekConsumption) / (countLastWeek1 + countLastWeek2);
                    } catch (Exception e) {
                        //
                    }
                    try {
                        week4 = (lastWeekConsumption + last2WeekConsumption + last3WeekConsumption + last4WeekConsumption) / (countLastWeek1 + countLastWeek2 + countLastWeek3 + countLastWeek4);
                    } catch (Exception e) {
                        //
                    }
    
                    if (value.getTimestamp() > System.currentTimeMillis()) {
                        continue;
                    }
                    
                    final Row newRow = sheet.createRow(row++);
                    newRow.createCell(0, CellType.NUMERIC).setCellValue(cal.getTimeInMillis());
                    newRow.createCell(1, CellType.NUMERIC).setCellValue(cal.get(Calendar.YEAR));
                    newRow.createCell(2, CellType.NUMERIC).setCellValue(cal.get(Calendar.WEEK_OF_YEAR));
                    newRow.createCell(3, CellType.NUMERIC).setCellValue(cal.get(Calendar.MONTH));
                    newRow.createCell(4, CellType.NUMERIC).setCellValue(cal.get(Calendar.DAY_OF_MONTH));
                    newRow.createCell(5, CellType.NUMERIC).setCellValue(value.getDayOfWeek());
                    newRow.createCell(6, CellType.NUMERIC).setCellValue(value.getHourOfDay());
                    newRow.createCell(7, CellType.NUMERIC).setCellValue(round(value.getTemperature()));
                    
                    newRow.createCell(8, CellType.NUMERIC).setCellValue(round(value.getHumidity()));
                    newRow.createCell(9, CellType.NUMERIC).setCellValue(value.getComfortLevel());
                    newRow.createCell(10, CellType.NUMERIC).setCellValue(round(value.getLuminosity()));
                    newRow.createCell(11, CellType.NUMERIC).setCellValue(round(value.getNoise()));
                    newRow.createCell(12, CellType.NUMERIC).setCellValue(round(value.getMotion()));
                    newRow.createCell(13, CellType.NUMERIC).setCellValue(round(value.getPowerConsumption()));
                    newRow.createCell(14, CellType.NUMERIC).setCellValue(round(expectedOnWeekLevel));
                    newRow.createCell(15, CellType.NUMERIC).setCellValue(expectedOnWeekLevelRate);
                    newRow.createCell(16, CellType.NUMERIC).setCellValue(round(lastYearConsumption));
                    newRow.createCell(17, CellType.NUMERIC).setCellValue(round(lastWeekConsumption));
                    newRow.createCell(18, CellType.NUMERIC).setCellValue(round(week2));
                    newRow.createCell(19, CellType.NUMERIC).setCellValue(round(week4));
                    newRow.createCell(20, CellType.NUMERIC).setCellValue(round(value.getPm1()));
                    newRow.createCell(21, CellType.NUMERIC).setCellValue(round(value.getPm2()));
                    newRow.createCell(22, CellType.NUMERIC).setCellValue(round(value.getPm10()));
                }
                try {
                    FileOutputStream outputStream = new FileOutputStream(groupDTO.getName() + ".xlsx");
                    workbook.write(outputStream);
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static float round(Double d) {
        return round(d, 2);
    }
    
    public static float round(Double d, int decimalPlace) {
        try {
            if (d == null || d.isNaN()) {
                return 0;
            } else {
                return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
            }
        } catch (NumberFormatException e) {
            log.error("number : " + d);
            throw e;
        }
    }
    
    private double getAvg(final Iterator<HourData> iterator) {
        double sum = 0.0;
        double count = 0.0;
        while (iterator.hasNext()) {
            final Double next = iterator.next().getPowerConsumption();
            if (next > 0) {
                sum += next;
                count++;
            }
        }
        return count == 0 ? 0.0 : sum / count;
    }
    
    
}
