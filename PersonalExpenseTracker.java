import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class PersonalExpenseTracker extends JFrame {
    private ExpenseManager manager;
    private JTextField amountField, categoryField, dateField;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;
    private static final Color PRIMARY_COLOR = new Color(138, 43, 226);  // Rich violet
    private static final Color SECONDARY_COLOR = new Color(92, 184, 92);  // Fresh green
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);  // Soft light purple
    private static final Color TEXT_COLOR = new Color(51, 51, 51);  // Dark gray
    private static final Color ADD_BUTTON_COLOR = new Color(66, 139, 202);  // Sky blue
    private static final Color HEADER_COLOR = new Color(60, 90, 153);  // Deep blue
    private static final Color BORDER_COLOR = new Color(218, 220, 224);  // Light gray

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return field;
    }

    private JButton createStyledButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR);
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color darkerColor = new Color(
            Math.max((int)(baseColor.getRed() * 0.8), 0),
            Math.max((int)(baseColor.getGreen() * 0.8), 0),
            Math.max((int)(baseColor.getBlue() * 0.8), 0));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkerColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        
        return button;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Amount", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        expenseTable = new JTable(tableModel);
        expenseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        expenseTable.setRowHeight(30);
        expenseTable.setGridColor(BORDER_COLOR);
        expenseTable.setSelectionBackground(new Color(232, 240, 254));
        
        JTableHeader header = expenseTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
    
    public PersonalExpenseTracker() {
        manager = new ExpenseManager();
        setTitle("Personal Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main Panels
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        
        // Input Panel
        topPanel.add(createInputPanel(), BorderLayout.CENTER);
        
        // Statistics Panel
        topPanel.add(createStatsPanel(), BorderLayout.EAST);
        
        // Table
        JScrollPane scrollPane = createTablePanel();
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set frame properties
        setSize(900, 600);
        setLocationRelativeTo(null);
        refreshCategoryComboBox();
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR, 2), 
            "Add New Expense"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        inputPanel.setBackground(Color.WHITE);
        
        // Labels
        JLabel[] labels = {
            new JLabel("Amount ($):"),
            new JLabel("Category:"),
            new JLabel("Date (YYYY-MM-DD):")
        };
        
        for (JLabel label : labels) {
            label.setForeground(TEXT_COLOR);
            label.setFont(new Font("Arial", Font.BOLD, 14));
        }
        
        // Amount
        inputPanel.add(labels[0]);
        amountField = createStyledTextField();
        inputPanel.add(amountField);
        
        // Category with ComboBox
        inputPanel.add(labels[1]);
        JPanel categoryPanel = new JPanel(new BorderLayout(5, 0));
        categoryPanel.setBackground(Color.WHITE);
        categoryField = createStyledTextField();
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryComboBox.setBackground(Color.WHITE);
        categoryComboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        categoryComboBox.addActionListener(e -> categoryField.setText((String)categoryComboBox.getSelectedItem()));
        categoryPanel.add(categoryField, BorderLayout.CENTER);
        categoryPanel.add(categoryComboBox, BorderLayout.EAST);
        inputPanel.add(categoryPanel);
        
        // Date
        inputPanel.add(labels[2]);
        dateField = createStyledTextField();
        dateField.setText(LocalDate.now().toString());
        inputPanel.add(dateField);
        
        // Add Button
        JButton addButton = createStyledButton("Add Expense", ADD_BUTTON_COLOR);
        addButton.addActionListener(e -> addExpense());
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);
        
        return inputPanel;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR, 2), "Statistics"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        statsPanel.setBackground(Color.WHITE);
        
        JLabel totalLabel = new JLabel(String.format("Total Expenses: $%.2f", manager.getTotalExpenses()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalLabel.setForeground(TEXT_COLOR);
        
        Map<String, Double> monthlyTotals = manager.getMonthlyTotals();
        String currentMonth = YearMonth.now().toString();
        double monthlyTotal = monthlyTotals.getOrDefault(currentMonth, 0.0);
        JLabel monthlyLabel = new JLabel(String.format("This Month: $%.2f", monthlyTotal));
        monthlyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthlyLabel.setForeground(TEXT_COLOR);
        
        JButton refreshButton = createStyledButton("Refresh Stats", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> refreshStats(totalLabel, monthlyLabel));
        
        statsPanel.add(totalLabel);
        statsPanel.add(monthlyLabel);
        statsPanel.add(refreshButton);
        
        return statsPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton categoryTotalsBtn = createStyledButton("Category Totals", PRIMARY_COLOR);
        categoryTotalsBtn.addActionListener(e -> showCategoryTotals());
        
        JButton monthlyTotalsBtn = createStyledButton("Monthly Totals", PRIMARY_COLOR);
        monthlyTotalsBtn.addActionListener(e -> showMonthlyTotals());
        
        JButton dateRangeBtn = createStyledButton("Date Range Report", PRIMARY_COLOR);
        dateRangeBtn.addActionListener(e -> showDateRangeReport());
        
        buttonPanel.add(categoryTotalsBtn);
        buttonPanel.add(monthlyTotalsBtn);
        buttonPanel.add(dateRangeBtn);
        
        return buttonPanel;
    }
    
    private void refreshStats(JLabel totalLabel, JLabel monthlyLabel) {
        totalLabel.setText(String.format("Total Expenses: $%.2f", manager.getTotalExpenses()));
        
        Map<String, Double> monthlyTotals = manager.getMonthlyTotals();
        String currentMonth = YearMonth.now().toString();
        double monthlyTotal = monthlyTotals.getOrDefault(currentMonth, 0.0);
        monthlyLabel.setText(String.format("This Month: $%.2f", monthlyTotal));
    }
    
    private void refreshCategoryComboBox() {
        categoryComboBox.removeAllItems();
        categoryComboBox.addItem("");
        
        // Add default categories first
        for (String category : manager.getDefaultCategories()) {
            categoryComboBox.addItem(category);
        }
        
        // Add existing categories that aren't in defaults
        for (String category : manager.getAllCategories()) {
            if (!manager.getDefaultCategories().contains(category)) {
                categoryComboBox.addItem(category);
            }
        }
    }

    private void clearFields() {
        amountField.setText("");
        categoryField.setText("");
        categoryComboBox.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        amountField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void showCategoryTotals() {
        Map<String, Double> totals = manager.getAllCategoryTotals();
        StringBuilder message = new StringBuilder("Category Totals:\n\n");
        totals.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                message.append(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue())));
        showTextDialog("Category Totals", message.toString());
    }
    
    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String date = dateField.getText();
            
            manager.addExpense(amount, category, date);
            tableModel.addRow(new Object[]{String.format("$%.2f", amount), category, date});
            
            clearFields();
            refreshCategoryComboBox();
            
        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }
    
    private void showDateRangeReport() {
        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (YYYY-MM-DD):"));
        panel.add(endDateField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Date Range", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                List<Expense> rangeExpenses = manager.getExpensesByDateRange(
                    startDateField.getText(), endDateField.getText());
                    
                showExpenseList("Expenses from " + startDateField.getText() + 
                              " to " + endDateField.getText(), rangeExpenses);
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }
    
    private void showMonthlyTotals() {
        Map<String, Double> totals = manager.getMonthlyTotals();
        StringBuilder message = new StringBuilder("Monthly Totals:\n\n");
        totals.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                message.append(String.format("%s: $%.2f\n", entry.getKey(), entry.getValue())));
        
        showTextDialog("Monthly Totals", message.toString());
    }
    
    private void showExpenseList(String title, List<Expense> expenses) {
        StringBuilder message = new StringBuilder();
        double total = 0;
        
        for (Expense expense : expenses) {
            message.append(expense.toString()).append("\n");
            total += expense.getAmount();
        }
        
        message.append("\nTotal: $").append(String.format("%.2f", total));
        showTextDialog(title, message.toString());
    }
    
    private void showTextDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, title, 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new PersonalExpenseTracker().setVisible(true);
        });
    }
}