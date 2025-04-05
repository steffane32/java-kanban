public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // ТЕСТИРОВАНИЕ_1 - Создаём основные задачи
        Tasks task1 = new Tasks(1, "Задача 1", "Описание задачи 1", Status.NEW);
        Tasks task2 = new Tasks(2, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS);

        // ТЕСТИРОВАНИЕ_2 Создаём первый эпик с двумя подзадачами
        Epic epic1 = new Epic(3, "Эпик 1", "Описание эпика 1", Status.NEW);
        SubTask subTask1 = new SubTask(4, "Подзадача 1", "Описание подзадачи 1", Status.NEW);
        SubTask subTask2 = new SubTask(5, "Подзадача 2", "Описание подзадачи 2", Status.DONE);

        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);

        // ТЕСТИРОВАНИЕ_3 Создаём второй эпик с одной подзадачей
        Epic epic2 = new Epic(6, "Эпик 2", "Описание эпика 2", Status.NEW);
        SubTask subTask3 = new SubTask(7, "Подзадача 3", "Описание подзадачи 3", Status.IN_PROGRESS);

        epic2.addSubTask(subTask3);

        // ТЕСТИРОВАНИЕ_4 Добавляем задачи и эпики в менеджер
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(epic1);
        taskManager.createTask(epic2);

        // ТЕСТИРОВАНИЕ_5 Отображаем все задачи и эпики
        System.out.println("Все задачи:");
        for (Tasks task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // ТЕСТИРОВАНИЕ_6 Изменяем статусы
        task1.setStatus(Status.DONE);
        subTask1.setStatus(Status.DONE);
        epic1.setStatus(Status.IN_PROGRESS);

         // ТЕСТИРОВАНИЕ_7 Обновляем статус
        taskManager.updateEpicStatus(epic1.getId());
        taskManager.updateEpicStatus(epic2.getId());

        // ТЕСТИРОВАНИЕ_8 Отображаем все задачи и эпики после изменений
        System.out.println("\nПосле изменения статусов:");
        for (Tasks task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // ТЕСТИРОВАНИЕ_9 Удаляем одну из задач и один из эпиков
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteTaskById(epic2.getId());

        // ТЕСТИРОВАНИЕ_10 Отображаем все задачи и эпики после удаления
        System.out.println("\nПосле удаления задачи и эпика:");
        for (Tasks task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
    }
}