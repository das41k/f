package org.example.simulation;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.SpinnerValueFactory;
import org.example.simulation.Models.Habitat;

import java.time.LocalTime;
import java.time.Duration;

public class HelloController {
    @FXML
    private Spinner<Integer> freqMotorcycle; // Частота для мотоциклов
    @FXML
    private Spinner<Integer> freqCar;       // Частота для автомобилей
    @FXML
    private Spinner<Double> pMotorcycle;    // Вероятность для мотоциклов
    @FXML
    private Spinner<Double> pCar;          // Вероятность для автомобилей

    private Habitat habitat; // Среда обитания
    private boolean isSimulationRunning = false; // Флаг для запуска/остановки симуляции
    private AnimationTimer timer; // Таймер для управления симуляцией
    private LocalTime simulationStartTime; // Время начала симуляции

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
            case T -> displaySimulationTime();
        }
    }

    private void startSimulation() {
        if (isSimulationRunning) {
            System.out.println("Симуляция уже запущена");
            return;
        }

        // Получаем значения из Spinner
        int motorcycleFreq = freqMotorcycle.getValue();
        double motorcycleProb = pMotorcycle.getValue();
        int carFreq = freqCar.getValue();
        double carProb = pCar.getValue();

        // Инициализируем время начала симуляции
        simulationStartTime = LocalTime.now();
        isSimulationRunning = true;

        // Настраиваем таймер для генерации объектов
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
        System.out.println("Симуляция запущена");
    }

    private void stopSimulation() {
        if (!isSimulationRunning) {
            System.out.println("Симуляция не запущена");
            return;
        }

        timer.stop();
        isSimulationRunning = false;
        System.out.println("Симуляция остановлена");

        // Выводим статистику
        System.out.println("Итоги симуляции:");
        System.out.println("Мотоциклы: " + habitat.getMotorcycleCount());
        System.out.println("Автомобили: " + habitat.getCarCount());
    }

    private void displaySimulationTime() {
        if (!isSimulationRunning) {
            System.out.println("Симуляция не запущена");
            return;
        }

        Duration duration = Duration.between(simulationStartTime, LocalTime.now());
        long seconds = duration.getSeconds();
        System.out.println("Время симуляции: " + seconds + " секунд");
    }
}
