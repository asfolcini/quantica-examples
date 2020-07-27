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
import quantica.indicator.talib.ATR;
import quantica.indicator.talib.BBANDS;
import quantica.indicator.talib.HT_TRENDLINE;
import quantica.indicator.talib.MA;
import quantica.indicator.talib.RSI;
import quantica.indicator.talib.TSF;
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
 * MAIA Advisor LOCAD<br>
 * Advisor used for MAIA in order to give entry/exit levels and general stock analysis.</br>
 * 
 * <h2>maia.surprisalx.com</h2>
 *  
 * <br>
 * <b>History:</b><br>
 *  - [19/07/2020] Created. (Alberto Sfolcini)<br>
 *
 *  
 */
public class MaiaAdvisor_locad extends Strategy implements IStrategy{
	
	private static String NAME 			= "MAIA Advisor LOCADA ";
	private static String VERSION		= "v1.0";
	private static String DESCRIPTION	= "Entry/Exit levels based on reliable TA indicators.";
	
	
	private int minimumPeriods = 200;
	
	MA ema200 = new MA(this,"ema200",200,MAType.Ema);
	MA ema50 = new MA(this,"ema50",50,MAType.Ema);
	
	private GoldenCrossIndicator goldenCrossIndicator = new GoldenCrossIndicator(this,"GCI",50,200,MAType.Ema);
	{
		goldenCrossIndicator.setShowInChart(false);
	}
	
	private RSI rsi = new RSI(this,"RSI",5,80,20);
	
	// Daily PP
	private PivotPoint pp_daily = new PivotPoint(this,"Daily_PP",1,FORMULA.CLASSIC);
	{
		pp_daily.setShowInChart(true);
	}
	
	// Weekly PP
	private PivotPoint pp_weekly = new PivotPoint(this,"Weekly_PP",5,FORMULA.CLASSIC);
	{
		pp_weekly.setShowInChart(true);
	}

	// Monthly PP
	private PivotPoint pp_monthly = new PivotPoint(this,"Monthly_PP",21,FORMULA.CLASSIC);
	{
		pp_monthly.setShowInChart(true);
	}
	
