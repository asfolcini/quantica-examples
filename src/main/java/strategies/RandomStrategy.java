/**
 * 
 */
package strategies;

import quantica.model.event.CandleEvent;
import quantica.model.order.Order;
import quantica.model.order.Transaction;
import quantica.model.security.Security;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;


/**
 * RANDOM Strategy - usefull for testing purpose only
 * @author alberto.sfolcini
 *
 */
public class RandomStrategy extends Strategy implements IStrategy{
		
	public RandomStrategy(String strategyName){
		this.setStrategyName(strategyName);
	}

	int i=0;
	@Override
	public void onEvent(Object event) {	
		log("Event received ["+i+"]: "+event.toString());
		
		CandleEvent ce = (CandleEvent) event;

		// Create an order "itsmine" and immediatly cancel it!
		if (i==4) {
			Order order = new Order();		
			order.setOrderId("itsmine");
			order.orderAtMarket(ce.getSymbol(), 100);
			getBroker("B1").sendOrder(this,order);
			getBroker("B1").cancelOrder(getBroker("IB").getOrderById("itsmine"));
		}
		
		// Create an order "itsyours" and execute it!
		if (i==6) {
			Order order = new Order();		
			order.setOrderId("itsyours");
			order.orderAtMarket(ce.getSymbol(), 20);
			getBroker("B1").sendOrder(this,order);
			i=0;
		}
		
		
		i++;
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
		log("onExecution(): ");
		t.getInfo();
	}
	
	
}
