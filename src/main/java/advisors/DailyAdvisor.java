/**
 * 
 */
package advisors;


import java.io.IOException;

import com.tictactec.ta.lib.MAType;

import quantica.broker.paper.PaperBroker;
import quantica.config.Config;
import quantica.engine.QuanticaEngine;
import quantica.indicator.custom.GoldenCrossIndicator;
import quantica.indicator.custom.PivotPoint;
import quantica.indicator.custom.PivotPoint.FORMULA;
import quantica.indicator.talib.MA;
import quantica.indicator.talib.RSI;
import quantica.marketdatafeed.csvDataFeed.CsvMarketDataFeed;
import quantica.model.event.CandleEvent;
import quantica.model.order.Transaction;
import quantica.model.security.types.TimeFrame;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;
import quantica.report.chart.ChartingReport;
import quantica.utils.Utils;
import quantica.utils.Writer;


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
	//private RollingWindow<CandleEvent> pastEvents = new RollingWindow<CandleEvent>(10);
	
	MA ema200 = new MA(this,"ema200",200,MAType.Ema);
	MA ema50 = new MA(this,"ema50",50,MAType.Ema);
	//BBANDS bb = new BBANDS(this,"bb",21);
	
	private GoldenCrossIndicator goldenCrossIndicator = new GoldenCrossIndicator(this,"GCI",50,200,MAType.Ema);
	{
		goldenCrossIndicator.setShowInChart(false);
	}
	
	private RSI rsi = new RSI(this,"RSI",5,80,20);
	
	// Weekly PP
	private PivotPoint pp = new PivotPoint(this,"Daily_PP",5,FORMULA.CLASSIC);
	{
		pp.setShowInChart(true);
	}
	
	
	public DailyAdvisor(){
		super(10);
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

						
		//log("["+getPeriodsFor(s)+"]"+ce.toString());
		
		
		// check if we have enough data to activate our strategy
		if (super.getPeriodsFor(s)>minimumPeriods) {		
				
		/*	if (tradeUtils.crossesBelow(s,ema50.getValue(s),ema200.getValue(s))) {
				System.err.println(ce.getTimeStamp().getDate().toString()+"  "+ce.getClosePrice()+" crossed below level "+ema50.getValue(s));
			}*/
			if (tradeUtils.crossesAbove(s,ema50.getValue(s),ema200.getValue(s))) {
				System.err.println(ce.getTimeStamp().getDate().toString()+"  "+ce.getClosePrice()+" crossed above level "+ema50.getValue(s));
			}
		
			
		}// end activation
		
	}

	
	@Override
	public void onStart() {		
		log("onStart():");		
	}
	
	
	@Override
	public void onStop() {
		log("onStop():");
		
		for ( String symbol : getPastEventAllSymbols() ) {

			String fname = "C:/QT/Storico/Reports/"+symbol+"-"+Utils.getFormattedDate("yyyyMMdd", Utils.getTodayDate())+".txt";
			Writer wrt = null;
			try {
				wrt = new Writer(fname,false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("CANNOT WRITE FILE ON FILESYSTEM!!");
				e.printStackTrace();
			}
			
			String mainTrend = goldenCrossIndicator.getValue(symbol)==1?"UP":goldenCrossIndicator.getValue(symbol)==-1?"DOWN":"FLAT";
			double gcDistance = Math.abs(goldenCrossIndicator.getSlowMAValue(symbol) - goldenCrossIndicator.getFastMAValue(symbol));
			String maintrendStrongness = "";
			if (gcDistance>=20) maintrendStrongness = "VERY STRONG";
			if (gcDistance>=10&&gcDistance<20) maintrendStrongness = "STRONG";
			if (gcDistance>=5&&gcDistance<10) maintrendStrongness = "GOOD";
			if (gcDistance>=2&&gcDistance<5) maintrendStrongness = "WEAK";
			if (gcDistance>=0&&gcDistance<2) maintrendStrongness = "VERY WEAK";
			
			String localtrend = "at RSI level of "+Utils.truncate(rsi.getValue(symbol),2);
			if (rsi.getValue(symbol)<=rsi.getOverSoldValue(symbol)) localtrend = "oversold at level "+rsi.getValue(symbol);
			if (rsi.getValue(symbol)>=rsi.getOverBoughtValue(symbol)) localtrend = "overbought at level "+rsi.getValue(symbol);
			
			
			String volume = "";
			if (((CandleEvent) getPastEvent(symbol,0)).getVolume()>((CandleEvent) getPastEvent(symbol,1)).getVolume()) 
				volume = "RISING";
			else
				volume = "STABLE";
			
		    CandleEvent cet = (CandleEvent) getPastEvent(symbol,0);
			System.out.println(symbol+" ENTRY/EXIT LEVELS FOR " +Utils.addPeriodsToDate("dd/MM/yyyy", Utils.getFormattedDate("dd/MM/yyyy", cet.getTimeStamp().getDate()), 1)+" : ");
			System.out.println("PP: "+Utils.truncate(pp.getPivotPoint(symbol), 2));
			System.out.println("R1: "+Utils.truncate(pp.getResistence1(symbol),2) + "   S1: "+Utils.truncate(pp.getSupport1(symbol),2));
			System.out.println("R2: "+Utils.truncate(pp.getResistence2(symbol),2) + "   S2: "+Utils.truncate(pp.getSupport2(symbol),2));
			System.out.println("R3: "+Utils.truncate(pp.getResistence3(symbol),2) + "   S3: "+Utils.truncate(pp.getSupport3(symbol),2));
			System.out.println("Main trend is "+mainTrend+" and "+maintrendStrongness);
			System.out.println("Local trend is "+localtrend);
			System.out.println("Volume is "+volume+" ("+Utils.FormatNumber(cet.getVolume(),Config.getInstance().LOCALE)+")");

			//pp.chartLastPeriod(symbol);
			pp.showPPLevelsOnChart(symbol);
			
			wrt.writeln("Expiry="+Utils.getFormattedDate("yyyyMMdd",cet.getTimeStamp().getDate()));
			wrt.writeln("Open="+cet.getOpenPrice());
			wrt.writeln("High="+cet.getHighPrice());
			wrt.writeln("Low="+cet.getLowPrice());
			wrt.writeln("Close="+cet.getClosePrice());
			wrt.writeln("Main Trend="+mainTrend);
			wrt.writeln("Main Trend Strongness="+maintrendStrongness);
			wrt.writeln("RSI Signal="+Utils.truncate(rsi.getValue(symbol),2));
			wrt.writeln("Volume="+volume);
			wrt.writeln("PP Daily="+Utils.truncate(pp.getPivotPoint(symbol), 2));
			wrt.writeln("R1 Daily="+Utils.truncate(pp.getResistence1(symbol),2));
			wrt.writeln("R2 Daily="+Utils.truncate(pp.getResistence2(symbol),2));
			wrt.writeln("R3 Daily="+Utils.truncate(pp.getResistence3(symbol),2));
			wrt.writeln("S1 Daily="+Utils.truncate(pp.getSupport1(symbol),2));
			wrt.writeln("S2 Daily="+Utils.truncate(pp.getSupport2(symbol),2));
			wrt.writeln("S3 Daily="+Utils.truncate(pp.getSupport3(symbol),2));
			wrt.close();
		}

		
		
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
		//CsvMarketDataFeed myDF = new CsvMarketDataFeed(Config.getInstance().RESOURCES_PATH+"RACE.MI.csv",TimeFrame.TIMEFRAME_1day);
		CsvMarketDataFeed myDF = new CsvMarketDataFeed("C:/QT/Historical/RACE.MI.csv",TimeFrame.TIMEFRAME_1day);
		myDF.setUseAdjClose(false);		
		
		/*Persistence persistenceEngine = new MariaPersistence();
		persistenceEngine.setLivePersistence(false);
		// reset persistence data at every run
		if (!UPDATE)
			persistenceEngine.setEngineMode(EngineMode.RESET);
		else
			persistenceEngine.setEngineMode(EngineMode.UPDATE);
		*/
		
		// Chart report
		ChartingReport chartReport = new ChartingReport();{
			chartReport.setShowChart(true);
			//chartReport.setSaveChartAsPNG(true);
			chartReport.setChartPeriods(21*4);
			chartReport.setShowEquity(false);
			chartReport.setPNGDimension(400, 300);
		}
		
		// STRATEGIES
		Strategy strategy 	= new DailyAdvisor();
		
			
		// QUANTICA ENGINE
		QuanticaEngine engine = new QuanticaEngine();
		engine.addBroker(broker);
		engine.addDataFeed(myDF);
		//engine.addPersistence(persistenceEngine);
		engine.addStrategy(strategy);
		engine.addReport(chartReport);
		engine.setVerbose(false);
		engine.run();			
		
		engine.report();			
	}
	
}
