//import java.time.Duration;
//import java.time.LocalDateTime;
//самопроверка
public class Main {
    public static void main(String[] args) {
//        TaskManager manager = Managers.getDefault();
//        HistoryManager historyManager = Managers.getDefaultHistory();
//
//        // Создаем эпики с указанием ID (0 для автоинкремента)
//        Epic epic1 = new Epic(0, "Подготовка к отпуску", "Организация поездки в Италию", Status.NEW);
//        Epic createdEpic1 = manager.createEpic(epic1);
//
//        Epic epic2 = new Epic(0, "Ремонт в квартире", "Полный ремонт кухни и ванной", Status.NEW);
//        Epic createdEpic2 = manager.createEpic(epic2);
//
//        // Создаем обычные задачи
//        LocalDateTime now = LocalDateTime.now();
//
//        Task task1 = new Task(0, "Записаться к врачу", "Терапевт и стоматолог", Status.NEW,
//                Duration.ofHours(1), now.plusDays(1));
//        Task createdTask1 = manager.createTask(task1);
//
//        Task task2 = new Task(0, "Купить продукты", "Молоко, хлеб, фрукты", Status.NEW,
//                Duration.ofMinutes(30), now.plusHours(2));
//        Task createdTask2 = manager.createTask(task2);
//
//        // Создаем подзадачи с указанием ID эпика
//        SubTask subTask1 = new SubTask(0, "Купить билеты", "Билеты на самолет в Рим", Status.NEW,
//                createdEpic1.getId(), Duration.ofHours(2), now.plusDays(3));
//        manager.createSubtask(subTask1);
//
//        SubTask subTask2 = new SubTask(0, "Забронировать отель", "Отель в центре Рима на 7 ночей", Status.NEW,
//                createdEpic1.getId(), Duration.ofHours(1), now.plusDays(4));
//        manager.createSubtask(subTask2);
//
//        // Выводим информацию
//        System.out.println("Все задачи:");
//        manager.getAllTasks().forEach(System.out::println);
//
//        System.out.println("\nВсе эпики:");
//        manager.getAllEpics().forEach(System.out::println);
//
//        System.out.println("\nВсе подзадачи:");
//        manager.getAllSubtasks().forEach(System.out::println);
//
//        System.out.println("\nИстория просмотров:");
//        manager.getTaskById(createdTask1.getId());
//        manager.getEpicById(createdEpic1.getId());
//        manager.getHistory().forEach(System.out::println);
    }
}