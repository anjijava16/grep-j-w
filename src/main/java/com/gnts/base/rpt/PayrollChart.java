package com.gnts.base.rpt;

import java.io.Serializable;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Cursor;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.GradientColor;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

public class PayrollChart implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Chart chart = null;
	
	public PayrollChart(VerticalLayout vlchart, DataSeries dataseries) {
		chart = new Chart(ChartType.PIE);
		Configuration conf = chart.getConfiguration();
		conf.setTitle("Payroll Summary , 2014-2015");
		PlotOptionsPie plotOptions = new PlotOptionsPie();
		plotOptions.setCursor(Cursor.POINTER);
		Labels dataLabels = new Labels();
		dataLabels.setEnabled(true);
		dataLabels.setColor(SolidColor.BLACK);
		dataLabels.setConnectorColor(SolidColor.BLACK);
		dataLabels.setFormatter("''+ this.point.name +': '+ this.percentage +' %'");
		plotOptions.setDataLabels(dataLabels);
		conf.setPlotOptions(plotOptions);
		conf.setSeries(getBrowserMarketShareSeries());
		conf.disableCredits();
		chart.drawChart();
		chart.setHeight("80%");
		vlchart.addComponent(chart);
		vlchart.setComponentAlignment(chart, Alignment.MIDDLE_LEFT); 
	}
	
	public Chart getCustomPayrollChart() {
		return this.chart;
	}
	
	private DataSeries getBrowserMarketShareSeries() {
		DataSeriesItem firefox = new DataSeriesItem("IT", 45.0);
		firefox.setColor(createRadialGradient(new SolidColor(255, 128, 0), new SolidColor(128, 64, 0)));
		DataSeriesItem ie = new DataSeriesItem("Delivery", 26.8);
		ie.setColor(createRadialGradient(new SolidColor(0, 255, 255), new SolidColor(0, 128, 128)));
		DataSeriesItem chrome = new DataSeriesItem("Marketing", 12.8);
		chrome.setColor(createRadialGradient(new SolidColor(255, 255, 0), new SolidColor(128, 128, 0)));
		chrome.setSliced(true);
		chrome.setSelected(true);
		DataSeriesItem safari = new DataSeriesItem("Maintaince", 8.5);
		safari.setColor(createRadialGradient(new SolidColor(0, 128, 255), new SolidColor(0, 64, 128)));
		DataSeriesItem opera = new DataSeriesItem("Testing", 6.2);
		opera.setColor(createRadialGradient(new SolidColor(255, 0, 0), new SolidColor(128, 0, 0)));
		DataSeriesItem others = new DataSeriesItem("Others", 0.7);
		others.setColor(createRadialGradient(new SolidColor(0, 128, 0), new SolidColor(0, 64, 0)));
		return new DataSeries(firefox, ie, chrome, safari, opera, others);
	}
	
	/**
	 * Creates a radial gradient with the specified start and end colors.
	 */
	private GradientColor createRadialGradient(SolidColor start, SolidColor end) {
		GradientColor color = GradientColor.createRadial(0.5, 0.3, 0.7);
		color.addColorStop(0, start);
		color.addColorStop(1, end);
		return color;
	}
}
