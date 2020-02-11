/**
 * 
 */
package strategies;


import quantica.model.order.Transaction;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;


/**
 * EMPTY Strategy - usefull for testing purpose only
 * @author alberto.sfolcini
 *
 */
public class EmptyStrategy extends Strategy implements IStrategy{
		
	/**
	 * @param strategyName
	 */
	public EmptyStrategy(String strategyName){
		this.setStrategyName(strategyName);
	}

	/**
	 * @param ID
	 * @param name
	 */
	public EmptyStrategy(String ID,String name){
		this.setStrategyID(ID);
		this.setStrategyName(name);
	}

	
	@Override
	public void onEvent(Object event) {	
		log("Event received: "+event.toString());		
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
		log("onExecution():");
		t.getInfo();
	}
	
	
}
