/**
 * 
 */
package tradingsystems.skeleton;

import quantica.broker.paper.PaperBroker;
import quantica.config.Config;
import quantica.engine.QuanticaEngine;
import quantica.marketdatafeed.csvDataFeed.CsvMarketDataFeed;
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


/**
 * @author alberto.sfolcini
 *
 */
public class SkeletonTradingSystem extends TradingSystem implements ITradingSystem{

	@Override
	public void init() {
		System.out.println("Trading System - SKELETON");
		System.out.println("Brief trading system description here");
	}

	
	@Override
	public void execute(boolean arg0) {
		
		boolean UPDATE = false; 
		
		// setting up broker and fees
		PaperBroker broker	= new PaperBroker("B1");
		broker.setFees(0.019,4,19);
		broker.setSlippage(0.01);
		
		// setting up CSV Market data feed
		CsvMarketDataFeed myDF = new CsvMarketDataFeed(Config.getInstance().RESOURCES_PATH+"RACE.MI.csv",TimeFrame.TIMEFRAME_1day);				
		myDF.setUseAdjClose(false);		
		
		Persistence persistenceEngine = new MariaPersistence();
		persistenceEngine.setLivePersistence(false);
		// reset persistence data at every run
		if (!UPDATE)
			persistenceEngine.setEngineMode(EngineMode.RESET);
		else
			persistenceEngine.setEngineMode(EngineMode.UPDATE);
		
		
		// Basic textual report 
		TextReport textReport = new TextReport(ReportType.OUTPUT_ONLY);		
		
		// Chart report
		ChartingReport chartReport = new ChartingReport();{
			chartReport.setShowChart(true);
			chartReport.setSaveChartAsPNG(false);
			chartReport.setChartPeriods(21*12);
			chartReport.setShowEquity(false);
			chartReport.setPNGDimension(1600, 1200);
			chartReport.setSavePath(Config.getInstance().REPORTS_PATH);
		}
		
		// STRATEGIES
		Strategy strategy 	= new SkeletonSystem();
		
			
		// QUANTICA ENGINE
		QuanticaEngine engine = new QuanticaEngine();
		engine.addBroker(broker);
		engine.addDataFeed(myDF);
		engine.addPersistence(persistenceEngine);
		engine.addStrategy(strategy);
		engine.addReport(textReport);
		engine.addReport(chartReport);
		engine.setVerbose(false);
		engine.run();			
		
		engine.report();	
	}
	
	@Override
	public void reports() {
		// TODO Auto-generated method stub		
	}
		
	
	@Override
	public void optimize() {
		System.out.println("Optimization is not supported.");
	}

	@Override
	public void backtest() {
		System.out.println("No test availabes for this Trading System.");		
	}

	@Override
	public void train() {	
		System.out.println("No training availabes for this Trading System.");		
	}		
	

}
