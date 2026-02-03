package pages;

import com.microsoft.playwright.Page;
import base.BasePage;
import utils.AllLoctors;

public class LoginPage extends BasePage {

    public LoginPage(Page page, String testName) {
        super(page, testName);
    }

    // Actions
    public LoginPage open(String url) {
        actions.navigate(url);
		return this;
    }

    public LoginPage enterUsername(String username) {
        actions.fill(AllLoctors.USERNAME_INPUT, username);
		return this;
    }
    
    public LoginPage enterPassword(String password) {
        actions.fill(AllLoctors.PASSWORD_INPUT, password);
		return this;
    }
    
    public ProductsPage clickLogin() {
        actions.click(AllLoctors.LOGIN_BUTTON);
        return new ProductsPage(page, testName);
    }

    public String getErrorMessage() {
        return actions.getText(AllLoctors.ERROR_MESSAGE);
    }
}
