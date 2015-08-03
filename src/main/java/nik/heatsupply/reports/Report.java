package nik.heatsupply.reports;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;

public class Report {
	public static void main(String[] args) {
		Report r = new Report();
		r.create("d:/GIT/NiK/HeatSupply/src/main/resources/reports/", "reportEmpty.jrxml");
	}
	
	public String create(String path, String name) {
		String ret = "";
		try(FileInputStream fis = new FileInputStream(path + name);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
			JasperDesign design = JRXmlLoader.load(fis);
			JasperReport rep = JasperCompileManager.compileReport(design);
			JasperPrint jp = JasperFillManager.fillReport(rep, null);
			
			HtmlExporter exporter = new HtmlExporter();
			exporter.setExporterInput(new SimpleExporterInput(jp));
//			exporter.setExporterOutput(new SimpleHtmlExporterOutput(new FileOutputStream(new File(path + name + ".html"))));
			exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));
			exporter.exportReport();
			ret = new String(bos.toByteArray());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}