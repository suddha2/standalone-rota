package com.midco.rota;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@PlanningSolution
public class Rota {
	@ValueRangeProvider(id = "employeeRange")
	@ProblemFactCollectionProperty
	private List<Employee> employeeList;

	@PlanningEntityCollectionProperty
	private List<ShiftAssignment> shiftAssignmentList;

	@PlanningScore
	private HardSoftScore score;

	@ProblemFactCollectionProperty
	private List<IdealShiftCount> idealShiftCountList;

	public Rota() {
	}

	public Rota(List<Employee> employeeList, List<ShiftAssignment> shiftAssignmentList) {
		this.employeeList = employeeList;
		this.shiftAssignmentList = shiftAssignmentList;
		int ideal = shiftAssignmentList.size() / employeeList.size();
		this.idealShiftCountList = List.of(new IdealShiftCount(ideal));
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public List<ShiftAssignment> getShiftAssignmentList() {
		return shiftAssignmentList;
	}

	public HardSoftScore getScore() {
		return score;
	}

	public void setScore(HardSoftScore score) {
		this.score = score;
	}

	public List<IdealShiftCount> getIdealShiftCountList() {
		return idealShiftCountList;
	}

	public void setIdealShiftCountList(List<IdealShiftCount> idealShiftCountList) {
		this.idealShiftCountList = idealShiftCountList;
	}

}
