package nik.heatsupply.socket.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String login;
	private String password;
	private boolean system;
	private int groupid;
	private String name;
	private String middlename;
	private String email;
	private String address;
	private String description;
	private boolean active;
	private Timestamp latest_entry_date;
	private Timestamp change_password_date;
	private boolean admin;
	private byte[] schedule;
	private int timeout_sessions;
	private int sessions_count;
	private int module_start;
	private boolean change_password;
	private String report_list_access;
	private String mnemonic_list_access;
	private String sms_phone;
	private String sms_email;
	private boolean notify_phone;
	private boolean notify_email;
	private Timestamp user_blocked_date;
	private Timestamp since_work_time;
	private Timestamp till_work_time;

	public User() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getLatest_entry_date() {
		return latest_entry_date;
	}

	public void setLatest_entry_date(Timestamp latest_entry_date) {
		this.latest_entry_date = latest_entry_date;
	}

	public Timestamp getChange_password_date() {
		return change_password_date;
	}

	public void setChange_password_date(Timestamp change_password_date) {
		this.change_password_date = change_password_date;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public byte[] getSchedule() {
		return schedule;
	}

	public void setSchedule(byte[] schedule) {
		this.schedule = schedule;
	}

	public int getTimeout_sessions() {
		return timeout_sessions;
	}

	public void setTimeout_sessions(int timeout_sessions) {
		this.timeout_sessions = timeout_sessions;
	}

	public int getSessions_count() {
		return sessions_count;
	}

	public void setSessions_count(int sessions_count) {
		this.sessions_count = sessions_count;
	}

	public int getModule_start() {
		return module_start;
	}

	public void setModule_start(int module_start) {
		this.module_start = module_start;
	}

	public boolean isChange_password() {
		return change_password;
	}

	public void setChange_password(boolean change_password) {
		this.change_password = change_password;
	}

	public String getReport_list_access() {
		return report_list_access;
	}

	public void setReport_list_access(String report_list_access) {
		this.report_list_access = report_list_access;
	}

	public String getMnemonic_list_access() {
		return mnemonic_list_access;
	}

	public void setMnemonic_list_access(String mnemonic_list_access) {
		this.mnemonic_list_access = mnemonic_list_access;
	}

	public String getSms_phone() {
		return sms_phone;
	}

	public void setSms_phone(String sms_phone) {
		this.sms_phone = sms_phone;
	}

	public String getSms_email() {
		return sms_email;
	}

	public void setSms_email(String sms_email) {
		this.sms_email = sms_email;
	}

	public boolean isNotify_phone() {
		return notify_phone;
	}

	public void setNotify_phone(boolean notify_phone) {
		this.notify_phone = notify_phone;
	}

	public boolean isNotify_email() {
		return notify_email;
	}

	public void setNotify_email(boolean notify_email) {
		this.notify_email = notify_email;
	}

	public Timestamp getUser_blocked_date() {
		return user_blocked_date;
	}

	public void setUser_blocked_date(Timestamp user_blocked_date) {
		this.user_blocked_date = user_blocked_date;
	}

	public Timestamp getSince_work_time() {
		return since_work_time;
	}

	public void setSince_work_time(Timestamp since_work_time) {
		this.since_work_time = since_work_time;
	}

	public Timestamp getTill_work_time() {
		return till_work_time;
	}

	public void setTill_work_time(Timestamp till_work_time) {
		this.till_work_time = till_work_time;
	}
}