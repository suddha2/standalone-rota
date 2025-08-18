package com.midco.rota;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;

public class RotaConstraintProvider implements ConstraintProvider {
	@Override
	public Constraint[] defineConstraints(ConstraintFactory factory) {

		return new Constraint[] { allEmployeesHaveAtLeastOneShift(factory), noBackToBack(factory),
				oneShiftPerDay(factory), evenDistribution(factory) };
	}

	private Constraint oneShiftPerDay(ConstraintFactory factory) {
	    return factory.forEach(ShiftAssignment.class)
	        .filter(sa -> sa.getEmployee() != null)
	        .groupBy(
	            ShiftAssignment::getEmployee,
	            ShiftAssignment::getDate,
	            ConstraintCollectors.count()
	        )
	        .filter((emp, date, count) -> count > 1)
	        .penalize(
	            "One shift per day",
	            HardSoftScore.ONE_HARD,
	            (emp, date, count) -> count - 1
	        );
	}

	private Constraint noBackToBack(ConstraintFactory factory) {
        return factory
            .forEachUniquePair(ShiftAssignment.class,
                Joiners.equal(ShiftAssignment::getEmployee))
            .filter((sa1, sa2) -> isBackToBack(sa1, sa2))
            .penalize("No back-to-back shifts",HardSoftScore.ONE_HARD);
    }

	private boolean isBackToBack(ShiftAssignment sa1, ShiftAssignment sa2) {
		LocalDate d1 = sa1.getDate();
		LocalDate d2 = sa2.getDate();
		int o1 = sa1.getShiftType().ordinal();
		int o2 = sa2.getShiftType().ordinal();

		// same day adjacent shifts (e.g. MORNING→AFTERNOON or AFTERNOON→NIGHT)
		if (d1.equals(d2) && Math.abs(o1 - o2) == 1) {
			return true;
		}
		// NIGHT on day D → MORNING on day D+1
		if (ChronoUnit.DAYS.between(d1, d2) == 1 && sa1.getShiftType() == ShiftType.NIGHT
				&& sa2.getShiftType() == ShiftType.MORNING) {
			return true;
		}
		// vice versa (we use forEachUniquePair, so this may not be strictly needed)
		if (ChronoUnit.DAYS.between(d2, d1) == 1 && sa2.getShiftType() == ShiftType.NIGHT
				&& sa1.getShiftType() == ShiftType.MORNING) {
			return true;
		}
		return false;
	}

	private Constraint allEmployeesHaveAtLeastOneShift(ConstraintFactory factory) {
		return factory.forEach(Employee.class)
				// if there is NO ShiftAssignment whose getEmployee()==this Employee
				.ifNotExists(ShiftAssignment.class, Joiners.equal(emp->emp, ShiftAssignment::getEmployee))
				.penalize("employee must have one shift", HardSoftScore.ONE_HARD);
	}

	private Constraint evenDistribution(ConstraintFactory factory) {
		// 1) Count shifts per employee
		BiConstraintStream<Employee, Integer> countPerEmp = factory.forEach(ShiftAssignment.class)
				.filter(sa -> sa.getEmployee() != null)
				.groupBy(ShiftAssignment::getEmployee, ConstraintCollectors.count());

		// 2) Join that with our single IdealShiftCount fact
		return countPerEmp.join(IdealShiftCount.class)
				// 3) Penalize deviation
				.penalize("Even distribution",HardSoftScore.ONE_SOFT, (employee, actualCount, idealFact) -> Math
						.abs(actualCount.intValue() - idealFact.getIdealCount()));
	}

}
