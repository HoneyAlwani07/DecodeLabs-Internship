import java.util.Scanner;

public class StudentGradeCalculator {

    
    static final int  MAX_MARKS_PER_SUBJECT = 100;
    static final int  PASS_MARK_PER_SUBJECT = 33;   // each subject must be >= 33
    static final int  PASS_PERCENTAGE       = 40;   // overall average must be >= 40

    
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

          int numSubjects = 0;
        while (true) {
            System.out.print("\nEnter the number of subjects (1-20): ");
            String line = sc.nextLine().trim();
            try {
                numSubjects = Integer.parseInt(line);
                if (numSubjects < 1 || numSubjects > 20) {
                    System.out.println("Please enter an integer between 1 and 20.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        
        System.out.print("Enter student name (press Enter to skip): ");
        String studentName = sc.nextLine().trim();
        if (studentName.isEmpty()) {
            studentName = "Student";
        }

        
        String[] subjectNames = new String[numSubjects];
        int[]    marks        = new int[numSubjects];
        boolean  anyFail      = false;

      
        System.out.println("Enter subject name and marks (0-100) for each subject");
       

        for (int i = 0; i < numSubjects; i++) {
            
            System.out.print("\nSubject " + (i + 1) + " name (press Enter to skip): ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                name = "Subject " + (i + 1);
            }
            subjectNames[i] = name;

        
            int m = -1;
            while (true) {
                System.out.print("   Marks for " + name + " (0-100): ");
                String ml = sc.nextLine().trim();
                try {
                    m = Integer.parseInt(ml);
                    if (m < 0 || m > MAX_MARKS_PER_SUBJECT) {
                        System.out.println("Marks must be between 0 and 100.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Try again.");
                }
            }
            marks[i] = m;
            if (m < PASS_MARK_PER_SUBJECT) {
                anyFail = true;
            }
        }

        
        int totalMarks = 0;
        for (int m : marks) {
            totalMarks += m;
        }
        double averagePercentage = (double) totalMarks / numSubjects;  // out of 100
        String grade = calculateGrade(averagePercentage);
        boolean isPassed = (averagePercentage >= PASS_PERCENTAGE) && (!anyFail);

        
        printReport(studentName, subjectNames, marks, totalMarks,
                    averagePercentage, grade, isPassed, anyFail);

        sc.close();
    }

    
    static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B";
        if (percentage >= 60) return "C";
        if (percentage >= 50) return "D";
        if (percentage >= 40) return "E";
        return "F";
    }

    
    static void printReport(String name, String[] subjects, int[] marks,
                            int total, double avg, String grade,
                            boolean isPassed, boolean anySubjectFailed) {

        int numSubjects = subjects.length;
        int maxTotal = numSubjects * MAX_MARKS_PER_SUBJECT;

        System.out.println();
       
        System.out.println("RESULT REPORT");
      
        System.out.printf("Student Name : %s%n", name);
        System.out.printf("Total Subjects: %d%n", numSubjects);
       
        System.out.printf("%-4s %-25s %10s %10s%n", "#", "Subject", "Marks", "Status");
       

        for (int i = 0; i < numSubjects; i++) {
            String status = (marks[i] >= PASS_MARK_PER_SUBJECT) ? "PASS" : "FAIL";
            System.out.printf("%-4d %-25s %7d/100 %10s%n",
                              (i + 1), truncate(subjects[i], 25), marks[i], status);
        }

     
        System.out.printf("Total Marks        : %d / %d%n", total, maxTotal);
        System.out.printf("Average Percentage : %.2f%%%n", avg);
        System.out.printf("Grade              : %s%n", grade);

        if (anySubjectFailed) {
            System.out.println("Note               : One or more subjects below pass mark (33).");
        }

        System.out.printf("Overall Result     : %s%n",
                          isPassed ? "PASSED" : " FAILED");
       
        System.out.println(" Thank you for using DecodeLabs Calculator           ");
       
    }

    static String truncate(String s, int maxLen) {
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 1) + ".";
    }
}
