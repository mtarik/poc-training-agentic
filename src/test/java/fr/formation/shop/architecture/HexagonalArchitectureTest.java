package fr.formation.shop.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Règles de l'architecture hexagonale, vérifiées à chaque build.
 *
 * <pre>
 *   adapter.in (web)  ──▶  application.port.in  ◀──  application.service  ──▶  application.port.out  ◀──  adapter.out
 *                                       └──────────────▶  domain  ◀──────────────┘
 * </pre>
 */
@AnalyzeClasses(packages = "fr.formation.shop", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_be_pure_java = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "com.fasterxml..", "tools.jackson..",
                    "..application..", "..adapter..")
            .because("le domaine est du Java pur : il ne connait ni les frameworks, ni les cas d'usage, ni l'infrastructure");

    @ArchTest
    static final ArchRule application_must_not_depend_on_adapters = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")
            .because("l'application ne connait l'infrastructure qu'a travers ses ports");

    @ArchTest
    static final ArchRule application_must_not_use_infrastructure_apis = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.persistence..", "org.springframework.data..", "org.springframework.jdbc..",
                    "org.springframework.web..", "org.springframework.http..")
            .because("seuls @Service et @Transactional sont toleres dans la couche application");

    @ArchTest
    static final ArchRule inbound_adapters_must_only_use_input_ports = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application.port.out..", "..application.service..", "..adapter.out..")
            .because("un adapter entrant passe uniquement par un use case (port entrant)");

    @ArchTest
    static final ArchRule outbound_adapters_must_not_depend_on_inbound_side = noClasses()
            .that().resideInAPackage("..adapter.out..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..adapter.in..", "..application.port.in..", "..application.service..")
            .because("un adapter sortant implemente un port sortant et rien d'autre");

    @ArchTest
    static final ArchRule output_ports_must_be_interfaces = classes()
            .that().resideInAPackage("..application.port.out..")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule use_cases_must_be_interfaces = classes()
            .that().resideInAPackage("..application.port.in..")
            .and().haveSimpleNameEndingWith("UseCase")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule jpa_entities_must_live_in_persistence_adapter = classes()
            .that().areAnnotatedWith(Entity.class)
            .should().resideInAPackage("..adapter.out.persistence..")
            .andShould().haveSimpleNameEndingWith("JpaEntity");

    @ArchTest
    static final ArchRule controllers_must_live_in_web_adapter = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..adapter.in.web..");
}
