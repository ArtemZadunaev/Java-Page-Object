package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement heading = $("[data-test-id=dashboard]");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        heading.shouldBe(visible);
    }


    private SelenideElement getCardElement(DataHelper.CardInfo cardInfo) {
        return cards.findBy(Condition.attribute("data-test-id", cardInfo.getTestId()));
    }

    private SelenideElement getCardTransferButton(DataHelper.CardInfo cardInfo) {
        return getCardElement(cardInfo).find(By.cssSelector("[data-test-id=action-deposit]"));
    }

    public TransferPage cardTransfer(DataHelper.CardInfo cardInfo) {
        getCardTransferButton(cardInfo).click();
        return new TransferPage(cardInfo);
    }

    public int getCardBalance(DataHelper.CardInfo cardInfo) {
        var elementText = getCardElement(cardInfo).getText();
        return extractBalance(elementText);
    }

    private int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }
}
