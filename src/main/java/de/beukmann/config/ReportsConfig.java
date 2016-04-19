package de.beukmann.config;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Configuration
public class ReportsConfig {

	
	@Bean(name={"dailyTemplate"})
	public JasperReport dailyTemplate() throws JRException{
		InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream("daily_orders.jasper");
		return (JasperReport) JRLoader.loadObject(jasperStream);
	}
	@Bean(name={"monthlyTemplate"})
	public JasperReport monthlyTemplate() throws JRException{
		InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream("monthly_orders.jasper");
		return (JasperReport) JRLoader.loadObject(jasperStream);
	}
}
