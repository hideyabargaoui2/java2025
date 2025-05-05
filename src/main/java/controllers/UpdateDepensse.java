package controllers;

import Modules.Depensse;
import Service.Depensseservice;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class UpdateDepensse {

    @FXML
    private TextField idField, categorieField, dateField, modeField, montantField, descriptionField;

    // Méthode appelée automatiquement par JavaFX après le chargement de la scène
    @FXML
    public void initialize() {
        // Rien ici car l'ID sera injecté via setDepensseData
    }

    // Cette méthode est appelée depuis l’extérieur pour remplir les champs avant l’affichage
    public void setDepensseData(Depensse depense) {
        idField.setText(String.valueOf(depense.getIdvoy())); // Champ caché mais utile pour update
        categorieField.setText(depense.getCategories());
        dateField.setText(depense.getDatepay());
        modeField.setText(depense.getModdepay());
        montantField.setText(String.valueOf(depense.getMontant()));
        descriptionField.setText(depense.getDescripiton());
    }

    // Action appelée lors du clic sur le bouton "Mettre à jour"
    @FXML
    private void updateDepensse() {
        try {
            int idvoy = Integer.parseInt(idField.getText());
            String categorie = categorieField.getText();
            String date = dateField.getText();
            String mode = modeField.getText();
            int montant = Integer.parseInt(montantField.getText());
            String description = descriptionField.getText();

            if (categorie.isEmpty() || date.isEmpty() || mode.isEmpty() || description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "تحذير", "يرجى ملء جميع الخانات.");
                return;
            }

            Depensse d = new Depensse(idvoy, mode, date, categorie, montant, description);
            Depensseservice service = new Depensseservice();
            service.update(d);

            showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم تحديث المصروف بنجاح.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "خطأ", "تأكد من أن المبلغ ورقم المعرف أرقام صحيحة.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "خطأ", "حدث خطأ أثناء التحديث: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
