package nik.heatsupply.socket.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Meter implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int metermodelid;
	private String serialnumber;
	private String connectionstring;
	private String description;
	private int electoid;
	private String parameters;
	private Timestamp lastmeterquerydate;
	private String code;
	private int meterid;
	private boolean channel;
	private double voltageclass;
	private boolean nonlossesinput;
	private Timestamp startprocessingdate;
	private boolean turn;
	private int dataaccess;
	private int connectioncircuit;
	private int poi;
	private int accountingtype;
	private int summaryfactor;
	private String adjacentlicensee;
	private int substationid;
	private int iec101code;
	private int buildingid;
	private int entrancenumber;
	private String ownername;
	private String owneraccount;
	private int algorithmtype;
	private String flatnumber;
	private String teplo_properties;
	private int generator;
	private int location_node_id;
	private Timestamp statuspoll;
	private int idx;
	private int accuracy_class;
	private Timestamp release_date;
	private Timestamp calibration_date;
	private int calibration_interval;
	private int ampere_rating;
	private int ampere_max;
	private int naminal_voltage;
	private int voltage_lerance_from;
	private int voltage_lerance_to;
	private int tree_node_id;
	
	public Meter() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMetermodelid() {
		return metermodelid;
	}

	public void setMetermodelid(int metermodelid) {
		this.metermodelid = metermodelid;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public String getConnectionstring() {
		return connectionstring;
	}

	public void setConnectionstring(String connectionstring) {
		this.connectionstring = connectionstring;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getElectoid() {
		return electoid;
	}

	public void setElectoid(int electoid) {
		this.electoid = electoid;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Timestamp getLastmeterquerydate() {
		return lastmeterquerydate;
	}

	public void setLastmeterquerydate(Timestamp lastmeterquerydate) {
		this.lastmeterquerydate = lastmeterquerydate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getMeterid() {
		return meterid;
	}

	public void setMeterid(int meterid) {
		this.meterid = meterid;
	}

	public boolean isChannel() {
		return channel;
	}

	public void setChannel(boolean channel) {
		this.channel = channel;
	}

	public double getVoltageclass() {
		return voltageclass;
	}

	public void setVoltageclass(double voltageclass) {
		this.voltageclass = voltageclass;
	}

	public boolean isNonlossesinput() {
		return nonlossesinput;
	}

	public void setNonlossesinput(boolean nonlossesinput) {
		this.nonlossesinput = nonlossesinput;
	}

	public Timestamp getStartprocessingdate() {
		return startprocessingdate;
	}

	public void setStartprocessingdate(Timestamp startprocessingdate) {
		this.startprocessingdate = startprocessingdate;
	}

	public boolean isTurn() {
		return turn;
	}

	public void setTurn(boolean turn) {
		this.turn = turn;
	}

	public int getDataaccess() {
		return dataaccess;
	}

	public void setDataaccess(int dataaccess) {
		this.dataaccess = dataaccess;
	}

	public int getConnectioncircuit() {
		return connectioncircuit;
	}

	public void setConnectioncircuit(int connectioncircuit) {
		this.connectioncircuit = connectioncircuit;
	}

	public int getPoi() {
		return poi;
	}

	public void setPoi(int poi) {
		this.poi = poi;
	}

	public int getAccountingtype() {
		return accountingtype;
	}

	public void setAccountingtype(int accountingtype) {
		this.accountingtype = accountingtype;
	}

	public int getSummaryfactor() {
		return summaryfactor;
	}

	public void setSummaryfactor(int summaryfactor) {
		this.summaryfactor = summaryfactor;
	}

	public String getAdjacentlicensee() {
		return adjacentlicensee;
	}

	public void setAdjacentlicensee(String adjacentlicensee) {
		this.adjacentlicensee = adjacentlicensee;
	}

	public int getSubstationid() {
		return substationid;
	}

	public void setSubstationid(int substationid) {
		this.substationid = substationid;
	}

	public int getIec101code() {
		return iec101code;
	}

	public void setIec101code(int iec101code) {
		this.iec101code = iec101code;
	}

	public int getBuildingid() {
		return buildingid;
	}

	public void setBuildingid(int buildingid) {
		this.buildingid = buildingid;
	}

	public int getEntrancenumber() {
		return entrancenumber;
	}

	public void setEntrancenumber(int entrancenumber) {
		this.entrancenumber = entrancenumber;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public String getOwneraccount() {
		return owneraccount;
	}

	public void setOwneraccount(String owneraccount) {
		this.owneraccount = owneraccount;
	}

	public int getAlgorithmtype() {
		return algorithmtype;
	}

	public void setAlgorithmtype(int algorithmtype) {
		this.algorithmtype = algorithmtype;
	}

	public String getFlatnumber() {
		return flatnumber;
	}

	public void setFlatnumber(String flatnumber) {
		this.flatnumber = flatnumber;
	}

	public String getTeplo_properties() {
		return teplo_properties;
	}

	public void setTeplo_properties(String teplo_properties) {
		this.teplo_properties = teplo_properties;
	}

	public int getGenerator() {
		return generator;
	}

	public void setGenerator(int generator) {
		this.generator = generator;
	}

	public int getLocation_node_id() {
		return location_node_id;
	}

	public void setLocation_node_id(int location_node_id) {
		this.location_node_id = location_node_id;
	}

	public Timestamp getStatuspoll() {
		return statuspoll;
	}

	public void setStatuspoll(Timestamp statuspoll) {
		this.statuspoll = statuspoll;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getAccuracy_class() {
		return accuracy_class;
	}

	public void setAccuracy_class(int accuracy_class) {
		this.accuracy_class = accuracy_class;
	}

	public Timestamp getRelease_date() {
		return release_date;
	}

	public void setRelease_date(Timestamp release_date) {
		this.release_date = release_date;
	}

	public Timestamp getCalibration_date() {
		return calibration_date;
	}

	public void setCalibration_date(Timestamp calibration_date) {
		this.calibration_date = calibration_date;
	}

	public int getCalibration_interval() {
		return calibration_interval;
	}

	public void setCalibration_interval(int calibration_interval) {
		this.calibration_interval = calibration_interval;
	}

	public int getAmpere_rating() {
		return ampere_rating;
	}

	public void setAmpere_rating(int ampere_rating) {
		this.ampere_rating = ampere_rating;
	}

	public int getAmpere_max() {
		return ampere_max;
	}

	public void setAmpere_max(int ampere_max) {
		this.ampere_max = ampere_max;
	}

	public int getNaminal_voltage() {
		return naminal_voltage;
	}

	public void setNaminal_voltage(int naminal_voltage) {
		this.naminal_voltage = naminal_voltage;
	}

	public int getVoltage_lerance_from() {
		return voltage_lerance_from;
	}

	public void setVoltage_lerance_from(int voltage_lerance_from) {
		this.voltage_lerance_from = voltage_lerance_from;
	}

	public int getVoltage_lerance_to() {
		return voltage_lerance_to;
	}

	public void setVoltage_lerance_to(int voltage_lerance_to) {
		this.voltage_lerance_to = voltage_lerance_to;
	}

	public int getTree_node_id() {
		return tree_node_id;
	}

	public void setTree_node_id(int tree_node_id) {
		this.tree_node_id = tree_node_id;
	}
}