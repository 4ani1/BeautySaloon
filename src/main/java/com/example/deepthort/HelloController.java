package com.example.deepthort;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.ZoneId;

public class HelloController implements Initializable {
    private final DBUtil dbUtil = new DBUtil();
    Connection conn = null;
    private int idCount = 0;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Employee, DatePicker> brithCal;

    @FXML
    private DatePicker brithDate;

    @FXML
    private DatePicker receiptDate1;

    @FXML
    private Button changeButton;

    @FXML
    private Button delButton;

    @FXML
    private Button exportButton;

    @FXML
    private Label errorLable;

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, String> lastNameCal;

    @FXML
    private TextField lastNameField;

    @FXML
    private TableColumn<Employee, String> nameCal;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<Employee, String> numberCal;

    @FXML
    private TextField numberField;

    @FXML
    private TableColumn<Employee, DatePicker> receiptCal;

    @FXML
    private TableColumn<Employee, String> specCal;

    @FXML
    private ComboBox<String> specField;

    @FXML
    private TableColumn<Employee, String> statusCal;

    @FXML
    private ComboBox<String> statusField;

    @FXML
    private TableColumn<Employee, String> surNameCol;

    @FXML
    private TextField surNameField;



    @FXML
    void onAddButton(ActionEvent event) {
        String name = nameField.getText();
        String surName = surNameField.getText();
        String lastName = lastNameField.getText();
        String speciality = specField.getValue();
        String status = statusField.getValue();
        String number = numberField.getText();
        LocalDate birthDate = brithDate.getValue();
        LocalDate receiptDate = receiptDate1.getValue();

        if (name.isEmpty() || surName.isEmpty() || lastName.isEmpty() || speciality == null ||
                status == null || number.isEmpty() || birthDate == null || receiptDate == null) {
            errorLable.setText("Не все строки были заполнены!");
            return; // Выходим из метода, чтобы предотвратить добавление записи с неполными данными
        }

        try {
            String query = "INSERT INTO employee2 (employee_name, employee_surname, employee_lastname, " +
                    " employee_spec, employee_number, employee_brithdate, employee_receiptdate, employee_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, surName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, speciality);
            preparedStatement.setString(5, number);
            preparedStatement.setDate(6, java.sql.Date.valueOf(birthDate));
            preparedStatement.setDate(7, java.sql.Date.valueOf(receiptDate));
            preparedStatement.setString(8, status);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Данные успешно добавлены в базу данных.");
                clearFields();
                loadDataFromDatabase();
            }

            errorLable.setText("");
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onChangeButton(ActionEvent event) {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            int selectedIndex = employeeTable.getSelectionModel().getSelectedIndex();

            // Показать данные выбранного сотрудника в текстовых полях для редактирования
            nameField.setText(selectedEmployee.getName());
            surNameField.setText(selectedEmployee.getSurName());
            lastNameField.setText(selectedEmployee.getLastName());
            specField.setValue(selectedEmployee.getSpeciality());
            statusField.setValue(selectedEmployee.getStatus());
            numberField.setText(String.valueOf(selectedEmployee.getNumber()));

            // Преобразовать java.util.Date в LocalDate для DatePicker
            if (selectedEmployee.getBrithDate() != null) {
                LocalDate brithLocalDate = selectedEmployee.getBrithDate();
                brithDate.setValue(brithLocalDate);
            } else {
                brithDate.setValue(null);
            }

            if (selectedEmployee.getReceiptDate() != null) {
                LocalDate receiptLocalDate = selectedEmployee.getReceiptDate();
                receiptDate1.setValue(receiptLocalDate);
            } else {
                receiptDate1.setValue(null);
            }

            // Изменить текст кнопки на "Сохранить"
            changeButton.setText("Сохранить");
            // Добавить обработчик для сохранения изменений
            delButton.setDisable(true); // Кнопка снова активная
            addButton.setDisable(true); // Кнопка снова активная

            changeButton.setOnAction(e -> onSaveButton());
        }
    }

    private void onSaveButton() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            String name = nameField.getText();
            String surName = surNameField.getText();
            String lastName = lastNameField.getText();
            String spec = specField.getValue();
            String status = statusField.getValue();
            String number = numberField.getText();

            LocalDate brithDateValue = brithDate.getValue();
            Date brithDate = (brithDateValue != null) ? java.sql.Date.valueOf(brithDateValue) : null;

            LocalDate receiptDateValue = receiptDate1.getValue();
            Date receiptDate = (receiptDateValue != null) ? java.sql.Date.valueOf(receiptDateValue) : null;

            int employeeId = getEmployeeIdFromDatabase(selectedEmployee.getName());

