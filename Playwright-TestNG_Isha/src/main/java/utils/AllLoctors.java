package utils;

import com.microsoft.playwright.Page;

import base.BasePage;

public class AllLoctors extends BasePage {

	public AllLoctors(Page page, String testName) {
		super(page, testName);
	}

	// Login Page Locators
	public static final String USERNAME_INPUT = "#loginUsername";
	public static final String PASSWORD_INPUT = "#loginPassword";
	public static final String LOGIN_BUTTON = "[type='submit']";
	public static final String ERROR_MESSAGE = "#errorMsg";
	public static final String LOG_OUT = "#logoutBtn";
	
	// Locators for Products Page
	public static final String PRODUCTS_TITLE = "//*[@class='inventory-top']/h2";
	public static final String FIRST_PRODUCT_ADD_BTN = "[data-id='p3']";
	public static final String CART_BUTTON = "#cartBtn";

	// Locators for Cart Page
	public static final String CART_TITLE = "//*[@class='container']/h2";
	public static final String CHECKOUT_BTN = "#checkoutBtn";

	// Locators for Checkout Information Page
	public static final String FIRST_NAME = "#fullName";
	public static final String ADDRESS = "#address";
	public static final String CARD_NUMBER = "#cardNumber";
	public static final String EXPIRY = "#expiry";
	public static final String CVC = "#cvc";
	public static final String PayBttn = ".btn.primary";
	public static final String ConfirmBttn = "[onclick='confirmPayment()']";
	
	//Locators for Checkout Overview Page
	public static final String ORDER_DESCRIPTION = "#orderDetails";
    public static final String SHIPPING_INFO     = "#orderDetails";
    public static final String FINISH_BUTTON     = "#finishBtn";
    
    //Locators for Checkout Complete Page
    public static final String THANK_YOU_MESSAGE = "#thankYouMsg";
}
