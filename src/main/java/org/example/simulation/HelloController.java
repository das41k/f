package org.example.simulation;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.example.simulation.Models.Habitat;

public class HelloController {
    @FXML
    private Spinner<Integer> freqMotorcycle; // Частота для мотоциклов
    @FXML
    private Spinner<Integer> freqCar;       // Частота для автомобилей
    @FXML
    private Spinner<Double> pMotorcycle;    // Вероятность для мотоциклов
    @FXML
    private Spinner<Double> pCar;          // Вероятность для автомобилей
    @FXML
    private Label timeLabel;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnEnd;
    @FXML
    private Button btnTimeClose;
    @FXML
    private Button btnTimeWatch;
    @FXML
    private Spinner<Integer> liveMoto;
    @FXML
    private Spinner<Integer> liveAvto;

    private final Alert alertResults = new Alert(Alert.AlertType.INFORMATION);
    private final ButtonType buttonCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

    private Habitat habitat; // Среда обитания
    private boolean isSimulationRunning = false; // Флаг для запуска/остановки симуляции
    private AnimationTimer timer; // Таймер для управления симуляцией
    private long elapsedTime = 0; // Общее накопленное время симуляции в секундах
    private Timeline simulationTimer; // Таймер для обновления времени

    @FXML
    public void initialize() {
        // Устанавливаем значения по умолчанию для Spinner
        freqMotorcycle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 5)); // Диапазон: 1-60, по умолчанию 5
        freqCar.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 10));      // Диапазон: 1-60, по умолчанию 10
        pMotorcycle.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1.0, 0.5, 0.1)); // Диапазон: 0.1-1.0, шаг 0.1
        pCar.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1.0, 0.8, 0.1));       // Диапазон: 0.1-1.0, шаг 0.1
        liveMoto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 6));          // Время жизни мотоциклов (секунды)
        liveAvto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 8));         // Время жизни автомобилей (секунды)
        // Инициализация среды обитания
        habitat = new Habitat(800, 600);
        // Убедимся, что кнопка "Отмена" добавлена только один раз
        alertResults.getButtonTypes().add(buttonCancel);
    }

    @FXML
    public void onKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case B -> startSimulation();
            case E -> stopSimulation();
            case T -> toggleSimulationTimeDisplay();
        }
    }

    @FXML
    public void startSimulation() {
        if (isSimulationRunning) {
            System.out.println("Симуляция уже запущена");
            return;
        }

        btnStart.setDisable(true);
        btnEnd.setDisable(false);

        // Получаем значения из Spinner
        int motorcycleFreq = freqMotorcycle.getValue();
        double motorcycleProb = pMotorcycle.getValue();
        int carFreq = freqCar.getValue();
        double carProb = pCar.getValue();
        // Настраиваем таймер для генерации объектов
        timer = new AnimationTimer() {
            private long lastUpdateMotorcycle = 0;
            private long lastUpdateCar = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdateMotorcycle >= motorcycleFreq * 1_000_000_000L) {
                    if (Math.random() <= motorcycleProb) {
                        habitat.addMotorcycle();
                    }
                    lastUpdateMotorcycle = now;
                }

                if (now - lastUpdateCar >= carFreq * 1_000_000_000L) {
                    if (Math.random() <= carProb) {
                        habitat.addCar();
                    }
                    lastUpdateCar = now;
                }
            }
        };

        timer.start();
        startSimulationTimer();
        isSimulationRunning = true;
        System.out.println("Симуляция запущена");
    }

    @FXML
    public void stopSimulation() {
        if (!isSimulationRunning) {
            System.out.println("Симуляция не запущена");
            return;
        }

        timer.stop();
        simulationTimer.pause();

        // Сохраняем накопленное время перед открытием диалога
        // Показываем окно результатов
        diplayResualts();
        // Если пользователь нажал "Отмена"
        if (alertResults.getResult() == buttonCancel) {
            isSimulationRunning = true;
            timer.start();
            simulationTimer.play();
            return;
        }

        // Завершаем симуляцию
        elapsedTime = 0;
        isSimulationRunning = false;
        simulationTimer.stop();
        btnStart.setDisable(false);
        btnEnd.setDisable(true);
        btnTimeWatch.setDisable(false);
        btnTimeClose.setDisable(true);
        habitat.clear();
    }

    @FXML
    private void displaySimulationTime() {
        if (!isSimulationRunning) {
            timeLabel.setText("Симуляция не запущена");
            return;
        }
        long hours = (elapsedTime / 3600);
        long minutes = (elapsedTime % 3600) / 60;
        long seconds = (elapsedTime % 60);
        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeLabel.setText("Время симуляции: " + timeText);
    }

    private void diplayResualts() {
        String statistic = habitat.getStatistics();
        String timeSimulation = timeLabel.getText();

        alertResults.setHeaderText("Результаты симуляции");
        alertResults.setContentText(statistic + '\n' + timeSimulation);

        alertResults.showAndWait();
    }

    @FXML
    private void startSimulationTimer() {
        simulationTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedTime++;// Увеличиваем время симуляции
            habitat.clearExpiredObjects(liveMoto.getValue(), liveAvto.getValue());
            displaySimulationTime();
        }));
        simulationTimer.setCycleCount(Timeline.INDEFINITE);
        simulationTimer.play();
    }

    @FXML
    private void toggleSimulationTimeDisplay() {
        if (timeLabel.isVisible()) {
            timeLabel.setVisible(false);
            btnTimeWatch.setDisable(false);
            btnTimeClose.setDisable(true);
        } else {
            timeLabel.setVisible(true);
            displaySimulationTime();
            btnTimeWatch.setDisable(true);
            btnTimeClose.setDisable(false);
        }
    }
}
