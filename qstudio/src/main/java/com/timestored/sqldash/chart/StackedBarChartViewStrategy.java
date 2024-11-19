package com.timestored.sqldash.chart;

import java.sql.ResultSet;

import net.jcip.annotations.Immutable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import com.timestored.sqldash.theme.DBIcons;



/**
 * Strategy for displaying {@link ResultSet}'s as bar charts. 
 * See {@link #getFormatExplainationHtml()} for details.
 */
@Immutable public class StackedBarChartViewStrategy extends AbstractCategoryViewStrategy {

	public static final ViewStrategy INSTANCE = new StackedBarChartViewStrategy();
	
	private StackedBarChartViewStrategy() { super("Bar Chart Stacked", DBIcons.CHART_BAR); }
	
	@Override public UpdateableView getView(ChartTheme theme) {
		final JFreeChart chart = ChartFactory.createStackedBarChart("", "", 
				"values", null, PlotOrientation.VERTICAL, theme.showChartLegend(), 
				true, false);
		return new CategoryDatasetUpdateableView(theme, chart);
	}
	
	@Override public String toString() {
		return StackedBarChartViewStrategy.class.getSimpleName() + "[" + getDescription() + "]";
	}


	@Override public boolean isQuickToRender(ResultSet rs, int rowCount, int numColumnCount) {
		return (numColumnCount*rowCount) < 10_000; // 1.5 seconds on Ryans PC
	}
	@Override public String getPulseName() { return "stack"; }
}

