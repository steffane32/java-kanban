// для самопроверки
public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Создаем задачу
        Task task = new Task("Test", "Description", Status.NEW);
        manager.createTask(task);

        // Проверяем до просмотра
        System.out.println("История до просмотра: " + manager.getHistory().size()); // Должно быть 0

        // Просматриваем задачу
        manager.getTaskById(task.getId());
        System.out.println("История после просмотра: " + manager.getHistory().size()); // Должно быть 1

        // Просматриваем ещё раз
        manager.getTaskById(task.getId());
        System.out.println("История после повторного просмотра: " + manager.getHistory().size()); // Должно остаться 1

        // Удаляем задачу
        manager.deleteTaskById(task.getId());
        System.out.println("История после удаления: " + manager.getHistory().size()); // Должно быть 0


    }
}