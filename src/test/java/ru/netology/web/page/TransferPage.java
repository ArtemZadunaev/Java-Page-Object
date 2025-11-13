package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TransferPage {
    SelenideElement amount = $("[data-test-id= amount] input");
    SelenideElement from = $("[data-test-id=from] input");
    SelenideElement to = $("[data-test-id=to] input");
    SelenideElement transferButton = $("[data-test-id=action-transfer].button");
    ElementsCollection chancelButton = $$("button");
    DataHelper.CardInfo card;

    public TransferPage(DataHelper.CardInfo cardInfo) {
        to.shouldBe(visible).shouldHave(Condition.value(DataHelper.getCardDisableValue(cardInfo)));
        this.card = cardInfo;
    }

    private SelenideElement getChancelButton() {
        return chancelButton.find(Condition.text("Отмена"));
    }

    public DashboardPage chancelTransfer() {
        getChancelButton().click();
        return new DashboardPage();
    }

    public DashboardPage transferFromCard(DataHelper.CardInfo cardInfo, int amount) {
        clearFields();
        this.amount.setValue(Integer.toString(amount));
        from.setValue(cardInfo.getCardNumber());
        transferButton.click();
        return new DashboardPage();
    }

    public void clearFields() {
        from.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        amount.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }


}
