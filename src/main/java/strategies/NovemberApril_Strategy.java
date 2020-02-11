/**
 * 
 */
package strategies;

import java.util.Calendar;

import quantica.engine.EngineManager;
import quantica.model.event.CandleEvent;
import quantica.model.order.Order;
import quantica.model.order.Transaction;
import quantica.model.security.types.TimeFrame;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;
import quantica.utils.Utils;


/**
 * 
 * November - April Strategy <br>
 * This strategy tries to exploit the seasonal November-April effect.
 * 
 * DETAILS
 * 
 * A well-known example of seasonal strategy in the stock markets is to buy in November and sell in April.
 * Tests have demonstrated that investors would get better returns by buying at the start of November 
 * and selling at the end of April.
 * In this strategy we want to exploit this effect but we buy at the end of October and sell at the start of May, this
 * way we take advantage of the turn of the month. 
 *  
 * Strategy returns avarge of 6.6% per year (gross) from 1990 to 2014 with a high rate of winner trades (72%)
 * on 3 stocks: XOM, BHP and LMT.
 *  
 * <br>
 * <b>History:</b><br>
 *  - [2/apr/2011] Created. (Alberto Sfolcini)<br>
 *
 *  @author Alberto Sfolcini <a.sfolcini@gmail.com>
 *  
 */
public class NovemberApril_Strategy extends Strategy implements IStrategy{
	
	// Anticipate the November buy to get advantage of the ToM effect.
	private int BUY_MONTH  	 = Calendar.OCTOBER;
	private int BUY_DAY		 = 27;
			
	private int SELL_MONTH	 = Calendar.MAY;
	private int SELL_DAY	 = 3;
		
	public NovemberApril_Strategy(){
		setStrategyName("November April Strategy");
		this.setStrategyDescription("This strategy tries to exploit the seasonal November-April effect.");
	}

	@Override
	public void onEvent(Object event) {	
		CandleEvent ce = (CandleEvent) event;
				
		if (!ce.getTimeFrame().equals(TimeFrame.TIMEFRAME_1day)) {
			log("This strategy supports DAILY timeframes only! your current TF is "+ce.getTimeFrame().getDescription());
			return;
		}
		
		Calendar cd = Calendar.getInstance();
		cd.setTime(ce.getTimeStamp().getDate());
		
		int day  = cd.get(Calendar.DAY_OF_MONTH);
		int mo   = cd.get(Calendar.MONTH);	
		int year = cd.get(Calendar.YEAR);
			
		if (mo==BUY_MONTH&&day>=BUY_DAY) {
			if (EngineManager.getInstance().getPositionsManager().getOpenPositionForSymbol(ce.getSymbol())==null){
				log("BUY Triggered on "+Utils.getISODate(cd.getTime()));
				Order o = new Order();
				o.setOrderId("B-"+Utils.getFormattedDate("YYYYMMdd",ce.getTimeStamp().getDate()));							
				o.setOrderCreationTimeStamp(ce.getTimeStamp());
				o.orderAtMarket(ce.getSymbol(), 1000);				
				getBroker("B1").sendOrder(this,o);
			}
		}else {
			if (mo==SELL_MONTH&&day>SELL_DAY) {
				if (EngineManager.getInstance().getPositionsManager().getOpenPositionForSymbol(ce.getSymbol())!=null){
					log("SELL Triggered on "+Utils.getISODate(cd.getTime()));
					Order o = new Order();
					o.setOrderId("S-"+Utils.getFormattedDate("YYYYMMdd",ce.getTimeStamp().getDate()));
					o.setOrderCreationTimeStamp(ce.getTimeStamp());
					o.orderAtMarket(ce.getSymbol(), -1000);
					getBroker("B1").sendOrder(this,o);
				}
			}
		}

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
		log("onExecution(): transactionID: "+t.getTransactionId());
		//t.getInfo();
	}
	
	
}
