package controllers;

import Modules.Revenue;
import Service.Revenueservice;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.beans.value.ObservableValue;

public class UpdateRevenue {

    @FXML private TextField idField, dateField, modeField, montantField, deviseField;




    @FXML
    public void initialize() {
        // لما المستخدم يكتب ID، نحاول نجيب بيانات الريفنو تلقائياً
        idField.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            if (!newValue.isEmpty() && newValue.matches("\\d+")) {
                chargerRevenue(Integer.parseInt(newValue));
            } else {
                clearFields();
            }
        });
    }
    public void setRevenueData(Revenue r) {
        idField.setText(String.valueOf(r.getIdvoy())); // ممكن تخفي هذا الحقل لاحقًا
        dateField.setText(r.getDaterevenue());
        modeField.setText(r.getModereception());
        montantField.setText(String.valueOf(r.getRmontant()));
        deviseField.setText(String.valueOf(r.getDevise()));
    }


    private void chargerRevenue(int idvoy) {
        Revenueservice service = new Revenueservice();
        Revenue r = service.getById(idvoy);
        if (r != null) {
            dateField.setText(r.getDaterevenue());
            modeField.setText(r.getModereception());
            montantField.setText(String.valueOf(r.getRmontant()));
            deviseField.setText(String.valueOf(r.getDevise()));
        } else {
            clearFields(); // إذا لم نجد الإيراد بالـ ID هذا
        }
    }

    private void clearFields() {
        dateField.clear();
        modeField.clear();
        montantField.clear();
        deviseField.clear();
    }

    @FXML
    private void updateRevenue() {
        try {
            int idvoy = Integer.parseInt(idField.getText());
            String date = dateField.getText();
            String mode = modeField.getText();
            int montant = Integer.parseInt(montantField.getText());
            String devise = deviseField.getText();

            if (date.isEmpty() || mode.isEmpty() || devise.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "تحذير", "يرجى ملء جميع الخانات.");
                return;
            }

            Revenue r = new Revenue(idvoy, date, mode, montant, devise);
            Revenueservice service = new Revenueservice();
            service.update(r); // تأكد أن update تستعمل idvoy لتحديث السطر الصحيح

            showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم تحديث الإيراد بنجاح.");
            clearFields();
            idField.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "خطأ", "الرجاء التأكد من الأرقام.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطأ", "حدث خطأ: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
