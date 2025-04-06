//так, вроде со всем разобралась
//по итогу переделала таскменеджер, переименовала клас в таску, сделал новые тесты и проверила всё варианты по ТЗ
// так же добавила метод очистки всех задач
public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        System.out.println("=== Тестируем TaskManager ===");

        // 1. Создаём обычную задачу
        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        manager.createTask(task1);
        System.out.println("Создана задача: " + task1);

        // 2. Создаём эпик с подзадачами
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", Status.NEW);
        manager.createEpic(epic1);
        System.out.println("\nСоздан эпик: " + epic1);

        // 3. Добавляем подзадачи в эпик
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic1.getId());

        manager.createSubtask(subTask1);
        manager.createSubtask(subTask2);

        System.out.println("\nДобавлены подзадачи:");
        System.out.println("- " + subTask1);
        System.out.println("- " + subTask2);

        // 4. Проверяем статус эпика (должен быть IN_PROGRESS)
        System.out.println("\nСтатус эпика после добавления подзадач: " + epic1.getStatus());

        // 5. Меняем статус подзадачи на DONE и проверяем эпик
        subTask1.setStatus(Status.DONE);
        manager.updateSubtask(subTask1);
        System.out.println("\nСтатус эпика после завершения одной подзадачи: " + epic1.getStatus());

        // 6. Завершаем вторую подзадачу → эпик должен стать DONE
        subTask2.setStatus(Status.DONE);
        manager.updateSubtask(subTask2);
        System.out.println("Статус эпика после завершения всех подзадач: " + epic1.getStatus());

        // 7. Удаляем подзадачу и проверяем эпик
        manager.deleteSubtaskById(subTask1.getId());
        System.out.println("\nПосле удаления подзадачи 1, остались подзадачи: " +
                manager.getSubtasksForEpic(epic1.getId()));

        // 8. Добавдяем подзадачу и смотрим стаус эпика
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", Status.NEW, epic1.getId());
        manager.createSubtask(subTask3);
        System.out.println("\nДобавлены подзадачи:");
        System.out.println("- " + subTask3);
        System.out.println("Статус эпика после завершения всех подзадач: " + epic1.getStatus());

        // 9. Удаляем эпик (должны удалиться и его подзадачи)
        manager.deleteEpicById(epic1.getId());
        System.out.println("\nПосле удаления эпика, его подзадачи: " + manager.getSubtasksForEpic(epic1.getId()));

        // 10 Еще раз создаём эпики с подзадачами
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3", Status.NEW);
        manager.createEpic(epic3);
        SubTask subTask4 = new SubTask("Подзадача 4", "Описание подзадачи 4", Status.IN_PROGRESS, epic3.getId());
        Epic epic4 = new Epic("Эпик 4", "Описание эпика 4", Status.NEW);
        manager.createEpic(epic4);
        SubTask subTask5 = new SubTask("Подзадача 5", "Описание подзадачи 5", Status.IN_PROGRESS, epic3.getId());

        // 11. Выводим все задачи
        System.out.println("\n=== Итоговый список задач ===");
        System.out.println("Обычные задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());


        //12. Очищаем список задач
        manager.clearAll();
        System.out.println("\n=== Итоговый список задач ===");
        System.out.println("Обычные задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());

    }
}