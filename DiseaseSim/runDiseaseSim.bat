@echo off
set JAVAFX_LIB=javafx-sdk-24.0.2\lib
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml,javafx.swing,javafx.base -jar DiseaseSim-1.0-SNAPSHOT-jar-with-dependencies.jar
pause