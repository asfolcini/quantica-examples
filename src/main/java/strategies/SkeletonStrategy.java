/**
 * 
 */
package strategies;

import quantica.model.event.CandleEvent;
import quantica.model.order.Transaction;
import quantica.model.security.types.TimeFrame;
import quantica.model.strategy.Strategy;

/**
 * @author alberto.sfolcini
 *
 */
public class SkeletonStrategy extends Strategy {

	private int minimumPeriods = 21;	
	
	public SkeletonStrategy(){
		setStrategyName("SKELETON System");
		setStrategyDescription("A clean,light skeleton template for a tradign strategy implementation.");		
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
		t.getInfo();
	}
	
		
	
}
