import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Timer timer = new Timer(); // Timer for quiz duration
        String name = "";
        while (true) {
            System.out.print("Enter your name: ");
            name = scanner.nextLine();
            if (!name.trim().isEmpty() && !name.matches(".*\\d.*")) { // Check if name is not empty and does not contain numbers
                break;
            }
            System.out.println("Invalid input. Name cannot be empty or contain numbers.");
        }

        int age = 0;
        while (true) {
            System.out.print("Enter your age: ");
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age > 0) {
                    break;
                }
                System.out.println("Invalid input. Age must be a positive integer.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer for age.");
            }
        }

        String id = "";
        while (true) {
            System.out.print("Enter your ID : ");
            id = scanner.nextLine();
            if (id.matches("\\d+")) {
                break;
            }
            System.out.println("Invalid input. ID must be numbers only.");
        }

        Student student = new Student(name, age, id);

        while (true) {
            String category = selectCategory(scanner);
            String difficulty = selectDifficulty(scanner);

            List<Question> questions = loadQuestions(category, difficulty); // List to store the questions from the file
            // Load questions takes 2 parameters (category, difficulty) and returns a list of questions

            System.out.println("Student Info: \033[1;34m" + student.getName() + "\033[0m, " + student.getAge() + " years, \033[1;34m" + student.getId() + "\033[0m");

            String selectedCategory = getCategoryName(category);
            String selectedDifficulty = getDifficultyName(difficulty);

            System.out.println("Selected Category: " + selectedCategory + "\033[0m");
            System.out.println("Selected Difficulty: " + selectedDifficulty + "\033[0m");
            System.out.println("\033[1;34mReady to start your quiz?\033[0m");
            System.out.println("\033[1;34mRemember your score will be displayed at the end of the quiz.\033[0m");
            System.out.println("\033[1;32mPress 'Y' to start the quiz or 'N' to exit.\033[0m");
            System.out.println("\033[1;34mNote:\033[0m");
            System.out.println("\033[1;34m- Easy: 30 seconds\033[0m");
            System.out.println("\033[1;34m- Medium: 45 seconds\033[0m");
            System.out.println("\033[1;34m- Hard: 1 minute\033[0m");
            String startQuiz = scanner.nextLine();
            while (!startQuiz.equalsIgnoreCase("Y") && !startQuiz.equalsIgnoreCase("N")) {
                System.out.println("Invalid input. Press 'Y' to start the quiz or 'N' to exit.");
                startQuiz = scanner.nextLine();
            } // Check if the user entered 'Y' or 'N' 
            // TO ensure defensive programming

            if (startQuiz.equalsIgnoreCase("Y")) {
                Quiz quiz = new Quiz(questions, difficulty, scanner, timer);
                quiz.start();
            } else {
                System.out.println("Quiz exited");
                break;
            }

            while (true) {
                System.out.println("Do you want to take another quiz? (Y/N)");
                String anotherQuiz = scanner.nextLine().trim();
                if (anotherQuiz.equalsIgnoreCase("Y")) {
                    break;
                } else if (anotherQuiz.equalsIgnoreCase("N")) {
                    System.out.println("Thank you for participating!");
                    timer.cancel(); // Cancel the timer when exiting
                    return;
                } else {
                    System.out.println("Invalid input. Please enter 'Y' for yes or 'N' for no.");
                }
            }
        }

        scanner.close();
    }

    private static String selectCategory(Scanner scanner) {
        String category = "";
        while (true) {
            System.out.print("Enter question category: (math, science, history): \n");
            System.out.println("\033[1;34m1)\033[0m math\n\033[1;34m2)\033[0m science\n\033[1;34m3)\033[0m history");
            category = scanner.nextLine();
            if (category.equals("1") || category.equals("2") || category.equals("3")) {
                break;
            }
            System.out.println("Invalid input. Please enter 1, 2, or 3.");
        }
        return category;
    }

    private static String selectDifficulty(Scanner scanner) {
        String difficulty = "";
        while (true) {
            System.out.print("Enter difficulty level: (easy, medium, hard): \n");
            System.out.println("\033[1;34m1)\033[0m easy\n\033[1;34m2)\033[0m medium\n\033[1;34m3)\033[0m hard");
            difficulty = scanner.nextLine();
            if (difficulty.equals("1") || difficulty.equals("2") || difficulty.equals("3")) {
                break;
            }
            System.out.println("Invalid input. Please enter 1, 2, or 3.");
        }
        return difficulty;
    }

    private static String getCategoryName(String category) {
        switch (category) {
            case "1":
                return "math";
            case "2":
                return "science";
            case "3":
                return "history";
            default:
                return category;
        }
    }

    private static String getDifficultyName(String difficulty) {
        switch (difficulty) {
            case "1":
                return "easy";
            case "2":
                return "medium";
            case "3":
                return "hard";
            default:
                return difficulty;
        }
    }

    private static List<Question> loadQuestions(String category, String difficulty) {
        List<Question> questions = new ArrayList<>();
        // This is my directory for the files containing the questions
        String fileName = "/e:/Alkolya/Java Application/questions_" + category + "_" + difficulty + ".txt";
        // You can change it to your own directory
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";"); // See after every semi colon we divide the question, options and correct answer
                // This is a sample : What is 2 + 2?;3;4;5;6;B
                if (parts.length == 6) {
                    String questionText = parts[0];
                    String[] options = { "\033[1;34mA)\033[0m " + parts[1], "\033[1;34mB)\033[0m " + parts[2], "\033[1;34mC)\033[0m " + parts[3], "\033[1;34mD)\033[0m " + parts[4] };
                    char correctAnswer = parts[5].charAt(0);
                    questions.add(new Question(questionText, options, correctAnswer));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading questions file: " + e.getMessage());
        }
        return questions;
    }
}

