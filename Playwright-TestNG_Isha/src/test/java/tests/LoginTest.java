package tests;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utils.ConfigManager;
import utils.ExcelUtils;
import utils.TestRetryAnalyzer;

import java.util.List;

/**
 * =====================================================================
 * LoginTest
 * =====================================================================
 *
 * Data-driven login and checkout flow validation.
 * Uses Excel for test data and Page Object chaining for readability.
 * =====================================================================
 */
public class LoginTest extends BaseTest {

    // ------------------------------------------------------------------
    // Data Provider
    // ------------------------------------------------------------------
    @DataProvider(name = "loginData")
    public Object[][] loginDataProvider() {

        List<String[]> dataList =
                ExcelUtils.readSheet(
                        "src/test/resources/testdata.xlsx",
                        "Sheet1"
                );

        return dataList.toArray(new Object[0][]);
    }

    // ------------------------------------------------------------------
    // Test
    // ------------------------------------------------------------------
    @Test(
            dataProvider = "loginData",
            retryAnalyzer = TestRetryAnalyzer.class
    )
    public void validLoginAndCheckoutTest(String username, String password) {

        getPage(LoginPage.class)
                .open(ConfigManager.get("base.url"))   // ✅ renamed
                .enterUsername(username)               // ✅ renamed
                .enterPassword(password)               // ✅ renamed
                .clickLogin()                          // ✅ returns ProductsPage
                .addToCart()
                .goToCart()                            // → CartPage
                .validateCartPage()
                .clickCheckout()                       // → CheckoutPage
                .enterFirstName()
                .enterAddress()
                .enterCardNumber()
                .enterEXPIRY()
                .enterCVC()
                .clickPayButton()
                .clickConfirm()                       // → CheckoutOverviewPage
                .validateOverviewDetails()
                .clickLogout();                        // → CheckoutCompletePage
    }
}
