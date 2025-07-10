import java.io.Serializable;
import java.util.Objects;

public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    private double amount;
    private String category;
    private String date;

    public Expense(double amount, String category, String date) {
        this.amount = amount;
        this.category = category.trim();
        this.date = date.trim();
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("Amount: $%.2f, Category: %s, Date: %s", amount, category, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.amount, amount) == 0 &&
               category.equals(expense.category) &&
               date.equals(expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, category, date);
    }
}