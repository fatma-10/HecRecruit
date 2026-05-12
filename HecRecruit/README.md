# HecRecruit — Plateforme de recrutement IHEC

Application web Spring Boot pour la gestion des offres de stage, alternance et PFE
entre étudiants/alumni de l'IHEC Carthage et entreprises partenaires.

## Stack technique

- **Spring Boot 3.4.4** (corrigé depuis la version 4.0.6 qui n'existe pas)
- **Spring Web MVC** + **Thymeleaf** + **thymeleaf-layout-dialect**
- **Spring Data JPA** + **Hibernate**
- **MySQL** (prod) ou **H2** (dev/test, en mémoire)
- **Bean Validation** (jakarta.validation)
- **Lombok** (configuré, pas utilisé activement — laissé dispo)
- **Java 17**

## Architecture (package-by-layer)

```
com.IHEC.Recruit
├── RecruitApplication.java     ← point d'entrée
├── entity/                     ← entités JPA (Candidat, Offre, …)
├── repository/                 ← interfaces JpaRepository
├── service/                    ← logique métier + @Transactional
├── controller/                 ← contrôleurs MVC (mappent les URLs)
├── dto/                        ← form objects (LoginForm, OffreForm, …)
├── config/                     ← WebConfig, GlobalModelAttributes
├── security/                   ← CurrentUser (session), AuthInterceptor
└── exception/                  ← Business/NotFound/Forbidden + @ControllerAdvice
```

Côté ressources :

```
src/main/resources
├── application.properties          ← config MySQL par défaut
├── application-h2.properties       ← profil "h2" pour test sans MySQL
├── static/css/main.css             ← design éditorial hand-rolled
└── templates/                      ← Thymeleaf + layout-dialect
    ├── fragments/layout.html       ← master template
    ├── welcome.html
    ├── type-selection.html
    ├── auth/        (login, register-candidat, register-entreprise)
    ├── candidat/    (dashboard, mes-candidatures, profil)
    ├── entreprise/  (dashboard, mes-offres, candidats-offre, profil)
    ├── offre/       (list, detail, creer)
    ├── forum/list.html
    ├── wishlist/list.html
    └── error/error.html
```

## Démarrage rapide

### Option A — Avec H2 (aucune base à installer)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Console H2 dispo sur http://localhost:8080/h2-console (JDBC URL : `jdbc:h2:mem:recruit`).

### Option B — Avec MySQL

1. Créer la base `poo` :
   ```sql
   CREATE DATABASE poo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Adapter le mot de passe dans `src/main/resources/application.properties` si besoin.
3. Lancer :
   ```bash
   ./mvnw spring-boot:run
   ```

L'app est sur http://localhost:8080

## Workflow utilisateur

### Candidat (Étudiant / Alumni)

- `/` → page d'accueil
- `/register/candidat` → inscription (email doit finir par `@ihec.ucar.tn`)
- `/login/candidat` → connexion
- `/candidat/dashboard` → tableau de bord avec recommandations personnalisées
- `/offres` → catalogue avec recherche par titre + filtre par type
- `/offres/{id}` → détail + bouton "Postuler"
- `/candidat/mes-candidatures` → liste + retrait
- `/candidat/profil` → modifier téléphone + champs spécifiques (étudiant : niveau/filière/établissement ; alumni : poste actuel/entreprise)
- `/forum` → publication + recherche

### Entreprise

- `/register/entreprise` → inscription
- `/login/entreprise` → connexion
- `/entreprise/dashboard` → stats + offres récentes
- `/entreprise/creer-offre` → formulaire dynamique (champs adaptés au type d'offre)
- `/entreprise/mes-offres` → liste, suppression
- `/entreprise/mes-offres/{id}/candidats` → vue des postulants + bouton wishlist + retrait
- `/entreprise/wishlist` → candidats suivis
- `/entreprise/profil` → modifier secteur/adresse/téléphone
- `/forum` → publication et lecture
- `/offres/{id}` → détail avec section "actions propriétaire" (date d'expiration, suppression)

## Authentification

Session HTTP + bean `CurrentUser` scope session.
`AuthInterceptor` protège `/candidat/**` et `/entreprise/**`.
Pas de Spring Security — choix volontaire pour un projet académique.

⚠ **Mots de passe stockés en clair** comme dans la version JavaFX originale.
Acceptable pour un projet pédagogique, à remplacer par BCrypt pour tout usage réel.

## Recommandations (service `RecommendationService`)

Score sur 100, calculé en fonction de :
- correspondance filière (40%)
- adéquation niveau / type d'offre (20%)
- fraîcheur de l'offre (15%)
- popularité (15%)
- secteur de l'entreprise (10%)

Les recommandations alumni utilisent une logique séparée basée sur le poste actuel.

## Bugs corrigés depuis la version d'origine

| # | Bug | Fix |
|---|-----|-----|
| 1 | `pom.xml` mentionnait Spring Boot 4.0.6 (inexistant) et `spring-boot-starter-webmvc`/`*-test` (faux) | Spring Boot 3.4.4 + artifacts corrects |
| 2 | `OffreSpecialisee` avait un `@Inheritance` redondant qui cassait le mapping | `@Inheritance` uniquement sur `Offre` |
| 3 | `OffreSpecialisee` avait sa propre `@Table` créant une table intermédiaire vide | Pas de `@Table` |
| 4 | `equals/hashCode` peu sûrs (false quand id null des deux côtés) | Pattern Hibernate-safe |
| 5 | NPE possibles dans `RecommendationService.calculerScoreFiliere` si `domaine`/`sujet`/`technologies` étaient null | `nullSafe()` partout |
| 6 | Scores étudiant et alumni sur des échelles incompatibles | Mêmes unités (0-100), même formule de pondération |
| 7 | `AuthService.registerCandidat` faisait `Integer.parseInt(infos.get("id"))` sans vérif | Validation complète + `BusinessException` |
| 8 | Les services lançaient `IllegalArgumentException`/`SecurityException` sans handler global | `BusinessException`/`NotFoundException`/`ForbiddenException` + `@ControllerAdvice` |
| 9 | Pas de validation des formulaires côté serveur | DTOs avec `@Valid`, `@NotBlank`, `@Email`, `@Pattern`, … |
| 10 | Aucune gestion de session | `CurrentUser` session-scoped + `AuthInterceptor` |

## Bugs NON corrigés (volontairement)

- **Mots de passe en clair** : conservé pour rester compatible avec les données existantes du projet pédagogique. À remplacer par BCrypt avant toute mise en prod.
- **CIN comme entier** : un CIN tunisien valide peut commencer par `0`, ce qui est tronqué par `int`. Conservé pour compatibilité avec le schéma original.

## Tests

```bash
./mvnw test
```

Un seul test (`contextLoads`) — vérifie que tout le câblage Spring fonctionne avec le profil H2.

## TODO si vous voulez aller plus loin

- Spring Security + BCrypt
- Tests d'intégration `@WebMvcTest` par contrôleur
- Upload de CV (PDF) avec stockage
- Notifications email (Spring Mail)
- Pagination des listes (`Pageable`)
- Internationalisation (i18n) pour passer en arabe/anglais
