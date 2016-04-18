package de.beukmann.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@RestController
@RequestMapping("reports")
public class ReportController {

	@Autowired
	private DataSource dataSource;
	
	@RequestMapping(value = "daily/{date}", method = RequestMethod.GET)
	@ResponseBody
	public void getDailyOrders(HttpServletResponse response, @PathVariable  @DateTimeFormat(iso=ISO.DATE) Date date) throws JRException, IOException, SQLException {
		InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream("daily_orders.jasper");
		Map<String, Object> params = new HashMap<>();
		params.put("ORDER_DATE", date);
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource.getConnection());
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", String.format("inline; filename=%1$tY-%1$tm-%1$td.pdf", date));
		final OutputStream outStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	}
}
