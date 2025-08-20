package com.midco.rota;

import java.time.LocalDate;

public class Shift {


	

	private long id;
	private LocalDate shiftDate;
	private ShiftTemplate shiftTemplate;
	private int empCount;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDate getShiftDate() {
		return shiftDate;
	}

	public void setShiftDate(LocalDate shiftDate) {
		this.shiftDate = shiftDate;
	}
	public Shift(long id, LocalDate shiftDate, ShiftTemplate shiftTemplate,int empCount) {
		super();
		this.id = id;
		this.shiftDate = shiftDate;
		this.shiftTemplate = shiftTemplate;
		this.empCount=empCount;
	}

	public int getEmpCount() {
		return empCount;
	}

	public void setEmpCount(int empCount) {
		this.empCount = empCount;
	}

	public ShiftTemplate getShiftTemplate() {
		return shiftTemplate;
	}

	public void setShiftTemplate(ShiftTemplate shiftTemplate) {
		this.shiftTemplate = shiftTemplate;
	}
}
