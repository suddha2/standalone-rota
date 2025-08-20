package com.midco.rota;

import java.time.LocalTime;

public class ShiftTemplate {

	private long id;

	private String location;

	private String region;

	private String dayOfWeek;

	private LocalTime startTime;

	private LocalTime endTime;

	private String requiredGender;

	private String requiredSkills;

	public ShiftTemplate(long id, String location, String region, String dayOfWeek, LocalTime startTime,
			LocalTime endTime, String requiredGender, String requiredSkills) {
		super();
		this.id = id;
		this.location = location;
		this.region = region;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
		this.requiredGender = requiredGender;
		this.requiredSkills = requiredSkills;
	}

	public String getRequiredSkills() {
		return requiredSkills;
	}

	public void setRequiredSkills(String requiredSkills) {
		this.requiredSkills = requiredSkills;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getRequiredGender() {
		return requiredGender;
	}

	public void setRequiredGender(String requiredGender) {
		this.requiredGender = requiredGender;
	}

}
