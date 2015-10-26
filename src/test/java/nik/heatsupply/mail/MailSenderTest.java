package nik.heatsupply.mail;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import nik.heatsupply.dao.DataBaseImpl;

public class MailSenderTest {
	private static URL sourceMail;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sourceMail = DataBaseImpl.class.getClassLoader().getResource(MailSender.MAIL_PROPERTIES);
	}

	@Test
	public void test() {
		assertTrue(sourceMail != null);
		
		System.out.println("\n\n\n-------------------------------------------------------");
		System.out.println("||\t1. File " + MailSender.MAIL_PROPERTIES + " is exist!");
		System.out.println("||");
		System.out.println("||\tDataBaseImpl Done");
		System.out.println("-------------------------------------------------------\n\n\n");
	}
	
	@AfterClass
	public static void setUpAfterClass() throws Exception {
		if(sourceMail == null) {
			System.out.println("\n\n\n--------------------------------------------------------------------------");
			System.out.println("||\t\t\t\t\t\t\t\t\t||\n||\t\t\t\t\t\t\t\t\t||\n||\tFile " + MailSender.MAIL_PROPERTIES +
					" not exist! Add it into resources folder.\t||");
			System.out.println("||\t\t\t\t\t\t\t\t\t||\n||\t\t\t\t\t\t\t\t\t||\n" + 
					"--------------------------------------------------------------------------\n\n\n");
		}
	}
}