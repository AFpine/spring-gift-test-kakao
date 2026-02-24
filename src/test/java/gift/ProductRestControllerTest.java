package gift;

import gift.model.CategoryRepository;
import gift.model.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.response.ValidatableResponse;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProductRestControllerTest {

    private static final String CATEGORY_NAME = "교환권";

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유효한 상품을 등록한다")
    void createValidProductReturnsCreatedProduct() {
        // given
        int categoryId = createCategoryAndGetId(CATEGORY_NAME);

        // when & then
        createProduct("스타벅스 아메리카노", 4500, "https://example.com/coffee.jpg", categoryId)
            .statusCode(200)
            .body("id", notNullValue())
            .body("name", equalTo("스타벅스 아메리카노"))
            .body("price", equalTo(4500))
            .body("imageUrl", equalTo("https://example.com/coffee.jpg"))
            .body("category.id", equalTo(categoryId))
            .body("category.name", equalTo(CATEGORY_NAME));
    }

    @Test
    @DisplayName("상품 목록을 조회한다")
    void retrieveProductsReturnsList() {
        // given
        int categoryId = createCategoryAndGetId(CATEGORY_NAME);
        createProduct("스타벅스 아메리카노", 4500, "https://example.com/americano.jpg", categoryId).statusCode(200);
        createProduct("스타벅스 카페라떼", 5000, "https://example.com/latte.jpg", categoryId).statusCode(200);

        // when & then
        given()
        .when()
            .get("/api/products")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("name", hasItem("스타벅스 아메리카노"))
            .body("name", hasItem("스타벅스 카페라떼"))
            .body("[0].category.id", notNullValue());
    }

    private int createCategoryAndGetId(String name) {
        return given()
            .contentType(ContentType.JSON)
            .body(Map.of("name", name))
        .when()
            .post("/api/categories")
        .then()
            .statusCode(200)
            .extract().path("id");
    }

    private ValidatableResponse createProduct(String name, int price, String imageUrl, int categoryId) {
        return given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "name", name,
                "price", price,
                "imageUrl", imageUrl,
                "categoryId", categoryId
            ))
        .when()
            .post("/api/products")
        .then();
    }
}
