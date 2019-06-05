package gr.cti.gaia.comfort.checker.dto;

import gr.cti.gaia.comfort.checker.util.ComfortUtils;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HourData {
    private long timestamp;
    private Double temperature;
    private Double humidity;
    private Double luminosity;
    private long comfortLevel;
    
    /**
     * Update the comfort level
     * @param clothes the level of clothing to use
     */
    public void updateComfortLevel(final double clothes) {
        comfortLevel = Math.round(ComfortUtils.pmv(temperature, temperature, 0.1, humidity, 1.1, clothes, 0).getPmv());
    }
    
}

