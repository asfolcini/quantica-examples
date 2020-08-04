/**
 * 
 */
package advisors;

import quantica.broker.paper.PaperBroker;
import quantica.config.Config;
import quantica.engine.EngineManager;
import quantica.engine.QuanticaEngine;
import quantica.marketdatafeed.csvDataFeed.CsvMarketDataFeed;
import quantica.model.event.CandleEvent;
import quantica.model.persistence.Persistence;
import quantica.model.persistence.types.EngineMode;
import quantica.model.security.types.TimeFrame;
import quantica.model.strategy.Strategy;
import quantica.model.tradingsystem.ITradingSystem;
import quantica.model.tradingsystem.TradingSystem;
import quantica.persistence.MariaPersistence;
import quantica.report.TextReport;
import quantica.report.TextReport.ReportType;
import quantica.report.chart.ChartingReport;
import quantica.utils.Utils;
import strategies.LocAdStrategy;


/**
 * @author alberto.sfolcini
 *
 */
public class MaiaAdvisor extends TradingSystem implements ITradingSystem{

	@Override
	public void init() {
		System.out.println("MAIA Advisor System");
		System.out.println("http://maia.surprisalx.com");
		System.out.println("(C) Copyright 2020, All rights reserved - Alberto Sfolcini <a.sfolcini@gmail.com>");
	}

	
	@Override
	public void execute(boolean arg0) {
		
		// setting up broker and fees
		PaperBroker broker	= new PaperBroker("DUMMY");
		broker.setFees(0.019,4,19);
		broker.setSlippage(0.01);
		
		// setting up CSV Market data feed
		CsvMarketDataFeed myDF = new CsvMarketDataFeed(Config.getInstance().RESOURCES_PATH+"RACE.MI.csv",TimeFrame.TIMEFRAME_1day);				
		myDF.setUseAdjClose(false);		
		
		// Chart report
		ChartingReport chartReport = new ChartingReport();{
			chartReport.setShowChart(false);
			chartReport.setSaveChartAsPNG(true);
			chartReport.setChartPeriods(21*4);
			chartReport.setShowEquity(false);
			chartReport.setPNGDimension(400, 200);
			chartReport.setSaveName("RACE.MI");
		}
		
		// STRATEGIES
		Strategy strategy 	= new LocAdStrategy();
		
			
		// QUANTICA ENGINE
		QuanticaEngine engine = new QuanticaEngine();
		engine.addBroker(broker);
		engine.addDataFeed(myDF);
		engine.addStrategy(strategy);
		//engine.addReport(chartReport);
		engine.setVerbose(false);
		engine.run();			

		engine.report();
	}
	
	@Override
	public void reports() {
		System.out.println("Report has been saved under folder "+Config.getInstance().REPORTS_PATH);
	}
		
	
	@Override
	public void optimize() {
		System.out.println("Optimization is not supported.");
	}

	@Override
	public void test() {
		System.out.println("No test availabes for this Trading System.");		
	}

	@Override
	public void train() {	
		System.out.println("No training availabes for this Trading System.");		
	}		
	

}
