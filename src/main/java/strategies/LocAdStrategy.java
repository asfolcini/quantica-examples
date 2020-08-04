/**
 * 
 */
package strategies;


import java.io.IOException;

import com.tictactec.ta.lib.MAType;

import quantica.config.Config;
import quantica.engine.EngineManager;
import quantica.indicator.custom.GoldenCrossIndicator;
import quantica.indicator.custom.PivotPoint;
import quantica.indicator.custom.PivotPoint.FORMULA;
import quantica.indicator.talib.MA;
import quantica.indicator.talib.RSI;
import quantica.model.event.CandleEvent;
import quantica.model.order.Transaction;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;
import quantica.utils.Utils;
import quantica.utils.Writer;


/**
 * EMPTY Strategy - usefull for testing purpose only
 * @author alberto.sfolcini
 *
 */
public class LocAdStrategy extends Strategy implements IStrategy{
		
	public LocAdStrategy() {
		super.setStrategyName("LocAd Strategy");
		super.setStrategyDescription("Entry/Exit levels based on reliabe TA analysis.");
	}
	
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
	
	
	@Override
	public void onStop() {
		for ( String symbol : getPastEventAllSymbols() ) {

		    CandleEvent cet = (CandleEvent) getPastEvent(symbol,0); // t0 "present/last" candle
		    CandleEvent cey = (CandleEvent) getPastEvent(symbol,1); // t1 "previous" candle 
			
		    String validitySignalDate = Utils.getFormattedDate("yyyyMMdd",Utils.addDate(cet.getTimeStamp().getDate(),1));
		    
		    // create filename with the day of the signal taken from the last event 
			String fname = Config.getInstance().REPORTS_PATH+EngineManager.getInstance().getRunID()+"/"+symbol+"-"+validitySignalDate+".txt";
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
				if (((CandleEvent) getPastEvent(symbol,0)).getVolume()<((CandleEvent) getPastEvent(symbol,1)).getVolume()) 
					volume = "FALLING";
				else
					volume = "STABLE";

		    
			pp_daily.showPPLevelsOnChart(symbol);		
			
			wrt.writeln("Expiry="+validitySignalDate);
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
		
	} // onStop()
	
	
	
}
