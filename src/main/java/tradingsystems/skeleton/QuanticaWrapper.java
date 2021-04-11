/**
 * 
 */
package tradingsystems.skeleton;


import java.util.UUID;

import quantica.broker.paper.PaperBroker;
import quantica.engine.EngineManager;
import quantica.engine.QuanticaEngine;
import quantica.marketdatafeed.bogus.BogusMarketDataFeed;
import quantica.model.event.CandleEvent;
import quantica.model.marketdatafeed.types.MarketDataEventType;
import quantica.model.order.Order;
import quantica.model.persistence.types.EngineMode;
import quantica.model.position.Position;
import quantica.model.position.types.PositionSide;
import quantica.model.strategy.IStrategy;
import quantica.model.strategy.Strategy;
import quantica.persistence.MariaPersistence;

import quantica.utils.Utils;


/**
 * 
 * @author alberto.sfolcini
 */
public class QuanticaWrapper {


	class RandomicStrategy extends Strategy implements IStrategy{				
		
		private int time = 20;
		
		public RandomicStrategy(String name) {
			super.setStrategyName(name);
		}
		public RandomicStrategy(String name,int time) {
			super.setStrategyName(name);
			this.time = time;
		}
		
		@Override
		public void onEvent(Object event) {
			CandleEvent ce = (CandleEvent) event;									
			log.info(ce);
			
			int i = (int) (Math.random()*time);
			
			int shares = 30;
			
			// BUY
			if (i==5) {
				if (EngineManager.getInstance().getPositionsManager().getOpenPositionForSymbol(this.getStrategyID(),ce.getSymbol())==null){
					Order o = new Order();
					o.setOrderId("B-"+UUID.randomUUID());							
					o.orderAtMarket(ce.getSymbol(), shares);				
					getBroker("PAPER").sendOrder(this,o);					
				}
			}
			
			// SELL THE CURRENT POSITION
			if (i==10) {
				Position p = EngineManager.getInstance().getPositionsManager().getOpenPositionForSymbol(this.getStrategyID(),ce.getSymbol());
				if ((p!=null)&&(p.getPositionSide().name().equals(PositionSide.LONG.name()))){				
						log("SELL SIGNAL detected on "+Utils.getISODate(ce.getTimeStamp().getDate()));
						Order o = new Order();
						o.setOrderId("S-"+UUID.randomUUID());							
						o.orderAtMarket(ce.getSymbol(), -p.getQuantity());
						getBroker("PAPER").sendOrder(this,o);
				}				
			}
			
		}				
	}
	
	
	public QuanticaWrapper() {							
				
		EngineManager.getInstance().setRunID("TEST"+"-"+EngineManager.getInstance().getRunID());
		//EngineManager.getInstance().setRunID("TEST");
		
		PaperBroker broker	= new PaperBroker("PAPER");		
		broker.setBalance(10_000);
		
		// DATAFEEDS
		BogusMarketDataFeed myDF = new BogusMarketDataFeed(500);		
		myDF.setFakeDays(true, "20200707");
		myDF.setMarketDataEventType(MarketDataEventType.CANDLE_EVENT);
		myDF.subscribeSecurities("RACE.MI");
								
		MariaPersistence persistence = new MariaPersistence();
		persistence.setLivePersistence(true);
		persistence.setEngineMode(EngineMode.RESET);
		
		
		// QUANTICA ENGINE
		QuanticaEngine engine = new QuanticaEngine();				
		engine.addBroker(broker);
		engine.addDataFeed(myDF);
		engine.addPersistence(persistence);
		engine.addStrategy(new RandomicStrategy("TF v1"));
		engine.addStrategy(new RandomicStrategy("SR v1",40));
		engine.addStrategy(new RandomicStrategy("Trend Catcher",60));
		engine.run();
		
	}
	
	// start it up
	public static void main(String[] args) {			
		new QuanticaWrapper();
	}
}
