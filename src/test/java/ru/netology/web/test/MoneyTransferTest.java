package ru.netology.web.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.VerificationPage;

import java.util.Random;

import static com.codeborne.selenide.Selenide.open;

class MoneyTransferTest {
    boolean failTestClean = false;

    @BeforeEach
    public void setup() {
        open("http://localhost:9999");
        failTestClean = false;
    }

    @AfterEach
    public void cleanIfFail() {

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
        var amount = new Random().nextInt(9000);
        transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var expectedFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo()) - amount;
        var expectedSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo()) + amount;
        try {
            Assertions.assertEquals(expectedFirstCardBalance, firstCardStartBalance);
            Assertions.assertEquals(expectedSecondCardBalance, secondCardStartBalance);
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
        var amount = new Random().nextInt(100_000)+100_000;
        dashboardPage = transferPage.transferFromCard(DataHelper.getSecondCardInfo(), amount);
        var actualFirstCardBalance = dashboardPage.getCardBalance(DataHelper.getFirstCardInfo());
        var actualSecondCardBalance = dashboardPage.getCardBalance(DataHelper.getSecondCardInfo());
        try {
            Assertions.assertEquals(actualFirstCardBalance, firstCardStartBalance);
            Assertions.assertEquals(actualSecondCardBalance, secondCardStartBalance);
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
    @DisplayName("should Login")
    void shouldLogin() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
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

