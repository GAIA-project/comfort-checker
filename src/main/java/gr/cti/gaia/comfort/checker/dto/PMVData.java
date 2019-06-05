package gr.cti.gaia.comfort.checker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PMVData {
    
    public static final double WINTER_CLOTHES = 1.0;
    public static final double SUMMER_CLOTHES = 0.5;
    
    private Double pmv;
    private Double ppd;
}

