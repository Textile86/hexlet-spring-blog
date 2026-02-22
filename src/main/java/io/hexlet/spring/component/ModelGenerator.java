package io.hexlet.spring.component;

import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!production && !test && !development")
public class ModelGenerator {

    private final Faker faker;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ModelGenerator(Faker faker, UserRepository userRepository, PostRepository postRepository) {
        this.faker = faker;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @PostConstruct
    public void generateData() {
        System.out.println("Generating test data...");

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            user.setEmail(faker.internet().emailAddress());
            user.setBirthday(faker.date().birthday().toLocalDateTime().toLocalDate());
            userRepository.save(user);

            for (int j = 0; j < 2; j++) {
                Post post = new Post();
                post.setTitle(faker.lorem().sentence(5));
                post.setContent(faker.lorem().paragraph(3));
                post.setPublished(faker.bool().bool());
                post.setUser(user);
                postRepository.save(post);
            }
        }

        System.out.println("Test data generated successfully!");
        System.out.println("Users: " + userRepository.count());
        System.out.println("Posts: " + postRepository.count());
    }
}