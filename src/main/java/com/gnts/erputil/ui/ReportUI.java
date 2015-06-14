package com.gnts.erputil.ui;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.HashMap;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.Logger;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class ReportUI {
	HashMap hm = null;
	Connection con = null;
	String reportName;
	private static Logger logger = Logger.getLogger(ReportUI.class);
	
	public ReportUI() {
		// setExtendedState(MAXIMIZED_BOTH);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public ReportUI(HashMap map) {
		this.hm = map;
		// setExtendedState(MAXIMIZED_BOTH);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public ReportUI(HashMap map, Connection con) {
		this.hm = map;
		this.con = con;
		// setExtendedState(MAXIMIZED_BOTH);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// setTitle("Report Viewer");
	}
	
	public void setReportName(String rptName) {
		this.reportName = rptName;
	}
	
	@SuppressWarnings("deprecation")
	public void callReport(final String basepath, final String header) {
		JasperPrint jasperPrint = generateReport();
		try {
			System.out.println("Test1---->" + jasperPrint);
			System.out.println("Testpath---->" + basepath + "/WEB-INF/reports/Test");
			// JasperViewer.viewReport(jasperPrint);exportReportToPdfFile
			JasperExportManager.exportReportToPdfFile(jasperPrint, basepath + "/WEB-INF/reports/Test");
			Window window = new Window();
			// window.setSizeFull();
			// window.setResizable(true);
			window.setCaption(header);
			window.setWidth("90%");
			window.setHeight("80%");
			window.center();
			StreamSource s = new StreamResource.StreamSource() {
				@Override
				public FileInputStream getStream() {
					try {
						File f = new File(basepath + "/WEB-INF/reports/Test");
						FileInputStream fis = new FileInputStream(f);
						return fis;
					}
					catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			};
			StreamResource r = new StreamResource(s, "repy.pdf");
			Embedded e = new Embedded();
			e.setWidth("100%");
			e.setHeight("99%");
			e.setType(Embedded.TYPE_BROWSER);
			r.setMIMEType("application/pdf");
			e.setSource(r);
			window.setContent(e);
			UI.getCurrent().addWindow(window);
		}
		catch (Exception e) {
			logger.error("Error 2>", e);
			e.printStackTrace();
		}
		// JRViewer viewer = new JRViewer(jasperPrint);
		// Container c = getContentPane();
		// c.add(viewer);
		// this.setVisible(true);
	}
	
	public void callConnectionLessReport() {
		JasperPrint jasperPrint = generateEmptyDataSourceReport();
		try {
			System.out.println("Test2---->" + jasperPrint);
			JasperViewer.viewReport(jasperPrint);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// JRViewer viewer = new JRViewer(jasperPrint);
		// Container c = getContentPane();
		// c.add(viewer);
		// this.setVisible(true);
	}
	
	public void closeReport() {
		// jasperViewer.setVisible(false);
	}
	
	/** this method will call the report from data source */
	public JasperPrint generateReport() {
		try {
			if (con == null) {
				try {
					// con = Database.getConnection();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			JasperPrint jasperPrint = null;
			if (hm == null) {
				hm = new HashMap();
			}
			try {
				/**
				 * You can also test this line if you want to display report from any absolute path other than the
				 * project root path
				 */
				// jasperPrint = JasperFillManager.fillReport("F:/testreport/"+reportName+".jasper",hm, con);
				System.out.println("reportName==================" + reportName);
				jasperPrint = JasperFillManager.fillReport(reportName + ".jasper", hm, con);
				// JasperExportManager.exportReportToPdfFile(jasperPrint, "Test");
			}
			catch (JRException e) {
				logger.error("Error>3", e);
				e.printStackTrace();
			}
			return jasperPrint;
		}
		catch (Exception ex) {
			logger.error("Error>4", ex);
			ex.printStackTrace();
			return null;
		}
	}
	
	/** call this method when your report has an empty data source */
	public JasperPrint generateEmptyDataSourceReport() {
		try {
			JasperPrint jasperPrint = null;
			if (hm == null) {
				hm = new HashMap();
			}
			try {
				jasperPrint = JasperFillManager.fillReport(reportName + ".jasper", hm, new JREmptyDataSource());
				JasperExportManager.exportReportToPdfFile(jasperPrint, "Test");
			}
			catch (JRException e) {
				e.printStackTrace();
			}
			return jasperPrint;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}