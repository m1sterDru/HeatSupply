package nik.heatsupply.socket.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nik.heatsupply.reports.Report;

public class ReportMessages {
	private static final Logger LOG = LoggerFactory.getLogger(ReportMessages.class);
	public static final String HTML_FORMAT = "html";
	public static final String NOT_HTML_FORMAT = "";

	public static void getReport(CommandMessage cm, Session session, String format) {
		String reportName = cm.getParameters().get("reportName");
		Report report = new Report();
		report.setParameter("CHERRY_IMG", "images/cherry.jpg");

		if(format.equals(HTML_FORMAT)) {
			String reportContent = 
					new String(report.create("/reports/", reportName + ".jrxml", "HTML").toByteArray(), StandardCharsets.UTF_8);
			
			CommandMessage retMessage = new CommandMessage("reportHTML");
			retMessage.setParameters("content", reportContent);
			try {
				session.getBasicRemote().sendObject(retMessage);
			} catch (IOException | EncodeException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
			}
		} else {
			String ext = cm.getParameters().get("ext");
			ByteArrayOutputStream reportContent4Save = report.create("/reports/", reportName + ".jrxml", ext);
			
			CommandMessage retMessage = new CommandMessage("report4Save");
			retMessage.setParameters("name", reportName + "." + ext.toLowerCase());
			try {
				session.getBasicRemote().sendBinary(ByteBuffer.wrap(reportContent4Save.toByteArray()));
			} catch (IOException e) {
				LOG.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
}