package com.midco.rota;

import java.time.LocalDateTime;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class RotaConstraintProvider implements ConstraintProvider {
	@Override
	public Constraint[] defineConstraints(ConstraintFactory factory) {

		return new Constraint[] { allShiftsMustBeAssigned(factory),
				minimumRestBetweenShifts(factory), encourageEmployeeUsage(factory),preventDuplicateAssignments(factory) };//, preferCloserEmployees(factory)
	}

	private Constraint encourageEmployeeUsage(ConstraintFactory factory) {
		return factory.from(Employee.class)
				.ifNotExists(ShiftAssignment.class, Joiners.equal((e) -> e, ShiftAssignment::getEmployee))
				.penalize("Unused employee", HardSoftScore.ofSoft(10));
	}

	private Constraint allShiftsMustBeAssigned(ConstraintFactory factory) {
		return factory.from(ShiftAssignment.class).filter(assignment -> assignment.getEmployee() == null)
				.penalize("Unassigned shift", HardSoftScore.ONE_HARD);
	}

	private Constraint minimumRestBetweenShifts(ConstraintFactory factory) {
		return factory.fromUniquePair(ShiftAssignment.class, Joiners.equal(ShiftAssignment::getEmployee))
				.filter((a1, a2) -> {
					LocalDateTime end1 = LocalDateTime.of(a1.getShift().getShiftDate(),
							a1.getShift().getShiftTemplate().getEndTime());
					LocalDateTime start2 = LocalDateTime.of(a2.getShift().getShiftDate(),
							a2.getShift().getShiftTemplate().getStartTime());
					LocalDateTime end2 = LocalDateTime.of(a2.getShift().getShiftDate(),
							a2.getShift().getShiftTemplate().getEndTime());
					LocalDateTime start1 = LocalDateTime.of(a1.getShift().getShiftDate(),
							a1.getShift().getShiftTemplate().getStartTime());

					// Check if shifts are too close together
					boolean overlapOrTooClose = !end1.plusHours(4).isBefore(start2)
							&& !end2.plusHours(4).isBefore(start1);

					return overlapOrTooClose;
				}).penalize("Insufficient rest between shifts", HardSoftScore.ONE_HARD);
	}

	private Constraint preferCloserEmployees(ConstraintFactory factory) {
		return factory.from(ShiftAssignment.class).filter(assignment -> assignment.getEmployee() != null)
				.penalize("Prefer closer employees", HardSoftScore.ONE_SOFT, assignment -> {
					String origin = assignment.getShift().getShiftTemplate().getLocation();
					String destination = assignment.getEmployee().getName(); // temp workaround
					return (int) (DistanceMatrixHolder.getDistance(origin, destination));
				});
	}

	private Constraint preventDuplicateAssignments(ConstraintFactory factory) {
        return factory
            .from(ShiftAssignment.class)
            .filter(assignment -> assignment.getEmployee() != null)
            .groupBy(
                assignment -> assignment.getShift(),
                assignment -> assignment.getEmployee(),
                ConstraintCollectors.count()
            )
            .filter((shift, employee, count) -> count > 1)
            .penalize("Duplicate assignment of employee to same shift",
                HardSoftScore.ONE_HARD);
    }
}
