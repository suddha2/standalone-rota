package com.midco.rota;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.util.Pair;

public class RotaApp {

	private static final String DB_URL = "jdbc:mariadb://localhost:3306/shiftmind";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "2096";

	public static void main(String[] args) {

		List<Employee> employees = queryEmployees();

		List<ShiftTemplate> shiftTemplates = queryShiftTemplates();

		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(6);
		List<ShiftAssignment> shifts = new ArrayList<>();
		List<Shift> instances = new ArrayList<>();
		AtomicLong id = new AtomicLong(1L);
		
		
		for (ShiftTemplate template : shiftTemplates) {
			LocalDate current = startDate;
			while (!current.isAfter(endDate)) {
				if (current.getDayOfWeek().toString().equals(template.getDayOfWeek())) {
					instances.add(new Shift(id.getAndIncrement(), current, template,(int)(Math.random() * 3) + 1));

				}
				current = current.plusDays(1);

			}
		}
		
		

//		List<ShiftAssignment> assignments = instances.stream()
//				.map(instance -> new ShiftAssignment(instance, id.getAndIncrement())).collect(Collectors.toList());

		List<ShiftAssignment> assignments = new ArrayList<>();

		for (Shift shift : instances) {
		    for (int i = 0; i < shift.getEmpCount(); i++) {
		        ShiftAssignment assignment = new ShiftAssignment(shift, id.getAndIncrement());
		        assignments.add(assignment);
		    }
		}
		
		final Map<Pair<String, String>, Double> distanceMap = loadDistanceMatrix();
		
		DistanceMatrixHolder.setDistanceMap(distanceMap);
		
		Rota problem = new Rota(employees, assignments);

		System.out.println(problem.toString());

		SolverFactory<Rota> solverFactory = SolverFactory.createFromXmlResource("solverConfig.xml");
		Solver<Rota> solver = solverFactory.buildSolver();

		ScoreManager<Rota, HardSoftScore> scoreManager = ScoreManager.create(solverFactory);

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
	
	public static Map<Pair<String, String>, Double> loadDistanceMatrix()  {
        Map<Pair<String, String>, Double> distanceMap = new HashMap<>();

        String sql = "SELECT origin_postcode, destination_postcode, distance_km FROM postcode_distance";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
        
        PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String origin = rs.getString("origin_postcode").trim().toLowerCase();
                String destination = rs.getString("destination_postcode").trim().toLowerCase();
                double distance = rs.getDouble("distance_km");

                distanceMap.put(Pair.of(origin, destination), distance);
            }
        
        } catch (SQLException e) {
			System.err.println("❌ Connection failed: " + e.getMessage());
		}
        return distanceMap;
    }

	private static void printConstraintMatches(ScoreExplanation<Rota, HardSoftScore> explanation) {
		System.out.println("Overall score: " + explanation.getScore());
		System.out.println();

	}

	private static List<Employee> queryEmployees() {
		List<Employee> emplist = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

			String sql = "SELECT id, name FROM employees";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				emplist.add(new Employee(rs.getLong("id"), rs.getString("name")));
			}

		} catch (SQLException e) {
			System.err.println("❌ Connection failed: " + e.getMessage());
		}
		return emplist;
	}

	private static List<ShiftTemplate> queryShiftTemplates() {
		List<ShiftTemplate> shiftTemList = new ArrayList<>();
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

			String sql = "SELECT id,location,region,day_of_week,start_time,end_time,required_gender,required_skills  FROM shift_templates";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				long id = rs.getLong("id");
				String location = rs.getString("location");
				String region = rs.getString("region");
				String day_of_week = rs.getString("day_of_week");
				LocalTime start_time = LocalTime.parse(rs.getString("start_time"));
				LocalTime end_time = LocalTime.parse(rs.getString("end_time"));
				String required_gender = rs.getString("required_gender");
				String required_skills = rs.getString("required_skills");
				shiftTemList.add(new ShiftTemplate(id, location, region, day_of_week, start_time, end_time,
						required_gender, required_skills));
			}

		} catch (SQLException e) {
			System.err.println("❌ Connection failed: " + e.getMessage());
		}
		return shiftTemList;
	}

}
