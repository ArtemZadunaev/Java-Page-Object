package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private SelenideElement loginField = $("[data-test-id=login] input");
  private SelenideElement passwordField = $("[data-test-id=password] input");
  private SelenideElement loginButton = $("[data-test-id=action-login]");
  private SelenideElement errorMessage = $("[data-test-id=error-notification]");

  public VerificationPage validLogin(DataHelper.AuthInfo info) {
    login(info);
    return new VerificationPage();
  }
  public void login(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }
    public void authErrorMessage(String expText){
      errorMessage.should(Condition.visible).shouldHave(Condition.text(expText));
    }
}
