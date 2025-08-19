@echo off
set JLINK_VM_OPTIONS=--module-path "C:\Program Files\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml
set DIR=%~dp0
"%DIR%\java" %JLINK_VM_OPTIONS% -m DiseaseSim/DiseaseSim.Main %*
