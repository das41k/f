package org.example.simulation;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import org.example.simulation.Models.Habitat;

import java.util.Date; // Используем java.util.Date для времени

public class HelloController {
    @FXML
    private Spinner<Integer> freqMotorcycle; // Частота для мотоциклов
    @FXML
    private Spinner<Integer> freqCar;       // Частота для автомобилей
    @FXML
    private Spinner<Double> pMotorcycle;    // Вероятность для мотоциклов
    @FXML
    private Spinner<Double> pCar;           // Вероятность для автомобилей
    @FXML
    private Label timeLabel;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnEnd;
    private Habitat habitat; // Среда обитания
    private boolean isSimulationRunning = false; // Флаг для запуска/остановки симуляции
    private AnimationTimer timer; // Таймер для управления симуляцией
    private long simulationStartTime; // Используем System.currentTimeMillis() для времени старта
    private Timeline simulationTimer; // Таймер для обновления времени

    @FXML
    public void initialize() {
        // Устанавливаем значения по умолчанию для Spinner
        freqMotorcycle.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 5)); // Диапазон: 1-60, по умолчанию 5
        freqCar.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 60, 10));      // Диапазон: 1-60, по умолчанию 10
        pMotorcycle.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1.0, 0.5, 0.1)); // Диапазон: 0.1-1.0, шаг 0.1
        pCar.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1.0, 0.8, 0.1));       // Диапазон: 0.1-1.0, шаг 0.1
        // Инициализация среды обитания
        habitat = new Habitat(800, 600);
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

        // Инициализируем время начала симуляции
        simulationStartTime = System.currentTimeMillis(); // Используем System.currentTimeMillis()
        isSimulationRunning = true;

        // Настроим таймер для генерации объектов
        timer = new AnimationTimer() {
            private long lastUpdateMotorcycle = 0;
            private long lastUpdateCar = 0;

            @Override
            public void handle(long now) {
                // Проверяем интервал для мотоциклов
                if (now - lastUpdateMotorcycle >= motorcycleFreq * 1_000_000_000L) {
                    if (Math.random() <= motorcycleProb) {
                        habitat.addMotorcycle();
                    }
                    lastUpdateMotorcycle = now;
                }

                // Проверяем интервал для автомобилей
                if (now - lastUpdateCar >= carFreq * 1_000_000_000L) {
                    if (Math.random() <= carProb) {
                        habitat.addCar();
                    }
                    lastUpdateCar = now;
                }
            }
        };

        timer.start();
        startSimulationTimer(); // Запуск таймера для отображения времени
        System.out.println("Симуляция запущена");
    }

    @FXML
    public void stopSimulation() {
        if (!isSimulationRunning) {
            System.out.println("Симуляция не запущена");
            return;
        }
        btnStart.setDisable(false);
        btnEnd.setDisable(true);
        timer.stop();
        isSimulationRunning = false;
        simulationTimer.stop(); // Останавливаем таймер отображения времени
        System.out.println("Симуляция остановлена");

        // Выводим статистику
        diplayResualts();
        habitat.clear();
    }

    private void displaySimulationTime() {
        if (!isSimulationRunning) {
            timeLabel.setText("Симуляция не запущена"); // Сообщение в интерфейсе
            return;
        }

        long currentTime = System.currentTimeMillis(); // Текущее время
        long elapsedTime = currentTime - simulationStartTime; // Разница между текущим временем и временем старта симуляции

        long hours = (elapsedTime / 1000) / 3600; // Переводим в часы
        long minutes = (elapsedTime / 1000) % 3600 / 60; // Переводим в минуты
        long seconds = (elapsedTime / 1000) % 60; // Переводим в секунды

        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timeLabel.setText("Время симуляции: " + timeText); // Обновляем текст в Label
    }
    private void diplayResualts() {
        String statistic = habitat.getStatistics();
        String timeSimulation = timeLabel.getText();
        Alert alertResults = new Alert(Alert.AlertType.INFORMATION);
        alertResults.setHeaderText("Результаты симуляции");
        alertResults.setContentText(statistic + '\n' + timeSimulation);
        alertResults.showAndWait();
    }
    private void startSimulationTimer() {
        simulationTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> displaySimulationTime()));
        simulationTimer.setCycleCount(Animation.INDEFINITE); // Бесконечный цикл
        simulationTimer.play(); // Запускаем таймер
    }

    private void toggleSimulationTimeDisplay() {
        if (timeLabel.isVisible()) {
            timeLabel.setVisible(false);
        } else {
            timeLabel.setVisible(true);
            displaySimulationTime();
        }
    }
}