            try {
                String query = "UPDATE employee2 SET employee_name=?, employee_surname=?, employee_lastname=?, " +
                        "employee_spec=?, employee_number=?, employee_brithdate=?, employee_receiptdate=?, employee_status=? " +
                        "WHERE employee_id=?";

                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surName);
                preparedStatement.setString(3, lastName);
                preparedStatement.setString(4, spec);
                preparedStatement.setString(5, number);
                preparedStatement.setDate(6, (java.sql.Date) brithDate);
                preparedStatement.setDate(7, (java.sql.Date) receiptDate);
                preparedStatement.setString(8, status);
                preparedStatement.setInt(9, employeeId);

                System.out.println(employeeId);

                System.out.println("!!!!!");

                int rowsUpdated = preparedStatement.executeUpdate();
                System.out.println(rowsUpdated);

                if (rowsUpdated > 0) {
                    System.out.println("Данные успешно обновлены в базе данных.");
                }

                // Показываем всплывающее окно с подтверждением перед сохранением
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение");
                alert.setHeaderText("Вы действительно хотите сохранить изменения?");
                alert.setContentText("После сохранения изменения будут внесены в базу данных.");

                // Определение кнопок в диалоге
                ButtonType buttonTypeSave = new ButtonType("Сохранить");
                ButtonType buttonTypeCancel = new ButtonType("Отмена");

                alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeCancel);

                // Ожидаем выбора пользователя
                alert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == buttonTypeSave) {
                        // Пользователь нажал "Сохранить", выполняем сохранение
                        try {
                            preparedStatement.executeUpdate();
                            System.out.println("Данные успешно обновлены в базе данных.");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                changeButton.setText("Изменить");
                changeButton.setOnAction(this::onChangeButton);
                loadDataFromDatabase(); // Обновить данные в TableView
                clearFields(); // Очистить текстовые поля после сохранения

                delButton.setDisable(false);
                addButton.setDisable(false);

                preparedStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getEmployeeIdFromDatabase(String name) {
        int employeeId = 0;
        try {
            String query = "SELECT employee_id FROM employee2 WHERE employee_name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeId = resultSet.getInt("employee_id");
            } else {
                // Обработайте ситуацию, когда сотрудник не найден в базе данных
                System.out.println("Сотрудник не найден в базе данных.");
            }
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return employeeId;
    }

    @FXML
    void onDelButton(ActionEvent event) {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        // Окно подверждения удаления
        if (selectedEmployee != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Вы уверены, что хотите удалить сотрудника?");
            alert.setContentText("Имя: " + selectedEmployee.getName() +
                    "\nФамилия: " + selectedEmployee.getSurName() +
                    "\nОтчество: " + selectedEmployee.getLastName());

            // Отображаем всплывающее окно и ждем нажатия кнопки "OK" или "Отмена"
            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

            if (result == ButtonType.OK) {
                // Пользователь подтвердил удаление, выполняем удаление
                deleteEmployee(selectedEmployee);
                loadDataFromDatabase();
            }
        }
    }

    private void deleteEmployee(Employee employee) {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            String selectedName = selectedEmployee.getName();

            try {
                String query = "DELETE FROM employee2 WHERE employee_name = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, selectedName);

                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Данные успешно удалены из базы данных.");
                    employees.remove(selectedEmployee);
                    employeeTable.setItems(employees);
                }
                errorLable.setText("");
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onExportButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Файлы Excel", "*.xlsx"));

        // Открываем диалоговое окно для выбора места сохранения файла
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            exportToExcel(file);
        }
    }

    public void exportToExcel(File file) {
        try {
            // Получите данные из базы данных
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM employee2");

            // Создайте новый Excel-файл
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Cотрудники организации");

            // Создаем стиль ячейки для даты
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));

            // Заголовки столбцов
            String[] headers = {"№", "Имя", "Фамилия", "Отчество", "Специальность", "Телефон", "Дата рождения", "Дата приема", "Статус"};

            // Запишите заголовки в первую строку
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Запишите данные в Excel-файл
            int rowNumber = 1;
            while (resultSet.next()) {
                Row row = sheet.createRow(rowNumber++);
                // Запишите данные в ячейки соответствующих столбцов
                row.createCell(0).setCellValue(rowNumber - 1); // Нумерация столбцов
                row.createCell(1).setCellValue(resultSet.getString("employee_name"));
                row.createCell(2).setCellValue(resultSet.getString("employee_surname"));
                row.createCell(3).setCellValue(resultSet.getString("employee_lastname"));
                row.createCell(4).setCellValue(resultSet.getString("employee_spec"));
                row.createCell(5).setCellValue(resultSet.getString("employee_number"));

                // Записываем даты в ячейки с правильным форматом
                LocalDate birthDate = resultSet.getDate("employee_brithdate").toLocalDate();
                java.sql.Date sqlBirthDate = java.sql.Date.valueOf(birthDate);
                row.createCell(6).setCellValue(sqlBirthDate);
                row.getCell(6).setCellStyle(dateCellStyle); // Устанавливаем стиль для ячейки с датой

                LocalDate receiptDate = resultSet.getDate("employee_receiptdate").toLocalDate();
                java.sql.Date sqlReceiptDate = java.sql.Date.valueOf(receiptDate);
                row.createCell(7).setCellValue(sqlReceiptDate);
                row.getCell(7).setCellStyle(dateCellStyle); // Устанавливаем стиль для ячейки с датой

                row.createCell(8).setCellValue(resultSet.getString("employee_status"));
            }

            // Получаем текущую дату и время
            LocalDateTime now = LocalDateTime.now();

            // Форматируем дату и время в строку с помощью DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            String formattedDateTime = now.format(formatter);

            // Создаем строку для даты и времени выгрузки
            Row dateRow = sheet.createRow(rowNumber + 1);

            // Создаем ячейку и записываем дату и время выгрузки
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Дата и время выгрузки: " + formattedDateTime);

            // Сохраните Excel-файл
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            workbook.close();
            System.out.println("Данные успешно экспортированы в Excel-файл.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        surNameCol.setCellValueFactory(new PropertyValueFactory<Employee, String>("surName"));
        nameCal.setCellValueFactory(new PropertyValueFactory<Employee, String>("name"));
        lastNameCal.setCellValueFactory(new PropertyValueFactory<Employee, String>("lastName"));
        specCal.setCellValueFactory(new PropertyValueFactory<Employee, String>("speciality"));
        numberCal.setCellValueFactory(new PropertyValueFactory<Employee, String>("number"));
        brithCal.setCellValueFactory(new PropertyValueFactory<Employee, DatePicker>("brithDate"));
        receiptCal.setCellValueFactory(new PropertyValueFactory<Employee, DatePicker>("receiptDate"));
        statusCal.setCellValueFactory(new PropertyValueFactory<Employee, String>("Status"));

        specField.setItems(getStringListForSpeciality());
        statusField.setItems(getStringListForStatus());
        System.out.println(idCount);

        conn = dbUtil.DBConnect();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try {
            if (conn != null) {
                try {
                    String query = "SELECT employee_name, employee_surname, employee_lastname, employee_spec, " +
                            "employee_number, employee_brithdate, employee_receiptdate, employee_status FROM employee2";

                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    employees.clear();

                    while (resultSet.next()) {
                        String employee_name = resultSet.getString("employee_name");
                        String employee_surName = resultSet.getString("employee_surName");
                        String employee_lastName = resultSet.getString("employee_lastName");
                        String employee_spec = resultSet.getString("employee_spec");
                        String employee_number = resultSet.getString("employee_number");
                        LocalDate employee_brithDate = resultSet.getDate("employee_brithDate").toLocalDate();
                        LocalDate employee_receiptDate = resultSet.getDate("employee_receiptDate").toLocalDate();
                        String employee_status = resultSet.getString("employee_status");

                        // Здесь мы используем конструктор класса Employee с параметрами Date
                        employees.add(new Employee(employee_surName, employee_name, employee_lastName, employee_spec,
                                employee_number, employee_brithDate, employee_receiptDate, employee_status));
                        idCount++;
                    }

                    employeeTable.setItems(employees);
                    errorLable.setText("");
                    resultSet.close();
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> getStringListForSpeciality() { // Добовляем параметры для ComboBox Специальность
        ObservableList<String> stringList = FXCollections.observableArrayList();
        stringList.add("Не указано");
        stringList.add("Визажист");
        stringList.add("Администратор");
        stringList.add("Мастер ногтевого сервиса");
        stringList.add("Парикмахер-стилист");
        stringList.add("Колорист");
        stringList.add("Стилист-визажист");
        stringList.add("Косметолог");
        stringList.add("Директор");
        return stringList;
    }

    private ObservableList<String> getStringListForStatus() { // Добовляем параметры для ComboBox Статус
        ObservableList<String> stringList = FXCollections.observableArrayList();
        stringList.add("Не указано");
        stringList.add("Уволен");
        stringList.add("Работает");
        return stringList;
    }

    private void clearFields() { // Метод для очистки текстовых полей
        nameField.clear();
        surNameField.clear();
        lastNameField.clear();
        specField.getSelectionModel().clearSelection();
        statusField.getSelectionModel().clearSelection();
        numberField.clear();
        brithDate.setValue(null);
        receiptDate1.setValue(null);
    }
}