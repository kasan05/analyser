package com.transaction.analyser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import com.transaction.analyser.modal.Transaction;
import com.transaction.analyser.util.CsvReader;

public class App {
	
	private static final String FILE_NAME = "transaction.csv";
	
	private static final String PROPRTY_FILE_NAME = "application.properties";
	
	public static void main(String[] args) throws IOException {

		App app = new App();
		
			Properties properties = new Properties();
			
			FileInputStream inStream = new FileInputStream(app.getClass().getClassLoader().getResource(PROPRTY_FILE_NAME).getFile());
			
			properties.load(inStream);
			
		    String fromDate = properties.getProperty("fromDate");
			String toDate 	= properties.getProperty("toDate");
			String merchant = properties.getProperty("merchant");

		String filePath = app.getClass().getClassLoader().getResource(FILE_NAME).getFile();
		File file = new File(filePath);

		List<Transaction> transactionList = CsvReader.readCsv(file);
		
		List<Transaction> results= CsvReader.searchInList(fromDate, toDate, merchant, transactionList);

		
		Double average = 0.0;
		if( results != null ) {
								average = results
								.stream()
								.map(t->t.getAmount())
								.collect(Collectors.averagingDouble(t->t));
			
		}
		
		
		System.out.println("Number of transactions = "+results.size());
		System.out.println("Average Transaction Value = "+average);
		
	}
}