// For all student Data and its setters and getters 
class Student 
{ 
    private String name;
    private int age;
    private String id;
    
    public Student(String name, int age, String id) 
    {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public int getAge() 
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

}

class Question {
    private String questionText;
    private String[] options;
    private char correctAnswer;

    public Question(String questionText, String[] options, char correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public char getCorrectAnswer() {
        return correctAnswer;
    }
}

class Quiz { // All the processes of the quiz
    private List<Question> questions;
    private Timer timer;
    private boolean timeUp;
    private long timerDuration;
    private Scanner scanner;

    public Quiz(List<Question> questions, String difficulty, Scanner scanner, Timer timer) {
        this.questions = questions;
        // I added a timer feature to the quiz here
        this.timer = timer;
        this.timeUp = false;
        this.scanner = scanner;
        setTimerDuration(difficulty);
    }

    private void setTimerDuration(String difficulty) {
        switch (difficulty) {
            case "1": // easy
                this.timerDuration = 30000; // 30 seconds
                break;
            case "2": // medium
                this.timerDuration = 45000; // 45 seconds
                break;
            case "3": // hard
                this.timerDuration = 60000; // 1 minute
                break;
            default:
                this.timerDuration = 60000; 
                break;
        }
    }

    public void start() {
        int score = 0;
        timeUp = false; 

        timer.cancel(); // By every start we cancel the timer and reset it
        timer = new Timer();

        // Start the timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeUp = true;
                System.out.println("\n\033[1;31mTime's up!\033[0m");
                timer.cancel();
            }
        }, timerDuration);

        for (Question question : questions) {
            // If the time is done close the quiz and show the score
            if (timeUp) break;

            System.out.println(question.getQuestionText());

            for (String option : question.getOptions()) 
            {
                System.out.println(option);
            }

            char answer = ' ';
            while (true) 
            {
                if (timeUp) break;

                System.out.print("Your answer: ");

                String input = scanner.nextLine().toUpperCase();
                
                if (input.length() == 1 && "ABCD".indexOf(input.charAt(0)) != -1) 
                {
                    answer = input.charAt(0);
                    break;
                } else {
                    System.out.println("Invalid input. Please enter A, B, C, or D.");
                }
            }
            if (timeUp) break;

            if (answer == question.getCorrectAnswer()) {
                System.out.println("\033[1;32mCorrect!\033[0m");
                Toolkit.getDefaultToolkit().beep();
                score++;
            } else {
                System.out.println("\033[1;31mIncorrect! The correct answer was: " + question.getCorrectAnswer() + "\033[0m");
                Toolkit.getDefaultToolkit().beep();
            }
        }

        System.out.println("Quiz finished! Your score: " + score + "/" + questions.size());
    }
}