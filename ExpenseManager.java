import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private List<Expense> expenses;
    private static final String DATA_FILE = "expenses.dat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Set<String> DEFAULT_CATEGORIES = new HashSet<>(Arrays.asList(
        "Groceries",
        "Transportation",
        "Entertainment",
        "Utilities",
        "Healthcare",
        "Dining Out",
        "Shopping",
        "Education"
    ));

    public ExpenseManager() {
        expenses = loadExpenses();
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        if (expenses.isEmpty()) {
            // Add a small initial expense for each category
            LocalDate today = LocalDate.now();
            DEFAULT_CATEGORIES.forEach(category -> {
                try {
                    addExpense(0.01, category, today.format(DATE_FORMATTER));
                } catch (IllegalArgumentException e) {
                    // Ignore any validation errors during initialization
                }
            });
            // Remove the initial expenses to keep the list clean
            expenses.clear();
            saveExpenses();
        }
    }

    public Set<String> getDefaultCategories() {
        return new HashSet<>(DEFAULT_CATEGORIES);
    }
    public void addExpense(double amount, String category, String date) throws IllegalArgumentException {
        validateDate(date);
        validateAmount(amount);
        validateCategory(category);
        
        expenses.add(new Expense(amount, category, date));
        saveExpenses();
    }

    private void validateDate(String date) throws IllegalArgumentException {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD");
        }
    }

    private void validateAmount(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private void validateCategory(String category) throws IllegalArgumentException {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
    }

    public double getTotalByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getTotalByDate(String date) {
        return expenses.stream()
                .filter(e -> e.getDate().equals(date))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<String, Double> getAllCategoryTotals() {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                    Expense::getCategory,
                    Collectors.summingDouble(Expense::getAmount)
                ));
    }

    public Map<String, Double> getMonthlyTotals() {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                    e -> e.getDate().substring(0, 7),
                    Collectors.summingDouble(Expense::getAmount)
                ));
    }

    public List<Expense> getExpensesByDateRange(String startDate, String endDate) {
        validateDate(startDate);
        validateDate(endDate);
        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);

        return expenses.stream()
                .filter(e -> {
                    LocalDate expenseDate = LocalDate.parse(e.getDate(), DATE_FORMATTER);
                    return !expenseDate.isBefore(start) && !expenseDate.isAfter(end);
                })
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());
    }

    public double getTotalExpenses() {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<String> getAllCategories() {
        return expenses.stream()
                .map(Expense::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private List<Expense> loadExpenses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            return (List<Expense>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveExpenses() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}