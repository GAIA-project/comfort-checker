package gr.cti.gaia.comfort.checker.dto;

import gr.cti.gaia.comfort.checker.util.ComfortUtils;
import lombok.Builder;
import lombok.Data;

import java.util.Calendar;
import java.util.TimeZone;

@Data
@Builder
public class HourData {
    private long timestamp;
    private Double temperature;
    private Double humidity;
    private Double luminosity;
    private Double noise;
    private Double motion;
    private Double pm1;
    private Double pm2;
    private Double pm10;
    private Double powerConsumption;
    private long comfortLevel;
    private int year;
    private int week;
    private int dayOfWeek;
    private int hourOfDay;
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(timestamp);
        year = calendar.get(Calendar.YEAR);
        week = calendar.get(Calendar.WEEK_OF_YEAR);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * Update the comfort level
     * @param clothes the level of clothing to use
     */
    public void updateComfortLevel(final double clothes) {
        comfortLevel = Math.round(ComfortUtils.pmv(temperature, temperature, 0.1, humidity, 1.1, clothes, 0).getPmv());
    }
    
}

