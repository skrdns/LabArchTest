package com.example.crud_demo.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void init() {
        importedClasses = new ClassFileImporter().importPackages("com.example.crud_demo");
    }

    // 1. Контролери мають бути в пакеті controller
    @Test
    void controllersShouldResideInControllerPackage() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller..")
                .check(importedClasses);
    }

    // 2. Моделі (наприклад, Book) мають бути в пакеті model
    @Test
    void modelsShouldResideInModelPackage() {
        ArchRuleDefinition.classes()
                .that().haveSimpleNameEndingWith("Book")
                .should().resideInAPackage("..model..")
                .check(importedClasses);
    }

    // 3. Моделі не повинні мати залежностей від контролерів чи репозиторіїв
    @Test
    void modelsShouldBeIndependent() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..model..")
                .should().onlyDependOnClassesThat().resideInAnyPackage("java..")
                .check(importedClasses);
    }

    // 4. Репозиторії не повинні залежати від контролерів
    @Test
    void repositoryShouldNotDependOnController() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(importedClasses);
    }

    // 5. Тільки контролери мають бути анотовані @RestController
    @Test
    void onlyControllersShouldBeAnnotatedWithRestController() {
        ArchRuleDefinition.noClasses()
                .that().resideOutsideOfPackage("..controller..")
                .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .check(importedClasses);
    }

    // 6. Заборонено використовувати field injection через @Autowired
    @Test
    void noFieldInjection() {
        ArchRuleDefinition.noFields()
                .should().beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
                .check(importedClasses);
    }

    // 7. Всі класи мають бути в пакеті com.example.crud_demo
    @Test
    void packageNamesShouldFollowConvention() {
        ArchRuleDefinition.classes()
                .should().resideInAPackage("com.example.crud_demo..")
                .check(importedClasses);
    }

    // 8. Контролери не повинні кидати загальний Exception
    @Test
    void controllersShouldNotThrowGenericException() {
        ArchRuleDefinition.noMethods()
                .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
                .should().declareThrowableOfType(Exception.class)
                .check(importedClasses);
    }

    // 9. Класи не повинні напряму використовувати java.sql (якщо не використовується JDBC)
    @Test
    void noDirectJdbcUsage() {
        ArchRuleDefinition.noClasses()
                .should().accessClassesThat().resideInAPackage("java.sql..")
                .check(importedClasses);
    }

    // 10. Інтерфейси репозиторіїв мають бути публічними
    @Test
    void repositoriesShouldBePublic() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().bePublic()
                .check(importedClasses);
    }

    // 11. Репозиторії повинні бути лише інтерфейсами (без реалізацій)
    @Test
    void repositoryShouldBeInterfaceOnly() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().beInterfaces()
                .check(importedClasses);
    }

    // 12. Внутрішні реалізації репозиторіїв не мають бути публічними
    @Test
    void innerRepositoriesShouldNotBePublic() {
        ArchRuleDefinition.classes()
                .that().haveSimpleName("InMemoryBookRepository")
                .should().notBePublic()
                .check(importedClasses);
    }

    // 13. Модель не повинна мати залежностей від контролера
    @Test
    void modelsShouldNotDependOnController() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..model..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .check(importedClasses);
    }

    // 14. Всі класи повинні бути в базовому пакеті (не поза межами com.example.crud_demo)
    @Test
    void allClassesShouldBeInBasePackage() {
        ArchRuleDefinition.noClasses()
                .should().resideOutsideOfPackage("com.example.crud_demo..")
                .check(importedClasses);
    }

    // 15. Жоден клас не повинен бути в default package
    @Test
    void noClassShouldBeInDefaultPackage() {
        ArchRuleDefinition.noClasses()
                .should().resideInAPackage("")
                .check(importedClasses);
    }

    // 16. Тільки сервіси повинні бути анотовані @Service
    @Test
    void onlyServicesShouldBeAnnotatedWithService() {
        ArchRuleDefinition.noClasses()
                .that().resideOutsideOfPackage("..service..")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
                .check(importedClasses);
    }

    // 17. Не можна використовувати одні й ті самі залежності в кількох різних модулях без чіткої архітектурної структури
    @Test
    void avoidMultipleModuleDependencies() {
        ArchRuleDefinition.noClasses()
                .should().dependOnClassesThat().resideInAnyPackage("..module1..", "..module2..")
                .check(importedClasses);
    }

    // 18. Інтерфейси повинні бути публічними
    @Test
    void interfacesShouldBePublic() {
        ArchRuleDefinition.classes()
                .that().areInterfaces()
                .should().bePublic()
                .check(importedClasses);
    }

    // 19. Класи репозиторіїв не повинні залежати від бізнес-логіки чи контролерів
    @Test
    void repositoriesShouldNotDependOnBusinessLogicOrControllers() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat().resideInAPackage("..logic..")
                .orShould().dependOnClassesThat().resideInAPackage("..controller..")
                .check(importedClasses);
    }

    // 20. Класи, що відповідають за обробку запитів, не повинні залежати від класів, що містять бізнес-логіку
    @Test
    void requestHandlingClassesShouldNotDependOnBusinessLogic() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..logic..")
                .check(importedClasses);
    }

}
