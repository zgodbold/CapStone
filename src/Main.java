import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static FileInputStream file = null;
    ArrayList<MonthlyPayment> monthlyPayments = new ArrayList<>();
    ArrayList<MonthlyIncome> monthlyIncomes = new ArrayList<>();


    public static void main(String[] args) {
        try {
            file = new FileInputStream(args[0]);
            //tests the final input stream
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.out.println("Please run the code again with a valid file path.");
            System.exit(1);
            //if the file is not found, the trigger boolean is false
        }
        Scanner fileReader = new Scanner(file);
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            if(line.equals("Total Monthly Income Post Tax")) {

            } else if(line.equals("%Income Invested Yearly")) {

            } else if(line.equals("%Income Saved Yearly")) {

            } else if(line.equals("%Income Spent Yearly")) {

            } else if(line.equals("Current Savings")) {

            } else if(line.equals("Current Total Invested")) {

            } else if(line.equals("Average Investment Return Yearly (%)")) {

            } else if(line.equals("Total Investment Return")) {

            } else if(line.equals("Student Loan Monthly Payment")) {
                while(fileReader.hasNextLine()) {
                    line = fileReader.nextLine();
                    if(line.equals("")){
                        break;
                    } else {
                        int monthCost = 0;
                        try {
                            monthCost = Integer.parseInt(line);
                        } catch (NumberFormatException e) {
                            System.out.println("Error in Finance Simulator Sheet.");
                            System.out.println("Please run the code again with a valid file inputs.");
                            System.exit(1);
                        }
                        MonthlyPayment studentLoan = new MonthlyPayment(monthCost);
                    }
                }
            } else if(line.equals("Housing Monthly Payment")) {
                while(fileReader.hasNextLine()) {
                    line = fileReader.nextLine();
                    if(line.equals("")){
                        break;
                    } else {
                        int monthCost = 0;
                        try {
                            monthCost = Integer.parseInt(line);
                        } catch (NumberFormatException e) {
                            System.out.println("Error in Finance Simulator Sheet.");
                            System.out.println("Please run the code again with a valid file inputs.");
                            System.exit(1);
                        }
                        MonthlyPayment housingPayment = new MonthlyPayment(monthCost);
                    }
                }
            } else if(line.equals("Car Monthly Payment")) {
                while(fileReader.hasNextLine()) {
                    line = fileReader.nextLine();
                    if(line.equals("")){
                        break;
                    } else {
                        int monthCost = 0;
                        try {
                            monthCost = Integer.parseInt(line);
                        } catch (NumberFormatException e) {
                            System.out.println("Error in Finance Simulator Sheet.");
                            System.out.println("Please run the code again with a valid file inputs.");
                            System.exit(1);
                        }
                        MonthlyPayment carPayment = new MonthlyPayment(monthCost);
                    }
                }
            }
        }




    }
}