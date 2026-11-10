import java.lang.annotation.*;
import java.lang.reflect.Method;

public class Main {

    @Target(ElementType.METHOD) // Можно ставить только на методы
    @Retention(RetentionPolicy.RUNTIME) // Аннотация видна во время работы программы (нужно для рефлексии)
    public @interface AutoCallable {
        int count() default 1; // Параметр с значением по умолчанию 1
    }

    public interface Call {
        static void execute(){}
    }

    public class StdOut implements Call {
        @AutoCallable
        public static void execute() {
            System.out.println("Сообщение в стандартный поток вывода (StdOut).");
        }
    }

    public class StdError implements Call {
        @AutoCallable
        public static void execute() {
            System.err.println("Сообщение в поток ошибок (StdError).");
        }
    }


    public class AutoCallableRunner {

        // Метод, который запускает все помеченные методы в указанном классе
        public static void run(Class<?> classes) {
            // Получаем все методы класса
            Method[] methods = classes.getDeclaredMethods();

            for (Method method : methods) {
                // Проверяем, есть ли у метода наша аннотация
                if (method.isAnnotationPresent(AutoCallable.class)) {
                    AutoCallable annotation = method.getAnnotation(AutoCallable.class);
                    int count = annotation.count(); // Берем значение count из аннотации

                    // Делаем метод доступным (на случай, если он private)
                    method.setAccessible(true);

                    // Вызываем метод нужное количество раз
                    try {
                        for (int i = 0; i < count; i++) {
                            method.invoke(null); // null, так как метод должен быть static
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        AutoCallableRunner.run(Main.class);
        AutoCallableRunner.run(StdOut.class);
        AutoCallableRunner.run(StdError.class);
    }
}