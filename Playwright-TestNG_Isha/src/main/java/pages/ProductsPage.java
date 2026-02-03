package pages;

import base.BasePage;
import com.microsoft.playwright.Page;
import utils.AllLoctors;

/**
 * Products page after successful login.
 */
public class ProductsPage extends BasePage {

    public ProductsPage(Page page, String testName) {
        super(page, testName);
    }

    // ----------------- Validations -----------------

//    public ProductsPage validateProductsPage() {
//        actions.assertVisible(AllLoctors.PRODUCTS_TITLE);
//        return this;
//    }

    // ----------------- Actions -----------------

    public ProductsPage addToCart() {
        actions.click(AllLoctors.FIRST_PRODUCT_ADD_BTN);
        return this;
    }

    public CartPage goToCart() {
        actions.click(AllLoctors.CART_BUTTON);
        return new CartPage(page, testName);
    }
}
