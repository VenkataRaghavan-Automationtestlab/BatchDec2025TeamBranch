package pages;

import base.BasePage;
import com.microsoft.playwright.Page;
import utils.AllLoctors;

/**
 * Checkout information page.
 */
public class CheckoutPage extends BasePage {

    public CheckoutPage(Page page, String testName) {
        super(page, testName);
    }

    // ----------------- Actions -----------------

    public CheckoutPage enterFirstName() {
        actions.fill(AllLoctors.FIRST_NAME, "Ramesh");
        return this;
    }

    public CheckoutPage enterAddress() {
        actions.fill(AllLoctors.ADDRESS, "3rd Cross Street, Chennai");
        return this;
    }

    public CheckoutPage enterCardNumber() {
        actions.fill(AllLoctors.CARD_NUMBER, "1234 5647 4856 4656");
        return this;
    }
    
    public CheckoutPage enterEXPIRY() {
        actions.fill(AllLoctors.EXPIRY, "12/12");
        return this;
    }
    
    public CheckoutPage enterCVC() {
        actions.fill(AllLoctors.CVC, "123");
        return this;
    }

    public CheckoutPage clickPayButton() {
        actions.click(AllLoctors.PayBttn);
        return this;
    }
    
    public CheckoutOverviewPage clickConfirm() {
        actions.click(AllLoctors.ConfirmBttn);
        return new CheckoutOverviewPage(page, testName);
    }
}
