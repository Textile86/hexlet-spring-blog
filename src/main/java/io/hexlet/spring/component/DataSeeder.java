package io.hexlet.spring.component;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.Tag;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.TagRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile({"development", "production"})
public class DataSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedData() {
        // Проверяем, есть ли уже данные
        if (userRepository.count() > 0) {
            System.out.println("База данных уже содержит данные. Пропускаем seed.");
            return;
        }

        System.out.println("Заполняем базу данных начальными данными...");

        // 1. Создаем пользователя-администратора
        User admin = userRepository.findByEmail("admin@example.com")
                .orElseGet(() -> {
                    User newAdmin = new User();
                    newAdmin.setFirstName("Admin");
                    newAdmin.setLastName("User");
                    newAdmin.setEmail("admin@example.com");
                    newAdmin.setPasswordDigest(passwordEncoder.encode("admin123"));
                    return userRepository.save(newAdmin);
                });

        // 2. Создаем теги
        Tag tagSpringBoot = createTag("Spring Boot");
        Tag tagJPA = createTag("JPA");
        Tag tagREST = createTag("REST API");
        Tag tagSecurity = createTag("Security");
        Tag tagDocker = createTag("Docker");
        Tag tagDatabase = createTag("Database");
        Tag tagTesting = createTag("Testing");

        // 3. Создаем 10 статей про Spring Boot
        createPost(admin, "Введение в Spring Boot",
                "Spring Boot — это фреймворк для создания автономных Spring-приложений production-grade. "
                        + "Он упрощает настройку и развертывание Spring-приложений, предоставляя разумные значения "
                        + "по умолчанию и автоматическую конфигурацию. С Spring Boot вы можете создать готовое к "
                        + " работе приложение всего за несколько минут.",
                Arrays.asList(tagSpringBoot));

        createPost(admin, "Spring Boot Starter Projects",
                "Spring Boot предоставляет набор starter-проектов, которые включают все необходимые "
                        + "зависимости для конкретной функциональности. Например, spring-boot-starter-web содержит "
                        + "все необходимое для создания веб-приложений, включая Spring MVC, Tomcat и Jackson для "
                        + "JSON-сериализации.",
                Arrays.asList(tagSpringBoot, tagREST));

        createPost(admin, "REST API с Spring Boot",
                "Создание REST API с Spring Boot невероятно просто благодаря аннотациям @RestController и "
                        + "@RequestMapping. Spring автоматически преобразует Java-объекты в JSON и обратно. Вы можете "
                        + "легко обрабатывать GET, POST, PUT, DELETE запросы и возвращать различные HTTP-статусы.",
                Arrays.asList(tagSpringBoot, tagREST));

        createPost(admin, "Spring Data JPA",
                "Spring Data JPA упрощает работу с базами данных, предоставляя repository-интерфейсы. "
                        + "Вы просто создаете интерфейс, расширяющий JpaRepository, и Spring автоматически генерирует "
                        + "реализацию с готовыми методами для CRUD-операций, пагинации и сортировки.",
                Arrays.asList(tagSpringBoot, tagJPA, tagDatabase));

        createPost(admin, "Валидация данных в Spring",
                "Spring Boot интегрирован с Bean Validation API (JSR-303). Используя аннотации @NotNull, "
                        + "@Size, @Email и другие, вы можете легко валидировать входящие данные. Аннотация @Valid на "
                        + "параметрах методов контроллера автоматически запускает процесс валидации.",
                Arrays.asList(tagSpringBoot, tagREST));

        createPost(admin, "Spring Security и JWT",
                "Spring Security обеспечивает комплексную защиту приложений. В связке с JWT-токенами вы можете "
                        + "реализовать stateless-аутентификацию, идеальную для REST API. JWT-токен содержит всю "
                        + "необходимую информацию о пользователе и подписывается сервером для защиты от подделки.",
                Arrays.asList(tagSpringBoot, tagSecurity));

        createPost(admin, "Профили в Spring Boot",
                "Профили позволяют иметь разные конфигурации для разных окружений: development, test, "
                        + "production. Вы можете создавать файлы application-{profile}.yml с специфичными настройками"
                        + "и переключаться между ними с помощью переменной окружения SPRING_PROFILES_ACTIVE.",
                Arrays.asList(tagSpringBoot, tagDocker));

        createPost(admin, "Тестирование Spring Boot приложений",
                "Spring Boot предоставляет отличную поддержку тестирования. Аннотация @SpringBootTest "
                        + "позволяет загрузить полный контекст приложения для интеграционных тестов. @WebMvcTest "
                        + "фокусируется только на слое контроллеров, а @DataJpaTest — на слое данных. MockMvc "
                        + "помогает тестировать REST API без запуска реального сервера.",
                Arrays.asList(tagSpringBoot, tagTesting));

        createPost(admin, "Docker и Spring Boot",
                "Docker упрощает развертывание Spring Boot приложений. Создав Dockerfile с многоступенчатой "
                        + "сборкой, вы получаете легковесный образ, содержащий только JRE и ваш JAR-файл. Это "
                        + "обеспечивает консистентность между development и production окружениями.",
                Arrays.asList(tagSpringBoot, tagDocker));

        createPost(admin, "Лучшие практики Spring Boot",
                "Используйте слой сервисов для бизнес-логики. Применяйте DTO для передачи данных между "
                        + "слоями. Настраивайте логирование с помощью Logback или Log4j2. Используйте MapStruct "
                        + "для маппинга объектов. Не забывайте про обработку исключений с @ControllerAdvice."
                        + "Применяйте пагинацию для больших списков данных.",
                Arrays.asList(tagSpringBoot, tagREST, tagJPA));

        System.out.println("База данных успешно заполнена!");
        System.out.println("Создано пользователей: " + userRepository.count());
        System.out.println("Создано тегов: " + tagRepository.count());
        System.out.println("Создано постов: " + postRepository.count());
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    private void createPost(User author, String title, String content, List<Tag> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setPublished(true);
        post.setUser(author);
        post.setTags(tags);
        postRepository.save(post);
    }
}
