package view;

import controller.InstructorController;
import model.Instructor;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GUIView extends JFrame {

    private InstructorController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private Set<Integer> unsavedRows;
    private boolean isLoading = false;

    public GUIView() {
        controller = new InstructorController();
        unsavedRows = new HashSet<>();

        setTitle("University Instructor Database");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                attemptExit();
            }
        });
        buildSidebar();
        buildMainArea();
        loadDataFromDatabase();
    }

    private void buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebar.setBackground(new Color(230, 230, 240));

        JButton btnAdd = new JButton("Add Instructor");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnSave = new JButton("Save Updates");
        JButton btnExit = new JButton("Exit");

        btnAdd.addActionListener(e -> addInstructor());
        btnDelete.addActionListener(e -> deleteInstructor());
        btnSave.addActionListener(e -> saveUpdates());
        btnExit.addActionListener(e -> attemptExit());

        sidebar.add(new JLabel("Menu Options", SwingConstants.CENTER));
        sidebar.add(btnAdd);
        sidebar.add(btnDelete);
        sidebar.add(btnSave);
        sidebar.add(new JLabel(""));
        sidebar.add(btnExit);

        add(sidebar, BorderLayout.WEST);
    }

    private void buildMainArea() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] searchColumns = {"ID", "name", "dept_name"};
        JComboBox<String> searchCombo = new JComboBox<>(searchColumns);
        JTextField searchField = new JTextField(20);
        JButton btnSearch = new JButton("Search / Filter");
        JButton btnClear = new JButton("Clear Filter");

        btnSearch.addActionListener(e -> {
            String text = searchField.getText();
            int colIndex = searchCombo.getSelectedIndex();
            if (text.trim().isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, colIndex));
            }
        });

        btnClear.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        searchPanel.add(new JLabel("Search By:"));
        searchPanel.add(searchCombo);
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Department", "Salary"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Prevent editing the ID column
            }
        };

        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        tableModel.addTableModelListener(e -> {
            if (!isLoading && e.getType() == TableModelEvent.UPDATE) {
                unsavedRows.add(e.getFirstRow());
            }
        });

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadDataFromDatabase() {
        // SwingWorker prevents the GUI from freezing while fetching data
        SwingWorker<List<Instructor>, Void> worker = new SwingWorker<>() {
            protected List<Instructor> doInBackground() {
                return controller.getAllInstructors();
            }
            protected void done() {
                try {
                    isLoading = true;
                    List<Instructor> instructors = get();
                    tableModel.setRowCount(0); // Clear table
                    for (Instructor inst : instructors) {
                        tableModel.addRow(new Object[]{inst.getId(), inst.getName(), inst.getDeptName(), inst.getSalary()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GUIView.this, "Error loading data.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    isLoading = false;
                }
            }
        };
        worker.execute();
    }

    private void addInstructor() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField salaryField = new JTextField();

        Object[] message = {
                "ID:", idField,
                "Name:", nameField,
                "Department:", deptField,
                "Salary:", salaryField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Instructor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText();
            String name = nameField.getText();
            String dept = deptField.getText();

            try {
                double salary = Double.parseDouble(salaryField.getText());

                // Run insertion in background
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    protected Boolean doInBackground() {
                        return controller.addInstructor(id, name, dept, salary);
                    }

                    protected void done() {
                        try {
                            if (get()) {
                                isLoading = true; // Prevent triggering unsaved changes alert
                                tableModel.addRow(new Object[]{id, name, dept, salary});
                                isLoading = false;
                                JOptionPane.showMessageDialog(GUIView.this, "Success! Instructor added.");
                            } else {
                                JOptionPane.showMessageDialog(GUIView.this, "Operation Failed: ID might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteInstructor() {
        int selectedViewRow = table.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to delete.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        String id = tableModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete instructor ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                protected Boolean doInBackground() {
                    return controller.deleteInstructor(id);
                }
                protected void done() {
                    try {
                        if (get()) {
                            isLoading = true;
                            tableModel.removeRow(modelRow);
                            unsavedRows.remove(modelRow); // Fix indices if necessary
                            isLoading = false;
                            JOptionPane.showMessageDialog(GUIView.this, "Instructor deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(GUIView.this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private void saveUpdates() {
        if (unsavedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No updates to save.");
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                boolean allSuccess = true;
                for (int row : unsavedRows) {
                    String id = tableModel.getValueAt(row, 0).toString();
                    String name = tableModel.getValueAt(row, 1).toString();
                    String dept = tableModel.getValueAt(row, 2).toString();
                    double salary = Double.parseDouble(tableModel.getValueAt(row, 3).toString());

                    if (!controller.updateInstructor(id, name, dept, salary)) {
                        allSuccess = false;
                    }
                }
                return allSuccess;
            }
            protected void done() {
                try {
                    if (get()) {
                        unsavedRows.clear();
                        JOptionPane.showMessageDialog(GUIView.this, "All updates saved successfully!");
                    } else {
                        JOptionPane.showMessageDialog(GUIView.this, "Some updates failed to save.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void attemptExit() {
        if (!unsavedRows.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes in the table.\nWould you like to save them before exiting?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                saveUpdates();
                System.exit(0);
            } else if (choice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }
}