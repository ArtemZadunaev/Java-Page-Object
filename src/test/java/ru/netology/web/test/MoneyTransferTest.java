package ru.netology.web.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransferPage;
import ru.netology.web.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;

class MoneyTransferTest {
    DashboardPage dashboardPage;
    TransferPage transferPage;
    VerificationPage verificationPage;
    LoginPage loginPage;
    int firstCardStartBalance;
    int secondCardStartBalance;

    @BeforeEach
    public void setup() {
        open("http://localhost:9999");
        loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        int amountCorrection = 0;
        firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        if (firstCardStartBalance > 10000) {
            amountCorrection = firstCardStartBalance - 10000;
            transferPage = dashboardPage.cardTransfer(DataHelper.getSecondCardInfo());
            transferPage.transferFromCard(DataHelper.getFirstCardInfo(), amountCorrection);
        } else if (secondCardStartBalance > 10000) {
            amountCorrection = secondCardStartBalance - 10000;
            transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
            transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amountCorrection);
        } else {
            transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
            transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amountCorrection);
        }

    }

    @Test
    @DisplayName("should Transfer Money Between Own Cards")
    void shouldTransferMoneyBetweenOwnCards() {
        firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        transferPage.clearFields();
        var amount = DataHelper.validAmountGenerator();
        transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo()) - amount;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo()) + amount;

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedFirstCardBalance, firstCardStartBalance),
                () -> Assertions.assertEquals(expectedSecondCardBalance, secondCardStartBalance));


    }

    @Test
    @DisplayName("should Not Transfer Money Between Own Cards")
    void shouldNotTransferMoneyBetweenOwnCardsOverLimit() {
        firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        var amount = DataHelper.invalidAmountGenerator();
        dashboardPage = transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var actualFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        var actualSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        Assertions.assertAll(
                () -> Assertions.assertEquals(firstCardStartBalance, actualFirstCardBalance),
                () -> Assertions.assertEquals(secondCardStartBalance, actualSecondCardBalance));
    }

    @Test
    @DisplayName("should Transfer Huge Amount Between Own Cards")
    void shouldTransferHugeAmountBetweenOwnCards() {

        firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        var amount = DataHelper.hugeAmountGenerator();
        transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo()) - amount;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo()) + amount;
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedFirstCardBalance, firstCardStartBalance),
                () -> Assertions.assertEquals(expectedSecondCardBalance, secondCardStartBalance));
    }

    @Test
    @DisplayName("should Chancel Transfer")
    void shouldChancelTransfer() {
        transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        transferPage.chancelTransfer();
    }

    @Test
    @DisplayName("should Not Login With Invalid Password")
    void shouldNotLoginWithInvalidPassword() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var inValidPassAuthInfo = DataHelper.getInvalidPassAuthInfo();
        loginPage.login(inValidPassAuthInfo);
        loginPage.authErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("should Not Login With Invalid Login")
    void shouldNotLoginWithInvalidLogin() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var inValidPassAuthInfo = DataHelper.getInvalidLoginAuthInfo();
        loginPage.login(inValidPassAuthInfo);
        loginPage.authErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("should Not Login With Invalid Auth Code")
    void shouldNotLoginWithInvalidAuthCode() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var validAuthInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(validAuthInfo);
        verificationPage.verify(DataHelper.getInvalidVerificationCodeFor(validAuthInfo));
        verificationPage.errorVerificationMessage("Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("should Update Page")
    void shouldUpdatePage() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var validAuthInfo = DataHelper.getAuthInfo();
        loginPage.validLogin(validAuthInfo);
        Selenide.refresh();
        new VerificationPage();
    }


}

