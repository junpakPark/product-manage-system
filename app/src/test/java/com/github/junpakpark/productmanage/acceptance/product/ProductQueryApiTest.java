package com.github.junpakpark.productmanage.acceptance.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.acceptance.ApiTest;
import io.restassured.RestAssured;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

class ProductQueryApiTest extends ApiTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        persistTestData();
    }

    @Test
    @DisplayName("상품 목록 조회")
    void getProducts() {
        // Arrange
        final Pageable pageable = PageRequest.of(0, 10);

        // Action
        final var response = RestAssured.given().log().all()
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .when()
                .get("/api/products")
                .then().log().all()
                .extract();

        // Assert
        final List<String> productNames = response.jsonPath().getList("content.name", String.class);
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getLong("totalElements")).isEqualTo(2);
            assertThat(productNames).containsExactly("상품2", "상품1");
        });
    }

    @Test
    @DisplayName("상품 단건 조회")
    void getProductDetail() {
        // Arrange
        final Long productId = 1L;

        // Action
        final var response = RestAssured.given().log().all()
                .when()
                .get("/api/products/{productId}", productId)
                .then()
                .log().all()
                .extract();

        // Assert
        final List<String> optionNames = response.jsonPath().getList("options.name", String.class);
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.jsonPath().getString("name")).isEqualTo("상품1");
            assertThat(optionNames).containsExactly("색상", "각인문구");
        });
    }

    @Test
    @DisplayName("상품 옵션 조회")
    void getProductOptions() {
        // Arrange
        final Long productId = 1L;

        // Action
        final var response = RestAssured.given().log().all()
                .when()
                .get("/api/products/{productId}/options", productId)
                .then()
                .log().all()
                .extract();

        // Assert
        final List<String> optionNames = response.jsonPath().getList("name", String.class);
        final List<String> colorChoices = response.jsonPath().getList("find { it.name == '색상' }.choices", String.class);
        final List<String> engravingChoices = response.jsonPath()
                .getList("find { it.name == '각인문구' }.choices", String.class);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(optionNames).containsExactly("색상", "각인문구");
            assertThat(colorChoices).containsExactly("빨강", "파랑");
            assertThat(engravingChoices).isEmpty();
        });
    }

    private void persistTestData() {
        jdbcTemplate.execute("""
                    INSERT INTO product (id, name, description, price, shipping_fee, member_id, deleted, created_at)
                    VALUES (1, '상품1', '설명1', 10000, 2500, 1, false, now());
                """);
        jdbcTemplate.execute("""
                    INSERT INTO product (id, name, description, price, shipping_fee, member_id, deleted, created_at)
                    VALUES (2, '상품2', '설명2', 15000, 3000, 2, false, now());
                """);
        jdbcTemplate.execute("""
                    INSERT INTO product_option (id, product_id, name, additional_price, option_type, deleted, created_at)
                    VALUES (101, 1, '색상', 0, 'SELECT', false, now());
                """);
        jdbcTemplate.execute("""
                    INSERT INTO product_option (id, product_id, name, additional_price, option_type, deleted, created_at)
                    VALUES (102, 1, '각인문구', 2000, 'INPUT', false, now());
                """);
        jdbcTemplate.execute("""
                    INSERT INTO option_choice (select_option_id, choice_value)
                    VALUES (101, '빨강'), (101, '파랑');
                """);
    }

}
