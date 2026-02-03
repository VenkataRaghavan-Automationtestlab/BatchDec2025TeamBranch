package pages;

import base.BasePage;
import com.microsoft.playwright.Page;
import utils.AllLoctors;

/**
 * Checkout overview page.
 */
public class CheckoutOverviewPage extends BasePage {

    public CheckoutOverviewPage(Page page, String testName) {
        super(page, testName);
    }

    // ----------------- Validations -----------------

    public CheckoutOverviewPage validateOverviewDetails() {
        actions.assertVisible(AllLoctors.ORDER_DESCRIPTION);        
       System.out.println(actions.getText(AllLoctors.ORDER_DESCRIPTION));
        return this;
    }

	// ----------------- Actions -----------------

    public LoginPage clickLogout() {
        actions.click(AllLoctors.LOG_OUT);
        return new LoginPage(page, testName);
    }
}
