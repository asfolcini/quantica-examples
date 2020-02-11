/**
 * 
 */
package tradingsystems.skeleton;


import quantica.broker.paper.PaperBroker;
import quantica.engine.QuanticaEngine;
import quantica.marketdatafeed.bogus.BogusMarketDataFeed;
import quantica.model.event.CandleEvent;
import quantica.model.marketdatafeed.types.MarketDataEventType;
import quantica.model.security.types.Currency;
import quantica.model.security.types.SecurityType;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;
import quantica.strategy.utils.LoadSecurities;


/**
 * HELLOWORLD SYSTEM
 * @author alberto.sfolcini
 */
public class HelloWorld {


	class HelloworldStrategy extends Strategy implements IStrategy{				
		
		public HelloworldStrategy() {
			super.setStrategyName("Hello World!");
		}
		
		@Override
		public void onEvent(Object event) {
			CandleEvent ce = (CandleEvent) event;									
			log(ce.toString());			
		}				
	}
	
	
	public HelloWorld() {							
		
		PaperBroker broker	= new PaperBroker("PAPER");		
		broker.setBalance(1_000_000);
		
		// DATAFEEDS
		BogusMarketDataFeed myDF = new BogusMarketDataFeed(500);		
		myDF.setMarketDataEventType(MarketDataEventType.CANDLE_EVENT);
		myDF.subscribeSecurities("IBM,XOM");
								
		// QUANTICA ENGINE
		QuanticaEngine engine = new QuanticaEngine();
		engine.addBroker(broker);
		engine.addDataFeed(myDF);
		engine.addStrategy(new HelloworldStrategy());
		engine.runForMillis(20_000);				
		
	}
	
	// start it up
	public static void main(String[] args) {			
		new HelloWorld();
	}
}
