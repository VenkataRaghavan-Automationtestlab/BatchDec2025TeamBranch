package pages;

import base.BasePage;
import com.microsoft.playwright.Page;
import utils.AllLoctors;

/**
 * Cart page.
 */
public class CartPage extends BasePage {

    public CartPage(Page page, String testName) {
        super(page, testName);
    }

    // ----------------- Validations -----------------

    public CartPage validateCartPage() {
        actions.assertVisible(AllLoctors.CART_TITLE);
        return this;
    }

    // ----------------- Actions -----------------

    public CheckoutPage clickCheckout() {
        actions.click(AllLoctors.CHECKOUT_BTN);
        return new CheckoutPage(page, testName);
    }
}
