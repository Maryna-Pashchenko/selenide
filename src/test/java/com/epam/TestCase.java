package com.epam;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;

public class TestCase {
    private Logger LOG;

    @BeforeMethod
    public void setupClass() {
        System.setProperty("webdriver.chrome.driver", "resource/chromedriver.exe");
        System.setProperty("selenide.browser", "Chrome");
        LOG = Logger.getLogger(TestCase.class);
        open("http://iherb.com");
    }

    @Test
    public void verifySearchBox() {
        LOG.info("Verify that quick links appear after click on input field");
        $(By.id("txtSearch")).click();
        $("#quick-links-container").shouldBe(Condition.visible);
        LOG.info("Verify that keywords and suggested products appear after entering one letter");
        $(By.id("txtSearch")).setValue("m");
        $("#keywords-wrapper").shouldBe(Condition.visible);
        $("#suggested-products-wrapper").shouldBe(Condition.visible);
        LOG.info("Verify that keywords list contain 5 elements");
        $$("#keywords-wrapper li").shouldHave(CollectionCondition.size(5));
        LOG.info("Verify the background on hovered element");
        String back = $(By.cssSelector("#keywords-wrapper .results-container>li")).hover().getCssValue("background-color");
        Assert.assertEquals("rgba(249, 249, 249, 1)", back);
        LOG.info("Verify click on search button");
        $("#searchBtn").click();
        Assert.assertTrue(url().contains("/search?kw"));
        LOG.info("PASSED");
//        takeScreenShot("my-test-case");
    }

    @Test
    public void verifyFilterColumn() {
        LOG.info("Verify clicking on sub category");
        $("#li_bath").hover();
        $(By.xpath(".//*[@id='dd_bath']")).$(byText("Gift Sets")).shouldBe(Condition.visible).click();
        Assert.assertTrue(url().contains("/gift-bags-travel-kits"));
        LOG.info("Filter products by price");
        $("#PriceFilters").click();
        $("#PriceFilters .filter-list").shouldBe(Condition.visible);
        $("#PriceFilters #Filter₴0-₴150").click();
        LOG.info("Verify the result list");
        String count = $(By.xpath("//*[@id='PriceFilters']//label[@for='Filter₴0-₴150']/..")).getAttribute("data-count");
        $$(".ga-product").shouldHave(CollectionCondition.size(Integer.parseInt(count)));
        ArrayList<String> list = (ArrayList<String>) $$(".ga-product .price>bdi").texts();
        if(list.stream().map(el->Double.parseDouble(el.replaceAll("[₴]", ""))).filter(el->el>150).count()>0){
            Assert.fail("Not all products match the filter");
        }
        LOG.info("PASSED");
    }

    @Test
    public void verifyLogin() {
        LOG.info("CLick on login button");
        $("#iherb-account #sign-in").click();
        $("div#loginHeader").shouldBe(Condition.appears);
        LOG.info("Complete the login form");
        $("#UserNameLogin").setValue("epam.test.maryna@gmail.com");
        $("#Password").setValue("qwe123rty");
        $("input#header-sign-in-button").click();
        $("div#loginHeader").shouldBe(Condition.disappear);
        LOG.info("Close popup box");
        if ($("#share-page-container").isDisplayed())
            $(".icon-exit").click();
        LOG.info("Verify user logged in");
        $("#iherb-account #sign-in").shouldNotBe(Condition.visible);
    }
}
