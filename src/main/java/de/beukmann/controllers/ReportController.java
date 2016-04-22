package de.beukmann.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.beukmann.util.DateUtils;
import de.beukmann.util.Tuple;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@RestController
@RequestMapping("reports")
public class ReportController {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	@Qualifier("dailyTemplate")
	private JasperReport dailyTemplate;
	
	@Autowired
	@Qualifier("rangeTemplate")
	private JasperReport rangeTemplate;
	
	@RequestMapping(value = "daily/{date}.pdf", method = RequestMethod.GET)
	@ResponseBody
	public void getDailyOrders(HttpServletResponse response, @PathVariable  @DateTimeFormat(iso=ISO.DATE) LocalDate date) throws JRException, IOException, SQLException {
		Map<String, Object> params = new HashMap<>();
		params.put("ORDER_DATE", DateUtils.toDate(date));
		JasperPrint jasperPrint = JasperFillManager.fillReport(dailyTemplate, params, getConnection());
		writePdf(response, jasperPrint, String.format("%1$tY-%1$tm-%1$td", date));
	}
	
	@RequestMapping(value = "monthly/{year}-{month}.pdf", method = RequestMethod.GET)
	@ResponseBody
	public void getMonthlyOrders(HttpServletResponse response, @PathVariable  int year, @PathVariable  int month ) throws JRException, IOException, SQLException {
		Tuple<Date, Date> firstLast = DateUtils.getFirstLastDayOfMonth(month, year);
		Map<String, Object> params = new HashMap<>();
		params.put("ORDER_DATE_FROM", firstLast._1);
		params.put("ORDER_DATE_TO", firstLast._2);
		JasperPrint jasperPrint = JasperFillManager.fillReport(rangeTemplate, params, getConnection());
		writePdf(response, jasperPrint, String.format("monthly-%d-%02d", year, month));
	}
	@RequestMapping(value = "weekly/{year}-{week}.pdf", method = RequestMethod.GET)
	public void getWekleyOrders(HttpServletResponse response, @PathVariable  int year, @PathVariable  int week) throws JRException, IOException, SQLException {
		LocalDate[] weekdays = DateUtils.getWeek(week, year);
		Map<String, Object> params = new HashMap<>();
		params.put("ORDER_DATE_FROM", DateUtils.toDate(weekdays[0]));
		params.put("ORDER_DATE_TO", DateUtils.toDate(weekdays[6]));
		JasperPrint jasperPrint = JasperFillManager.fillReport(rangeTemplate, params, getConnection());
		writePdf(response, jasperPrint, String.format("weekly-%d-%02d", year,week ));
	}
	
	private void writePdf(HttpServletResponse response, JasperPrint jasperPrint, String filename) throws JRException, IOException{
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", String.format("inline; filename=%s.pdf", filename));
		final OutputStream outStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
	}
	
	private Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}
	
}
