package nik.heatsupply.socket.model;

import java.io.Serializable;

public class MeterUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private int iduser;
	private int idmeter;
	private double lastcash;
	private int idTypeDevice;
	private String idAccount;
	
	public MeterUser() {
		
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public int getIdmeter() {
		return idmeter;
	}

	public void setIdmeter(int idmeter) {
		this.idmeter = idmeter;
	}

	public double getLastcash() {
		return lastcash;
	}

	public void setLastcash(double lastcash) {
		this.lastcash = lastcash;
	}

	public int getIdTypeDevice() {
		return idTypeDevice;
	}

	public void setIdTypeDevice(int idTypeDevice) {
		this.idTypeDevice = idTypeDevice;
	}

	public String getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(String idAccount) {
		this.idAccount = idAccount;
	}
}