package gr.cti.gaia.comfort.checker.util;

import gr.cti.gaia.comfort.checker.dto.PMVData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComfortUtils {
    
    /**
     * @param ta  Air temperature [°C]
     * @param tr  Mean radiant temperature [°C]
     * @param vel Air velocity [m/s]
     * @param rh  Relative humidity [%]
     * @param met Metabolic rate [met]
     * @param clo Clothing insulation level [clo]
     * @param wme External work [W/m^2] (typically zero)
     * @return PMVData
     */
    public static PMVData pmv(final double ta, final double tr, final double vel, final double rh, final double met, final double clo, final double wme) {
        
        double pa = rh * 10 * Math.exp(16.6536 - 4030.183 / (ta + 235));
        double icl = 0.155 * clo;
        ;
        double m = met * 58.15;
        ;
        double w = wme * 58.15;
        double mw = m - w;
        ;
        double fcl;
        if (icl <= 0.078) {
            fcl = 1 + (1.29 * icl);
        } else {
            fcl = 1.05 + (0.645 * icl);
        }
        
        
        double hcf = 12.1 * Math.sqrt(vel);
        double taa = ta + 273;
        double tra = tr + 273;
        double tcla = taa + (35.5 - ta) / (3.5 * icl + 0.1);
        
        double p1 = icl * fcl;
        double p2 = p1 * 3.96;
        double p3 = p1 * 100;
        double p4 = p1 * taa;
        double p5 = (308.7 - 0.028 * mw) + (p2 * Math.pow(tra / 100, 4));
        double xn = tcla / 100;
        double xf = tcla / 50;
        double eps = 0.00015;
        
        double n = 0;
        double hc = 0;
        while (Math.abs(xn - xf) > eps) {
            xf = (xf + xn) / 2;
            double hcn = 2.38 * Math.pow(Math.abs(100.0 * xf - taa), 0.25);
            if (hcf > hcn) {
                hc = hcf;
            } else {
                hc = hcn;
            }
            xn = (p5 + p4 * hc - p2 * Math.pow(xf, 4)) / (100 + p3 * hc);
            n += 1;
            if (n > 150) {
                System.out.println("Max iterations exceeded");
                return PMVData.builder().build();
            }
        }
        double tcl = 100 * xn - 273;
        
        double hl1 = 3.05 * 0.001 * (5733 - (6.99 * mw) - pa);
        double hl2;
        if (mw > 58.15) {
            hl2 = 0.42 * (mw - 58.15);
        } else {
            hl2 = 0;
        }
        double hl3 = 1.7 * 0.00001 * m * (5867 - pa);
        double hl4 = 0.0014 * m * (34 - ta);
        double hl5 = 3.96 * fcl * (Math.pow(xn, 4) - Math.pow(tra / 100, 4));
        double hl6 = fcl * hc * (tcl - ta);
        double ts = 0.303 * Math.exp(-0.036 * m) + 0.028;
        double pmv = ts * (mw - hl1 - hl2 - hl3 - hl4 - hl5 - hl6);
        double ppd = 100.0 - 95.0 * Math.exp(-0.03353 * Math.pow(pmv, 4.0) - 0.2179 * Math.pow(pmv, 2.0));
        
        return PMVData.builder().pmv(pmv).ppd(ppd).build();
    }
}

