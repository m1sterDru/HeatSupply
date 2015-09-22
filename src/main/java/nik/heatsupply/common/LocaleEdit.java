package nik.heatsupply.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class LocaleEdit {
	private static final String PATH = "d:/GIT/NiK/HeatSupply/src/main/resources/lang/";
	private static final String en = "Language_en.properties";
	private static final String ru = "Language_ru.properties";
	private static final String ua = "Language_uk.properties";
	
	public static void main(String[] args) {
		String key = "keyMeterNotExist";
		String valueEN = "This counter is not found. Perhaps different recording format of the serial number. Verify that the specified number or contact your municipal services";
		String valueRU = "Данный счетчик НЕ НАЙДЕН. Возможно, отличается формат записи серийного номера. Проверьте правильность указанного Вами номера или обратитесь к поставщику коммунальной услуги";
		String valueUA = "Даний лічильник не знайдемо. Можливо, відрізняється формат запису серійного номера. Перевірте правильність введеного Вами номера або зверніться до постачальника комунальної послуги";

		boolean isExist = remove(en, key);
		remove(ru, key);
		remove(ua, key);
		
		if(!isExist) key = "\n" + key;
		
		append2File(en, key + "=" + valueEN);
		append2File(ru, key + "=" + valueRU);
		append2File(ua, key + "=" + valueUA);
		System.out.println("COMPLETE");
	}
	
	private static boolean remove(String fileName, String text) {
		boolean isExist = false;
		File inputFile = new File(PATH + fileName);
		File tempFile = new File("myTempFile.properties");
		try(BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));) {
			
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				if(currentLine.indexOf(text) == -1) {
					writer.write(currentLine + "\n");
				} else {
					isExist = true;
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		inputFile.delete();
		tempFile.renameTo(inputFile);
		tempFile.delete();
		return isExist;
	}
	
	private static void append2File(String fileName, String text) {
		File file = new File(PATH + fileName);
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			if (!file.exists()) file.createNewFile();
			out.print(new String(text.getBytes(), StandardCharsets.UTF_8));
		}catch (IOException e) {e.printStackTrace();}
	}
}