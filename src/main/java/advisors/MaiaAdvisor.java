/**
 * 
 */
package advisors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import quantica.broker.paper.PaperBroker;
import quantica.config.Config;
import quantica.engine.QuanticaEngine;
import quantica.marketdatafeed.csvDataFeed.CsvMarketDataFeed;
import quantica.model.security.types.TimeFrame;
import quantica.model.strategy.Strategy;
import quantica.model.tradingsystem.ITradingSystem;
import quantica.model.tradingsystem.TradingSystem;
import strategies.LocAdStrategy;


/**
 * MaiaAdvisor System</br>
 * </br>
 * MaiaAdvisorInstruments.config file should exists under the same executable directory and should contains a list of csv files 
 * comma separated.
 * </br></br>
 * Strategies included:</br>
 *   - LocAd Strategy for general stock analysis and reliable entry/exit area levels. 
 * 
 * 
 * </br></br>
 * @author alberto.sfolcini
 *
 */
public class MaiaAdvisor extends TradingSystem implements ITradingSystem{

	private static String VERSION = "v20200824";
	private String instrumentsFileName = "MaiaAdvisorInstruments.config";
	
	
	@Override
	public void init() {
		System.out.println("MAIA Advisor System "+VERSION);
		System.out.println("http://maia.surprisalx.com");
		System.out.println("(C) Copyright 2020, All rights reserved - Alberto Sfolcini <a.sfolcini@gmail.com>");
	}

	
	@Override
	public void execute(boolean arg0) {
		List<String> instruments = new ArrayList<String>();
	    try {
			String content = new String(Files.readAllBytes(Paths.get(this.instrumentsFileName)));
			instruments = Arrays.asList(content.split(","));			
			
			for(String ins:instruments) {
				System.out.println("==>> Executing for insturment: "+ins);
			
				// setting up broker and fees
				PaperBroker broker	= new PaperBroker("DUMMY");
				broker.setFees(0.019,4,19);
				broker.setSlippage(0.01);
				
				// setting up CSV Market data feed
				CsvMarketDataFeed myDF = new CsvMarketDataFeed(Config.getInstance().RESOURCES_PATH+ins,TimeFrame.TIMEFRAME_1day);				
				myDF.setUseAdjClose(false);		
				
				// STRATEGIES
				Strategy strategy 	= new LocAdStrategy();
					
				// QUANTICA ENGINE
				QuanticaEngine engine = new QuanticaEngine();
				engine.addBroker(broker);
				engine.addDataFeed(myDF);
				engine.addStrategy(strategy);
				engine.setVerbose(false);
				engine.run();			
		
				engine.report();
				
			} // end of loop for instruments
			
		} catch (IOException e) {
			System.err.println("Cannot find config file : "+this.instrumentsFileName);
			e.printStackTrace();
		}

		

		
	}
	
	@Override
	public void reports() {
		System.out.println("Report has been saved under folder "+Config.getInstance().REPORTS_PATH);
	}
		
	
	@Override
	public void optimize() {
		System.out.println("Optimization is not supported.");
	}

	@Override
	public void backtest() {
		System.out.println("No test availabes for this Trading System.");		
	}

	@Override
	public void train() {	
		System.out.println("No training availabes for this Trading System.");		
	}		
	

}
