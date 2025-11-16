package ru.netology.web.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;

class MoneyTransferTest {
    boolean failTestClean = false;

    @BeforeEach
    public void setup() {
        open("http://localhost:9999");
        failTestClean = false;
    }

    @Test
    @DisplayName("should Transfer Money Between Own Cards")
    void shouldTransferMoneyBetweenOwnCards() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);

        var firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        var secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        var transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        var amount = DataHelper.validAmountGenerator();
        transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo()) - amount;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo()) + amount;
        try {
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedFirstCardBalance, firstCardStartBalance),
                    () -> Assertions.assertEquals(expectedSecondCardBalance, secondCardStartBalance)
            );
        } catch (AssertionError e) {
            failTestClean = true;
            throw e;
        } finally {
            if (failTestClean) {
                dashboardPage.cardTransfer(DataHelper.getSecondCardInfo());
                transferPage.transferFromCard(DataHelper.getFirstCardInfo(), amount);
            }
        }
        dashboardPage.cardTransfer(DataHelper.getSecondCardInfo());
        transferPage.transferFromCard(DataHelper.getFirstCardInfo(), amount);
    }

    @Test
    @DisplayName("should Not Transfer Money Between Own Cards")
    void shouldNotTransferMoneyBetweenOwnCardsOverLimit() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        var secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        var transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        var amount = DataHelper.invalidAmountGenerator();
        dashboardPage = transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var actualFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        var actualSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        try {
            Assertions.assertAll(
                    () -> Assertions.assertEquals(firstCardStartBalance, actualFirstCardBalance),
                    () -> Assertions.assertEquals(secondCardStartBalance, actualSecondCardBalance)
            );
        } catch (AssertionError e) {
            failTestClean = true;
            throw e;
        } finally {
            if (failTestClean) {
                dashboardPage.cardTransfer(DataHelper.getSecondCardInfo());
                transferPage.transferFromCard(DataHelper.getFirstCardInfo(), amount);
            }
        }
    }

    @Test
    @DisplayName("should Transfer Huge Amount Between Own Cards")
    void shouldTransferHugeAmountBetweenOwnCards() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);

        var firstCardStartBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        var secondCardStartBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        var transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        var amount = DataHelper.hugeAmountGenerator();
        transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo()) - amount;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo()) + amount;
        try {
            Assertions.assertAll(
                    () -> Assertions.assertEquals(expectedFirstCardBalance, firstCardStartBalance),
                    () -> Assertions.assertEquals(expectedSecondCardBalance, secondCardStartBalance)
            );
        } catch (AssertionError e) {
            failTestClean = true;
            throw e;
        } finally {
            if (failTestClean) {
                dashboardPage.cardTransfer(DataHelper.getSecondCardInfo());
                transferPage.transferFromCard(DataHelper.getFirstCardInfo(), amount);
            }
        }
        dashboardPage.cardTransfer(DataHelper.getSecondCardInfo());
        transferPage.transferFromCard(DataHelper.getFirstCardInfo(), amount);
    }

    @Test
    @DisplayName("should Chancel Transfer")
    void shouldChancelTransfer() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var transferPage = dashboardPage.cardTransfer(DataHelper.getFirstCardInfo());
        transferPage.chancelTransfer();
    }

    @Test
    @DisplayName("should Not Login With Invalid Password")
    void shouldNotLoginWithInvalidPassword() {
        var loginPage = new LoginPage();
        var inValidPassAuthInfo = DataHelper.getInvalidPassAuthInfo();
        loginPage.login(inValidPassAuthInfo);
        loginPage.authErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("should Not Login With Invalid Login")
    void shouldNotLoginWithInvalidLogin() {
        var loginPage = new LoginPage();
        var inValidPassAuthInfo = DataHelper.getInvalidLoginAuthInfo();
        loginPage.login(inValidPassAuthInfo);
        loginPage.authErrorMessage("Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("should Not Login With Invalid Auth Code")
    void shouldNotLoginWithInvalidAuthCode() {
        var loginPage = new LoginPage();
        var validAuthInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(validAuthInfo);
        verificationPage.verify(DataHelper.getInvalidVerificationCodeFor(validAuthInfo));
        verificationPage.errorVerificationMessage("Неверно указан код! Попробуйте ещё раз.");
    }

    @Test
    @DisplayName("should Update Page")
    void shouldUpdatePage() {
        var loginPage = new LoginPage();
        var validAuthInfo = DataHelper.getAuthInfo();
        loginPage.validLogin(validAuthInfo);
        Selenide.refresh();
        new VerificationPage();

    }


}

