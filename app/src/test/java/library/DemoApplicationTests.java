package library;

import fr.uga.l3miage.library.LibraryApplication;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.data.domain.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = LibraryApplication.class,
        properties = {
        "spring.datasource.embedded-database-connection=h2",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "spring.jpa.show-sql=true"
        }
)
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
class DemoApplicationTests {

    @SuppressWarnings("java:S2699")
    @Test
    void contextLoads() {

    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homeResponse() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void apiV1Response() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("/api/v1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void authorsResponse() {
        var author = new AuthorDTO(null, "Me");
        var response = this.restTemplate.postForEntity("/api/v1/authors", author, AuthorDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        var list = this.restTemplate.getForObject("/api/v1/authors", List.class);
        assertThat(list)
                .hasSize(1)
                .extracting("fullName")
                .containsExactly("Me");
    }
}
