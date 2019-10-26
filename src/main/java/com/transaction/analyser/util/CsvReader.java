package com.transaction.analyser.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.transaction.analyser.modal.Transaction;

public class CsvReader {

	
	private  static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";
	
	public static List<Transaction> readCsv(File file) {

		List<Transaction> list = new ArrayList<Transaction>();
		
		try (
			FileReader reader = new FileReader(file);
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
									.withIgnoreHeaderCase()
									.withTrim());
			){
			
			int i =0;
			for (CSVRecord csvRecord : csvParser) {
				
				Transaction transaction = new Transaction();
				if(i++==0)
					continue;
				transaction.setId(csvRecord.get(0));
				
				LocalDateTime dateTime = getLocalDateTime(csvRecord.get(1), DATE_PATTERN);
				
				transaction.setDate(dateTime);
				transaction.setAmount(Double.parseDouble(csvRecord.get(2)));
				transaction.setMerchant(csvRecord.get(3));
				transaction.setType(csvRecord.get(4));
				
				String relatedTransaction = csvRecord.get(5);
				
				
				if (relatedTransaction.isEmpty()) {
					list.add(transaction);
				} else {
					Optional<Transaction> transactionToRemove = list.stream()
											.filter(t -> relatedTransaction.equals(t.getId()))
											.findAny();

					if(transactionToRemove.isPresent())
						list.remove(transactionToRemove.get());
				}

			}
		} catch (IOException e) {
			System.out.println("Exception:" + e.getMessage());
		}
		return list;
	}
	
	public static List<Transaction> searchInList(String fromDate,String toDate,String merchant,List<Transaction> list) {
		
		LocalDateTime startDateTime = getLocalDateTime(fromDate, DATE_PATTERN);
    	LocalDateTime endDateTime = getLocalDateTime(toDate, DATE_PATTERN);
		
		return	list
				.stream()
				.filter(t->merchant.equals(t.getMerchant()))
				.filter(t-> startDateTime.isBefore(t.getDate()) && endDateTime.isAfter(t.getDate()))
				.collect(Collectors.toList());
		
		
	}
	
	private static LocalDateTime getLocalDateTime(String date,String pattern) {
		return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
	}
}
