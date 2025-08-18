package com.midco.rota;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

public class RotaApp {
	public static void main(String[] args) {
		List<Employee> employees = List.of(new Employee("A", "Alice"), new Employee("B", "Bob"),
				new Employee("C", "Carol"), new Employee("D", "Dan"));

		LocalDate start = LocalDate.now();
		List<ShiftAssignment> shifts = new ArrayList<>();
		Long id = 0L;
		for (int i = 0; i < 7; i++) {
			LocalDate date = start.plusDays(i);
			for (ShiftType type : ShiftType.values()) {
				shifts.add(new ShiftAssignment(date, type, id++));
			}
		}

		Rota problem = new Rota(employees, shifts);

		SolverFactory<Rota> solverFactory = SolverFactory.createFromXmlResource("solverConfig.xml");
		Solver<Rota> solver = solverFactory.buildSolver();

		ScoreManager<Rota, HardSoftScore> scoreManager = ScoreManager.create(solverFactory);

		// 2) Build a solver and register a BestSolutionChanged listener
		// Solver<Rota> solver = solverFactory.buildSolver();
		solver.addEventListener(event -> {
			Rota bestSoFar = event.getNewBestSolution();
			System.out.println("=== New best: " + bestSoFar.getScore() + " ===");
			ScoreExplanation<Rota, HardSoftScore> explanation = scoreManager.explainScore(bestSoFar);
			printConstraintMatches(explanation);
		});
		Rota solution = solver.solve(problem);
		System.out.println("\nFinal Score: " + solution.getScore());
		solution.getShiftAssignmentList().forEach(System.out::println);
	}

	private static void printConstraintMatches(ScoreExplanation<Rota, HardSoftScore> explanation) {
		System.out.println("Overall score: " + explanation.getScore());
		System.out.println();

	}
}