	public MaiaAdvisor_locad(){
		super(10);
		setStrategyName(NAME+" "+VERSION);
		setStrategyDescription(DESCRIPTION);		
	}

	
	@Override
	public void onEvent(Object event) {	
		CandleEvent ce = (CandleEvent) event;
		String s = ce.getSymbol();					
		
		if (!ce.getTimeFrame().equals(TimeFrame.TIMEFRAME_1day)) {
			log("This strategy supports DAILY timeframes only! your current TF is "+ce.getTimeFrame().getDescription());
			return; 
		}
		
		
		// check if we have enough data to activate our strategy
		if (super.getPeriodsFor(s)>minimumPeriods) {		
			//System.out.println("Volume="+ce.getVolume());
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
						
			
			String volume = "";
			if (((CandleEvent) getPastEvent(symbol,0)).getVolume()>((CandleEvent) getPastEvent(symbol,1)).getVolume()) 
				volume = "RISING";
			else
				volume = "STABLE";
			
		    CandleEvent cet = (CandleEvent) getPastEvent(symbol,0); // today
		    CandleEvent cey = (CandleEvent) getPastEvent(symbol,1); // yesterday (previous candle) 
		    
			pp_daily.showPPLevelsOnChart(symbol);		
			
			wrt.writeln("Expiry="+Utils.getFormattedDate("yyyyMMdd",cet.getTimeStamp().getDate()));
			wrt.writeln("Open="+cet.getOpenPrice());
			wrt.writeln("High="+cet.getHighPrice());
			wrt.writeln("Low="+cet.getLowPrice());
			wrt.writeln("Close="+cet.getClosePrice());
			wrt.writeln("PreOpen="+cey.getOpenPrice());
			wrt.writeln("PreHigh="+cey.getHighPrice());
			wrt.writeln("PreLow="+cey.getLowPrice());
			wrt.writeln("PreClose="+cey.getClosePrice());
			wrt.writeln("Var%="+Utils.truncate(((cet.getClosePrice()/cey.getClosePrice()*100)-100),2));
			wrt.writeln("Main Trend="+mainTrend);
			wrt.writeln("Main Trend Strongness="+maintrendStrongness);
			wrt.writeln("RSI Signal="+Utils.truncate(rsi.getValue(symbol),2));
			wrt.writeln("Volume="+volume);
			wrt.writeln("PP Daily="+Utils.truncate(pp_daily.getPivotPoint(symbol), 2));
			wrt.writeln("R1 Daily="+Utils.truncate(pp_daily.getResistence1(symbol),2));
			wrt.writeln("R2 Daily="+Utils.truncate(pp_daily.getResistence2(symbol),2));
			wrt.writeln("R3 Daily="+Utils.truncate(pp_daily.getResistence3(symbol),2));
			wrt.writeln("S1 Daily="+Utils.truncate(pp_daily.getSupport1(symbol),2));
			wrt.writeln("S2 Daily="+Utils.truncate(pp_daily.getSupport2(symbol),2));
			wrt.writeln("S3 Daily="+Utils.truncate(pp_daily.getSupport3(symbol),2));
			wrt.writeln("PP Weekly="+Utils.truncate(pp_weekly.getPivotPoint(symbol), 2));
			wrt.writeln("R1 Weekly="+Utils.truncate(pp_weekly.getResistence1(symbol),2));
			wrt.writeln("R2 Weekly="+Utils.truncate(pp_weekly.getResistence2(symbol),2));
			wrt.writeln("R3 Weekly="+Utils.truncate(pp_weekly.getResistence3(symbol),2));
			wrt.writeln("S1 Weekly="+Utils.truncate(pp_weekly.getSupport1(symbol),2));
			wrt.writeln("S2 Weekly="+Utils.truncate(pp_weekly.getSupport2(symbol),2));
			wrt.writeln("S3 Weekly="+Utils.truncate(pp_weekly.getSupport3(symbol),2));
			wrt.writeln("PP Monthly="+Utils.truncate(pp_monthly.getPivotPoint(symbol), 2));
			wrt.writeln("R1 Monthly="+Utils.truncate(pp_monthly.getResistence1(symbol),2));
			wrt.writeln("R2 Monthly="+Utils.truncate(pp_monthly.getResistence2(symbol),2));
			wrt.writeln("R3 Monthly="+Utils.truncate(pp_monthly.getResistence3(symbol),2));
			wrt.writeln("S1 Monthly="+Utils.truncate(pp_monthly.getSupport1(symbol),2));
			wrt.writeln("S2 Monthly="+Utils.truncate(pp_monthly.getSupport2(symbol),2));
			wrt.writeln("S3 Monthly="+Utils.truncate(pp_monthly.getSupport3(symbol),2));
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
		CsvMarketDataFeed myDF = new CsvMarketDataFeed("C:/QT/Historical/RACE.MI.csv",TimeFrame.TIMEFRAME_1day,true);
		
		// Chart report
		ChartingReport chartReport = new ChartingReport();{
			chartReport.setShowChart(false);
			chartReport.setSaveChartAsPNG(true);
			chartReport.setChartPeriods(21*4);
			chartReport.setShowEquity(false);
			chartReport.setPNGDimension(400, 200);
		}
		
		// STRATEGIES
		Strategy strategy 	= new MaiaAdvisor_locad();
					
		// QUANTICA ENGINE
		QuanticaEngine engine = new QuanticaEngine();
		engine.addBroker(broker);
		engine.addDataFeed(myDF);
		engine.addStrategy(strategy);
		engine.addReport(chartReport);
		engine.setVerbose(false);
		engine.run();			
		
		engine.report();			
	}
	
}
