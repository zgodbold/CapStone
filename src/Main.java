import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner userInputScanner = new Scanner(System.in);

        System.out.println("Choose simulation mode:");
        System.out.println("1. Use predefined simulation from file");
        System.out.println("2. Enter data month by month");
        System.out.print("Enter choice (1 or 2): ");

        int choice = userInputScanner.nextInt();
        userInputScanner.nextLine();

        if (choice == 1) {
            runFileBasedSimulation();
        } else if (choice == 2) {
            runInteractiveSimulation();
        } else {
            System.out.println("Invalid choice. Exiting.");
        }
    }

    public static void runFileBasedSimulation() {
        try {
            File file = new File("/Users/zellgodbold/IdeaProjects/CapStone/src/FinanceSimulatorSheet.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("---BREAK---")) {
                    break;
                }
            }

            List<Double> dataList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (!line.contains(":")) {
                    System.err.println("Warning: Skipping invalid line (missing colon): " + line);
                    continue;
                }

                String value = line.split(":")[1].trim();

                value = value.replace("$", "").replace(",", "").replace("%", "");

                try {
                    dataList.add(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Skipping invalid value (not a number): " + value);
                }
            }

            if (dataList.size() < 12) {
                throw new RuntimeException("Error: The file does not contain enough data. Expected 12 values, but found " + dataList.size());
            }

            double totalMonthlyIncome = dataList.get(0);
            double percentInvested = dataList.get(1);
            double percentSaved = dataList.get(2);
            double percentSpent = dataList.get(3);
            double currentSavings = dataList.get(4);
            double currentTotalInvested = dataList.get(5);
            double averageInvestmentReturnYearly = dataList.get(6);
            double totalInvestmentReturn = dataList.get(7);
            double studentLoanPayment = dataList.get(8);
            double housingPayment = dataList.get(9);
            double carPayment = dataList.get(10);
            int monthsToSimulate = dataList.get(11).intValue();

            IncomeUses incomeUses = new IncomeUses(percentInvested, percentSaved, percentSpent);
            MonthlyPayment monthlyPayments = new MonthlyPayment(studentLoanPayment, housingPayment, carPayment);
            MonthlyIncome monthlyIncome = new MonthlyIncome(totalMonthlyIncome);
            Investments investments = new Investments(currentSavings, currentTotalInvested, averageInvestmentReturnYearly, totalInvestmentReturn);

            LinkedList<MonthlyResult> simulationResults = new LinkedList<>();

            simulate(incomeUses, monthlyPayments, monthlyIncome, investments, monthsToSimulate, simulationResults, 1);

            writeSimulationResultsToFile(simulationResults, "simulation_results.txt");

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please ensure 'financial_data.txt' exists.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static void simulate(IncomeUses incomeUses, MonthlyPayment monthlyPayment,
                                MonthlyIncome monthlyIncome, Investments investments,
                                int monthsLeft, LinkedList<MonthlyResult> simulationResults,
                                int currentMonth) {
        if (monthsLeft <= 0) {
            return;
        }

        double monthlyInvestmentReturn = investments.getAverageInvestmentReturnYearly() / 12 / 100;
        double income = monthlyIncome.getTotalMonthlyIncome();
        double invested = income * incomeUses.getPercentInvested() / 100;
        double saved = income * incomeUses.getPercentSaved() / 100;
        double spent = income * incomeUses.getPercentSpent() / 100;

        double mandatoryExpenses = monthlyPayment.getStudentLoanPayment() +
                monthlyPayment.getHousingPayment() +
                monthlyPayment.getCarPayment();

        double remainingIncome = income - spent - mandatoryExpenses;

        investments.setCurrentSavings(investments.getCurrentSavings() + saved);
        investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() + invested);

        investments.setTotalInvestmentReturn(investments.getTotalInvestmentReturn() +
                investments.getCurrentTotalInvested() * monthlyInvestmentReturn);

        if (remainingIncome < 0) {
            investments.setCurrentSavings(investments.getCurrentSavings() + remainingIncome);
            if (investments.getCurrentSavings() < 0) {
                double difference = -investments.getCurrentSavings();
                investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() - difference);
                investments.setCurrentSavings(0);
            }
        }

        simulationResults.add(new MonthlyResult(
                currentMonth,
                investments.getCurrentSavings(),
                investments.getCurrentTotalInvested(),
                investments.getTotalInvestmentReturn(),
                spent + mandatoryExpenses,
                invested,
                income
        ));

        investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() + investments.getTotalInvestmentReturn());
        investments.setTotalInvestmentReturn(0);

        simulate(incomeUses, monthlyPayment, monthlyIncome, investments,
                monthsLeft - 1, simulationResults, currentMonth + 1);
    }

    private static void writeSimulationResultsToFile(LinkedList<MonthlyResult> results, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Financial Simulation Results\n");
            writer.write("==========================\n\n");
            writer.write(String.format("%-8s %-12s %-15s %-18s %-12s %-15s %-15s\n", "Month", "Income", "Spent", "New Investments", "Savings", "Total Invested", "Returns"));
            writer.write("----------------------------------------------------------------------------------------------\n");

            for (MonthlyResult result : results) {
                writer.write(String.format("%-8d $%-11.2f $%-14.2f $%-17.2f $%-11.2f $%-14.2f $%-14.2f\n", result.getMonth(), result.getIncome(), result.getSpent(), result.getInvestedAmount(), result.getSavings(), result.getInvested(), result.getInvestmentReturn()));
            }

            System.out.println("\nSimulation results written to: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void runInteractiveSimulation() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter filename to load (leave blank for new simulation): ");
        String filename = scanner.nextLine().trim();
        if(filename.isEmpty()) {
            System.out.println("Please enter the name of your new file: ");
            filename = scanner.nextLine().trim();
        }
        if (!filename.isEmpty() && !filename.endsWith(".txt")) {
            filename += ".txt";
        }

        LinkedList<MonthlyResult> simulationResults = new LinkedList<>();
        Investments investments = null;
        MonthlyIncome monthlyIncome = null;
        MonthlyPayment monthlyPayments = null;
        double amountInvested = 0;
        double amountSaved = 0;
        double amountSpent = 0;
        double yearlyReturnRate = 0;
        int startingMonth = 1;

        if (!filename.isEmpty()) {
            try {

                List<String> existingContent = Files.readAllLines(Paths.get(filename));

                boolean foundState = false;
                for (int i = 0; i < existingContent.size(); i++) {
                    String line = existingContent.get(i).trim();
                    if (line.startsWith("=== SIMULATION STATE ===")) {
                        double savings = parseDollarValue(existingContent.get(i+1));
                        double totalInvested = parseDollarValue(existingContent.get(i+2));
                        yearlyReturnRate = parsePercentageValue(existingContent.get(i+3));
                        double lastIncome = parseDollarValue(existingContent.get(i+4));
                        double lastInvested = parseDollarValue(existingContent.get(i+5));
                        double lastSaved = parseDollarValue(existingContent.get(i+6));
                        double lastSpent = parseDollarValue(existingContent.get(i+7));
                        double studentLoan = parseDollarValue(existingContent.get(i+8));
                        double housingPayment = parseDollarValue(existingContent.get(i+9));
                        double carPayment = parseDollarValue(existingContent.get(i+10));

                        investments = new Investments(savings, totalInvested, yearlyReturnRate, 0);
                        monthlyIncome = new MonthlyIncome(lastIncome);
                        monthlyPayments = new MonthlyPayment(studentLoan, housingPayment, carPayment);
                        amountInvested = lastInvested;
                        amountSaved = lastSaved;
                        amountSpent = lastSpent - studentLoan - housingPayment - carPayment;
                        foundState = true;
                        break;
                    }
                }

                if (foundState) {
                    for (String line : existingContent) {
                        if (line.startsWith("Month ")) {
                            startingMonth = Integer.parseInt(line.split(" ")[1].replace(":", "")) + 1;
                        }
                    }
                    System.out.println("Successfully loaded simulation state through month " + (startingMonth-1));
                } else {
                    System.out.println("No valid state found in file. Starting new simulation.");
                }
            } catch (IOException e) {
                System.out.println("Starting new simulation.");
            }
        }

        if (investments == null) {
            System.out.println("\nEnter initial financial data:");
            System.out.print("Total Monthly Income Post Tax: $");
            double income = scanner.nextDouble();
            monthlyIncome = new MonthlyIncome(income);

            System.out.print("Amount to Invest Monthly: $");
            amountInvested = scanner.nextDouble();

            System.out.print("Amount to Save Monthly: $");
            amountSaved = scanner.nextDouble();

            System.out.print("Amount to Spend Monthly: $");
            amountSpent = scanner.nextDouble();

            System.out.print("Current Savings: $");
            double currentSavings = scanner.nextDouble();

            System.out.print("Current Total Invested: $");
            double currentTotalInvested = scanner.nextDouble();

            System.out.print("Average Investment Return Yearly (%): ");
            yearlyReturnRate = scanner.nextDouble();

            System.out.print("Student Loan Monthly Payment: $");
            double studentLoanPayment = scanner.nextDouble();

            System.out.print("Housing Monthly Payment: $");
            double housingPayment = scanner.nextDouble();

            System.out.print("Car Monthly Payment: $");
            double carPayment = scanner.nextDouble();
            scanner.nextLine();

            monthlyPayments = new MonthlyPayment(studentLoanPayment, housingPayment, carPayment);
            investments = new Investments(currentSavings, currentTotalInvested, yearlyReturnRate, 0);
        }

        boolean continueSimulation = true;

        for (int month = startingMonth; continueSimulation; month++) {
            System.out.println("\nMonth " + month + " Simulation");

            boolean modifying = true;
            while (modifying) {
                System.out.println("\nCurrent Values:");
                System.out.println("1. Monthly Income: $" + monthlyIncome.getTotalMonthlyIncome());
                System.out.println("2. Amount to Invest: $" + amountInvested);
                System.out.println("3. Amount to Save: $" + amountSaved);
                System.out.println("4. Amount to Spend: $" + amountSpent);
                System.out.println("5. Student Loan: $" + monthlyPayments.getStudentLoanPayment());
                System.out.println("6. Housing Payment: $" + monthlyPayments.getHousingPayment());
                System.out.println("7. Car Payment: $" + monthlyPayments.getCarPayment());
                System.out.println("8. Investment Return Rate: " + yearlyReturnRate + "%");
                System.out.println("9. Run Simulation");

                System.out.print("\nEnter choice (1-9): ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("New Monthly Income: $");
                        monthlyIncome.setTotalMonthlyIncome(scanner.nextDouble());
                        scanner.nextLine();
                        break;
                    case 2:
                        System.out.print("New Invest Amount: $");
                        amountInvested = scanner.nextDouble();
                        scanner.nextLine();
                        break;
                    case 3:
                        System.out.print("New Save Amount: $");
                        amountSaved = scanner.nextDouble();
                        scanner.nextLine();
                        break;
                    case 4:
                        System.out.print("New Spend Amount: $");
                        amountSpent = scanner.nextDouble();
                        scanner.nextLine();
                        break;
                    case 5:
                        System.out.print("New Student Loan: $");
                        monthlyPayments.setStudentLoanPayment(scanner.nextDouble());
                        scanner.nextLine();
                        break;
                    case 6:
                        System.out.print("New Housing Payment: $");
                        monthlyPayments.setHousingPayment(scanner.nextDouble());
                        scanner.nextLine();
                        break;
                    case 7:
                        System.out.print("New Car Payment: $");
                        monthlyPayments.setCarPayment(scanner.nextDouble());
                        scanner.nextLine();
                        break;
                    case 8:
                        System.out.print("New Investment Return Rate (%): ");
                        yearlyReturnRate = scanner.nextDouble();
                        investments.setAverageInvestmentReturnYearly(yearlyReturnRate);
                        scanner.nextLine();
                        break;
                    case 9:
                        modifying = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1-9.");
                }
            }

            simulateMonth(amountInvested, amountSaved, amountSpent, monthlyPayments,
                    monthlyIncome, investments, simulationResults, month);

            System.out.println("\nMonth " + month + " Results:");
            System.out.println(simulationResults.getLast());

            System.out.print("\nSimulate another month? (y/n): ");
            continueSimulation = scanner.nextLine().equalsIgnoreCase("y");
        }

        appendResultsToFile(simulationResults, filename,
                amountSaved, yearlyReturnRate, monthlyPayments);
        System.out.println("Simulation results appended to " + filename);
    }


    private static LinkedList<MonthlyResult> loadExistingResults(String filename) {
        LinkedList<MonthlyResult> results = new LinkedList<>();
        try (Scanner fileScanner = new Scanner(new File(filename))) {
            fileScanner.nextLine();
            fileScanner.nextLine();

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.startsWith("Month")) {
                    int month = Integer.parseInt(line.split(" ")[1].replace(":", ""));
                    fileScanner.nextLine();
                    double income = parseDollarValue(fileScanner.nextLine());
                    double spent = parseDollarValue(fileScanner.nextLine());
                    double invested = parseDollarValue(fileScanner.nextLine());
                    double savings = parseDollarValue(fileScanner.nextLine());
                    double totalInvested = parseDollarValue(fileScanner.nextLine());
                    double investmentReturn = parseDollarValue(fileScanner.nextLine());
                    fileScanner.nextLine();

                    results.add(new MonthlyResult(month, savings, totalInvested,
                            investmentReturn, spent, invested, income));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing file found - starting new simulation");
        }
        return results;
    }

    private static void appendResultsToFile(LinkedList<MonthlyResult> newResults, String filename,
                                            double lastSaved, double yearlyReturnRate,
                                            MonthlyPayment monthlyPayments) {
        try {
            List<String> existingContent = new ArrayList<>();

            if (Files.exists(Paths.get(filename))) {
                existingContent = Files.readAllLines(Paths.get(filename));
            }

            int dataEndIndex = existingContent.size();
            for (int i = 0; i < existingContent.size(); i++) {
                if (existingContent.get(i).startsWith("=== SIMULATION STATE ===")) {
                    dataEndIndex = i - 1;
                    break;
                }
            }

            List<String> newEntries = new ArrayList<>();
            for (MonthlyResult result : newResults) {
                newEntries.add("");
                newEntries.add(String.format("Month %d:", result.getMonth()));
                newEntries.add(String.format("  Income: $%,.2f", result.getIncome()));
                newEntries.add(String.format("  Spent: $%,.2f", result.getSpent()));
                newEntries.add(String.format("  Invested: $%,.2f", result.getInvestedAmount()));
                newEntries.add(String.format("  Saved: $%,.2f", lastSaved));
                newEntries.add(String.format("  Savings Balance: $%,.2f", result.getSavings()));
                newEntries.add(String.format("  Total Invested: $%,.2f", result.getInvested()));
                newEntries.add(String.format("  Investment Return: $%,.2f", result.getInvestmentReturn()));
            }

            List<String> newState = new ArrayList<>();
            if (!newResults.isEmpty()) {
                MonthlyResult last = newResults.getLast();
                newState.add("=== SIMULATION STATE ===");
                newState.add(String.format("Current Savings: $%,.2f", last.getSavings()));
                newState.add(String.format("Current Total Invested: $%,.2f", last.getInvested()));
                newState.add(String.format("Investment Return Rate: %.2f%%", yearlyReturnRate));
                newState.add(String.format("Last Month Income: $%,.2f", last.getIncome()));
                newState.add(String.format("Last Month Invested: $%,.2f", last.getInvestedAmount()));
                newState.add(String.format("Last Month Saved: $%,.2f", lastSaved));
                newState.add(String.format("Last Month Spent: $%,.2f", last.getSpent()));
                newState.add(String.format("Student Loan Payment: $%,.2f", monthlyPayments.getStudentLoanPayment()));
                newState.add(String.format("Housing Payment: $%,.2f", monthlyPayments.getHousingPayment()));
                newState.add(String.format("Car Payment: $%,.2f", monthlyPayments.getCarPayment()));
                newState.add("=======================");
                newState.add("");
            }

            List<String> newContent = new ArrayList<>();
            newContent.addAll(existingContent.subList(0, dataEndIndex));
            newContent.addAll(newEntries);
            newContent.addAll(newState);

            Files.write(Paths.get(filename), newContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void simulateMonth(double amountInvested, double amountSaved, double amountSpent,
                                      MonthlyPayment monthlyPayment, MonthlyIncome monthlyIncome,
                                      Investments investments, LinkedList<MonthlyResult> simulationResults,
                                      int currentMonth) {

        double monthlyInvestmentReturn = investments.getAverageInvestmentReturnYearly() / 12 / 100;
        double income = monthlyIncome.getTotalMonthlyIncome();

        double mandatoryExpenses = monthlyPayment.getStudentLoanPayment() +
                monthlyPayment.getHousingPayment() +
                monthlyPayment.getCarPayment();

        double remainingIncome = income - amountSpent - mandatoryExpenses;

        investments.setCurrentSavings(investments.getCurrentSavings() + amountSaved);
        investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() + amountInvested);

        investments.setTotalInvestmentReturn(investments.getTotalInvestmentReturn() +
                investments.getCurrentTotalInvested() * monthlyInvestmentReturn);

        if (remainingIncome < 0) {
            investments.setCurrentSavings(investments.getCurrentSavings() + remainingIncome);
            if (investments.getCurrentSavings() < 0) {
                double difference = -investments.getCurrentSavings();
                investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() - difference);
                investments.setCurrentSavings(0);
            }
        }

        simulationResults.add(new MonthlyResult(
                currentMonth,
                investments.getCurrentSavings(),
                investments.getCurrentTotalInvested(),
                investments.getTotalInvestmentReturn(),
                amountSpent + mandatoryExpenses,
                amountInvested,
                income
        ));

        investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() + investments.getTotalInvestmentReturn());
        investments.setTotalInvestmentReturn(0);
    }

    private static void writeResultsToFile(LinkedList<MonthlyResult> results, String filename,
                                           double lastSaved, double yearlyReturnRate,
                                           MonthlyPayment monthlyPayments) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("=== SIMULATION STATE ===\n");
            if (!results.isEmpty()) {
                MonthlyResult last = results.getLast();
                writer.write(String.format("Current Savings: $%,.2f\n", last.getSavings()));
                writer.write(String.format("Current Total Invested: $%,.2f\n", last.getInvested()));
                writer.write(String.format("Investment Return Rate: %.2f%%\n", yearlyReturnRate));
                writer.write(String.format("Last Month Income: $%,.2f\n", last.getIncome()));
                writer.write(String.format("Last Month Invested: $%,.2f\n", last.getInvestedAmount()));
                writer.write(String.format("Last Month Saved: $%,.2f\n", lastSaved));
                writer.write(String.format("Last Month Spent: $%,.2f\n", last.getSpent()));
                writer.write(String.format("Student Loan Payment: $%,.2f\n", monthlyPayments.getStudentLoanPayment()));
                writer.write(String.format("Housing Payment: $%,.2f\n", monthlyPayments.getHousingPayment()));
                writer.write(String.format("Car Payment: $%,.2f\n", monthlyPayments.getCarPayment()));
            }
            writer.write("=======================\n\n");
            writer.write("Monthly Financial Results\n");
            writer.write("=======================\n\n");

            for (MonthlyResult result : results) {
                writer.write(String.format("Month %d:\n", result.getMonth()));
                writer.write(String.format("  Income: $%,.2f\n", result.getIncome()));
                writer.write(String.format("  Spent: $%,.2f\n", result.getSpent()));
                writer.write(String.format("  Invested: $%,.2f\n", result.getInvestedAmount()));
                writer.write(String.format("  Saved: $%,.2f\n", lastSaved));
                writer.write(String.format("  Savings Balance: $%,.2f\n", result.getSavings()));
                writer.write(String.format("  Total Invested: $%,.2f\n", result.getInvested()));
                writer.write(String.format("  Investment Return: $%,.2f\n\n", result.getInvestmentReturn()));
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static double parseDollarValue(String line) {
        return Double.parseDouble(line.split("\\$")[1].replace(",", ""));
    }

    private static double parsePercentageValue(String line) {
        String[] parts = line.split(":");
        String percentagePart = parts[1].replace("%", "").trim();
        return Double.parseDouble(percentagePart);
    }
}

