package org.example.simulation.Models;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Habitat {
    // Размеры рабочей области (можно использовать для визуализации)
    private int width;
    private int height;

    // Коллекции для хранения объектов
    private ArrayList<Transport> objects;          // Список всех транспортных средств
    private HashSet<String> uniqueIds;             // Набор уникальных идентификаторов
    private HashMap<String, LocalTime> birthTimeMap; // Карта ID -> Время рождения

    // Счетчики для статистики
    private int motorcycleCount;
    private int carCount;

    // Конструктор
    public Habitat(int width, int height) {
        this.width = width;
        this.height = height;
        this.objects = new ArrayList<>();
        this.uniqueIds = new HashSet<>();
        this.birthTimeMap = new HashMap<>();
        this.motorcycleCount = 0;
        this.carCount = 0;
    }

    /**
     * Добавляет мотоцикл в среду обитания.
     */
    public void addMotorcycle() {
        String id = generateUniqueId();
        LocalTime birthTime = LocalTime.now();
        Motorcycle motorcycle = new Motorcycle(id, birthTime);
        objects.add(motorcycle);
        uniqueIds.add(id);
        birthTimeMap.put(id, birthTime);
        motorcycleCount++;
        System.out.println("Мотоцикл добавлен с ID: " + id);
    }

    /**
     * Добавляет автомобиль в среду обитания.
     */
    public void addCar() {
        String id = generateUniqueId();
        LocalTime birthTime = LocalTime.now();
        Car car = new Car(id, birthTime);
        objects.add(car);
        uniqueIds.add(id);
        birthTimeMap.put(id, birthTime);
        carCount++;
        System.out.println("Автомобиль добавлен с ID: " + id);
    }
    /**
     * Генерирует уникальный идентификатор.
     *
     * @return Уникальный ID в виде строки.
     */
    private String generateUniqueId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (uniqueIds.contains(id));
        return id;
    }

    /**
     * Возвращает количество сгенерированных мотоциклов.
     *
     * @return Количество мотоциклов.
     */
    public int getMotorcycleCount() {
        return motorcycleCount;
    }

    /**
     * Возвращает количество сгенерированных автомобилей.
     *
     * @return Количество автомобилей.
     */
    public int getCarCount() {
        return carCount;
    }

    /**
     * Возвращает список всех транспортных средств.
     *
     * @return Список объектов Transport.
     */
    public ArrayList<Transport> getObjects() {
        return objects;
    }

    /**
     * Очищает все данные симуляции.
     */
    public void clear() {
        objects.clear();
        uniqueIds.clear();
        birthTimeMap.clear();
        motorcycleCount = 0;
        carCount = 0;
        System.out.println("Все данные симуляции очищены.");
    }
    public void clearBirthTime() {
        for (String key : birthTimeMap.keySet()) {

        }
    }
    /**
     * Возвращает статистическую информацию о симуляции.
     *
     * @return Строка со статистикой.
     */
    public String getStatistics() {
        return String.format("Статистика симуляции:\nМотоциклы: %d\nАвтомобили: %d", motorcycleCount, carCount);
    }

    /**
     * Возвращает размер рабочей области.
     *
     * @return Массив из двух элементов: [ширина, высота].
     */
    public int[] getSize() {
        return new int[]{width, height};
    }

    /**
     * Метод для отображения всех объектов (может быть использован для консольного вывода или интеграции с UI).
     */
    public void displayObjects() {
        System.out.println("Текущее количество объектов: " + objects.size());
    }
    public void clearExpiredObjects(int maxLifetimeMoto, int maxLifetimeCar) {
        Iterator<String> iterator = birthTimeMap.keySet().iterator();
        LocalTime now = LocalTime.now();

        while (iterator.hasNext()) {
            String id = iterator.next();
            LocalTime birthTime = birthTimeMap.get(id);
            long lifetime = Duration.between(birthTime, now).getSeconds();

            // Определяем, к какому типу относится объект
            Transport transport = objects.stream()
                    .filter(obj -> obj.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (transport instanceof Motorcycle && lifetime > maxLifetimeMoto) {
                iterator.remove();
                uniqueIds.remove(id);
                objects.remove(transport);
                motorcycleCount--;
                System.out.println("Удалён мотоцикл с ID: " + id);
            } else if (transport instanceof Car && lifetime > maxLifetimeCar) {
                iterator.remove();
                uniqueIds.remove(id);
                objects.remove(transport);
                carCount--;
                System.out.println("Удалён автомобиль с ID: " + id);
            }
        }
    }
}
