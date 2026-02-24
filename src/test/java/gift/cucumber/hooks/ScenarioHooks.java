package gift.cucumber.hooks;

import gift.model.CategoryRepository;
import gift.model.MemberRepository;
import gift.model.OptionRepository;
import gift.model.ProductRepository;
import gift.model.WishRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

public class ScenarioHooks {

    @LocalServerPort
    private int port;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @After
    public void cleanUp() {
        wishRepository.deleteAllInBatch();
        optionRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }
}
