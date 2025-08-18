package com.midco.rota;

import java.time.LocalDate;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class ShiftAssignment {
	private LocalDate date;
	private ShiftType shiftType;
	@PlanningId
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Employee employee; // planning variable

	public ShiftAssignment() {
	}

	public ShiftAssignment(LocalDate date, ShiftType shiftType, Long id) {
		this.date = date;
		this.shiftType = shiftType;
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public ShiftType getShiftType() {
		return shiftType;
	}

	@PlanningVariable(valueRangeProviderRefs = "employeeRange" ,nullable = true)
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String toString() {
		return date + " " + shiftType + " -> " + (employee == null ? "UNASSIGNED" : employee.getName());
	}
}
