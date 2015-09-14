package nik.heatsupply.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class Report {
	private static final Logger LOG = LoggerFactory.getLogger(Report.class);
	private HashMap<String, Object> params = new HashMap<String, Object>();
	
	public static void main(String[] args) {
		Report r = new Report();
		r.setParameter("CHERRY_IMG", "cherry.jpg");
		r.create2("d:/GIT/NiK/HeatSupply/src/main/resources/reports/", "reportTest.jrxml");
		System.out.println("end");
	}

	public void setParameter(String name, Object path) {
		params.put(name, path);
	}
	
	public ByteArrayOutputStream create(String path, String name, String format) {
		File template = getFileFromURL(path + name);
		try(FileInputStream fis = new FileInputStream(template);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();) {

			JasperDesign design = JRXmlLoader.load(fis);
			JasperReport rep = JasperCompileManager.compileReport(design);
			JasperPrint jp = JasperFillManager.fillReport(rep, params);

			switch (format.toLowerCase()) {
			case "html":
				HtmlExporter exporter = new HtmlExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));
				exporter.exportReport();
				break;
			case "xls":
				JRXlsExporter exporterXLS = new JRXlsExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jp));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				//configuration.setOnePagePerSheet(true);
				configuration.setDetectCellType(true);
				configuration.setCollapseRowSpan(false);
				exporterXLS.setConfiguration(configuration);
				exporterXLS.exportReport();
				break;
			case "pdf":
				JRPdfExporter exporterPDF = new JRPdfExporter();
				exporterPDF.setExporterInput(new SimpleExporterInput(jp));
				exporterPDF.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
				exporterPDF.exportReport();
				break;
			}

			return bos;
		} catch(Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}
	public String create2(String path, String name) {
		String ret = "";
		File template = new File(path + name);
		try(FileInputStream fis = new FileInputStream(template);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();) {

			JasperDesign design = JRXmlLoader.load(fis);
			JasperReport rep = JasperCompileManager.compileReport(design);
			JasperPrint jp = JasperFillManager.fillReport(rep, params);

			HtmlExporter exporter = new HtmlExporter();
			exporter.setExporterInput(new SimpleExporterInput(jp));
			exporter.setExporterOutput(new SimpleHtmlExporterOutput(new File(path + name + ".html")));
//			exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));
			exporter.exportReport();
			ret = new String(bos.toByteArray());
		} catch(Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		return ret;
	}

	private File getFileFromURL(String path) {
		URL url = this.getClass().getClassLoader().getResource(path);
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}
		return file;
	}
}