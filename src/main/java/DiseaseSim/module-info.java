module DiseaseSim {
    requires javafx.controls;
    requires javafx.fxml;

    opens DiseaseSim to javafx.fxml;
    exports DiseaseSim;
}
