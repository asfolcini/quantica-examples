/**
 * 
 */
package indicators.skeleton;

import java.util.HashMap;

import quantica.model.event.CandleEvent;
import quantica.model.event.TickEvent;
import quantica.model.indicator.IndicatorBase;
import quantica.model.report.chart.SerieChartType;
import quantica.model.strategy.Strategy;

/**
 * @author alberto.sfolcini
 *
 */
public class SkeletonIndicator extends IndicatorBase{


	public SkeletonIndicator(Strategy strategy,String name) {
		super(strategy,name);
		setIndicatorName(name);
		setSerieChartType(SerieChartType.SUBPLOT);
	}
	
	public void add(Object event) {
		if (event instanceof CandleEvent) {
			CandleEvent ce = (CandleEvent) event;		
			addData(ce.getSymbol(),ce.getTimeStamp());
		}else 
			if (event instanceof TickEvent) {
				TickEvent te = (TickEvent) event;
				addData(te.getSymbol(),te.getTimeStamp());
			}else {
				log.error("Event type NOT supported ( must be CandleEvent or TickEvent");
			}				
	}

	@Override
	public double getValue(String symbol) {
		// IMPLEMENT HERE
		return Math.random()*100;
	}	
	
	@Override
	public HashMap<String, Double> getMap(String symbol) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put(this.getIndicatorName(), this.getValue(symbol));
		return map;
	}



}
