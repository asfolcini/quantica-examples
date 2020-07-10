/**
 * 
 */
package da;


import com.tictactec.ta.lib.MAType;

import quantica.broker.paper.PaperBroker;
import quantica.config.Config;
import quantica.engine.QuanticaEngine;
import quantica.indicator.custom.GoldenCrossIndicator;
import quantica.indicator.custom.PivotPoint;
import quantica.indicator.talib.BBANDS;
import quantica.indicator.talib.MA;
import quantica.indicator.talib.RSI;
import quantica.marketdatafeed.csvDataFeed.CsvMarketDataFeed;
import quantica.model.event.CandleEvent;
import quantica.model.order.Transaction;
import quantica.model.persistence.Persistence;
import quantica.model.persistence.types.EngineMode;
import quantica.model.security.types.TimeFrame;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;
import quantica.persistence.MariaPersistence;
import quantica.report.TextReport;
import quantica.report.TextReport.ReportType;
import quantica.report.chart.ChartingReport;


/**
 * 
 * DailyAdvisor<br>
 * Use this code as a starting point.</br>
 * 
 * <br><b>DETAILS</b><br>
 * 
 * <b>TODO</b><br>
 * -
 * -  
 *  
 * <br>
 * <b>History:</b><br>
 *  - [24/10/2019] Created. (Alberto Sfolcini)<br>
 *
 *  
 */
public class DailyAdvisor extends Strategy implements IStrategy{
	
	private int minimumPeriods = 200;
	
	MA ema200 = new MA(this,"ema200",200,MAType.Ema);
	MA ema50 = new MA(this,"ema50",50,MAType.Ema);

	
	private GoldenCrossIndicator goldenCrossIndicator = new GoldenCrossIndicator(this,"GCI",50,200,MAType.Ema);
	{
		goldenCrossIndicator.setShowInChart(false);
	}
	private RSI rsi = new RSI(this,"RSI",5,80,20);
	
	
	public DailyAdvisor(){
		setStrategyName("LOCADA System v.1");
		setStrategyDescription("A clean,light, Daily Advisor strategy");	
		
	}

	
	@Override
	public void onEvent(Object event) {	
		CandleEvent ce = (CandleEvent) event;
		String s = ce.getSymbol();					
		
		if (!ce.getTimeFrame().equals(TimeFrame.TIMEFRAME_1day)) {
			log("This strategy supports DAILY timeframes only! your current TF is "+ce.getTimeFrame().getDescription());
			return; 
		}
						
		log("["+getPeriodsFor(s)+"]"+ce.toString());
		
		
		// check if we have enough data to activate our strategy
		if (super.getPeriodsFor(s)>minimumPeriods) {		
				
			// *********************************
			//         YOUR CODE HERE			
			// *********************************
		
			
			
		}// end activation
		
	}

	
	@Override
	public void onStart() {		
		log("onStart():");		
	}
	
	
	@Override
	public void onStop() {
		log("onStop():");
	}
	
	@Override
	public void onExecution(Transaction t) {
		log("onExecution(): transactionID: "+t.getTransactionId()+" "+t.getTimestamp().getDate().toString());
		//t.getInfo();
	}
	
	
	
	
	
	/**
	 * ==========   MAIN   ============================================================================================
	 * 
	 * Setting up and execute it
	 * 
	 * ================================================================================================================
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean UPDATE = false; 
		
		// setting up broker and fees
		PaperBroker broker	= new PaperBroker("B1");
		broker.setFees(0.019,4,19);
		broker.setSlippage(0.01);
		
		// setting up CSV Market data feed
		CsvMarketDataFeed myDF = new CsvMarketDataFeed(Config.getInstance().RESOURCES_PATH+"RACEMI20200320.csv",TimeFrame.TIMEFRAME_1day);				
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
			chartReport.setSaveChartAsPNG(true);
			chartReport.setChartPeriods(21*4);
			chartReport.setShowEquity(false);
			chartReport.setPNGDimension(400, 300);
			chartReport.setSavePath("C:\\QT\\Storico\\reports");
		}
		
		// STRATEGIES
		Strategy strategy 	= new DailyAdvisor();
		
			
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
	
}
