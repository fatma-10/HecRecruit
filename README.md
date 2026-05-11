# HecRecruit - Spring Boot MVC Application

## 📋 Description

HecRecruit est une application web Spring Boot MVC pour la gestion des offres d'emploi, stages et alternances. Elle permet aux candidats (étudiants et alumni) de consulter les offres, et aux entreprises de publier leurs opportunités.

## 🏗️ Architecture

### Structure du Projet

```
src/main/java/com/hecrecruit/
├── model/                 # Entités JPA
│   ├── Candidat.java
│   ├── Etudiant.java
│   ├── Alumni.java
│   ├── Entreprise.java
│   ├── Offre.java
│   ├── Stage.java
│   ├── Alternance.java
│   ├── ProjetFinEtudes.java
│   └── Forum.java
├── repository/            # Data Access Layer
│   ├── CandidatRepository.java
│   ├── EntrepriseRepository.java
│   ├── OffreRepository.java
│   └── ForumRepository.java
├── service/              # Business Logic
│   ├── CandidatService.java
│   ├── EntrepriseService.java
│   ├── OffreService.java
│   └── ForumService.java
├── controller/           # Web Controllers
│   ├── HomeController.java
│   ├── CandidatController.java
│   ├── EntrepriseController.java
│   └── OffreController.java
└── HecRecruitApplication.java

src/main/resources/
├── application.properties # Configuration
└── templates/            # Thymeleaf Templates
    ├── index.html
    ├── offres/
    ├── candidats/
    └── entreprises/
```

## 🔧 Modèles (Entities)

### Candidat (Base Class)
- **id**: CIN (Integer, Primary Key)
- **nom**: String
- **prenom**: String
- **email**: String (Unique)
- **telephone**: String
- **mdp**: String (Password)
- **candidaturesEnCours**: List<Offre> (Many-to-Many)

### Etudiant (extends Candidat)
- **niveau**: String (Education level)
- **filiere**: String (Field of study)
- **etablissement**: String (Institution)

### Alumni (extends Candidat)
- **anneeGraduation**: Integer
- **diplome**: String
- **emploiActuel**: String
- **entreprise**: String
- **dateEmbauche**: LocalDate

### Entreprise
- **id**: UUID (Primary Key)
- **nom**: String
- **secteur**: String
- **adresse**: String
- **email**: String (Unique)
- **telephone**: String
- **mdp**: String
- **offresPubliees**: List<Offre> (One-to-Many)
- **wishlist**: List<Candidat> (Many-to-Many)

### Offre (Base Class)
- **id**: UUID (Primary Key)
- **titre**: String
- **description**: Text
- **typeOffre**: String (JOINED Inheritance)
- **datePublication**: LocalDate
- **dateExpiration**: LocalDate
- **entreprise**: Entreprise (Many-to-One)
- **candidatures**: List<Candidat> (Many-to-Many)

### Stage (extends Offre)
- **dureeMois**: Integer
- **gratification**: Double
- **lieu**: String

### Alternance (extends Offre)
- **salaireMensuel**: Double
- **rythmeAlternance**: String
- **dureeContratMois**: Integer

### ProjetFinEtudes (extends Offre)
- **technologies**: String
- **niveauRequis**: String
- **dureeMois**: Integer

### Forum
- **id**: UUID (Primary Key)
- **titre**: String
- **message**: Text
- **dateCreation**: LocalDateTime
- **candidat**: Candidat (Many-to-One)
- **nombreReponses**: Integer

## 🚀 Configuration & Installation

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Configurer la base de données

Créer une base de données MySQL :
```sql
CREATE DATABASE hec_recruit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Modifier `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hec_recruit
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Compiler et Démarrer

```bash
# Cloner le projet
git clone -b mariem https://github.com/fatma-10/HecRecruit.git
cd HecRecruit

# Compiler
mvn clean install

# Démarrer l'application
mvn spring-boot:run
```

### 4. Accéder à l'Application

Ouvrir le navigateur et accéder à :
```
http://localhost:8080/api/
```

## 📊 Endpoints Disponibles

### Home
- `GET /api/` - Accueil
- `GET /api/home` - Page d'accueil
- `GET /api/about` - À propos

### Offres
- `GET /api/offres` - Liste des offres
- `GET /api/offres?type=Stage` - Filtrer par type
- `GET /api/offres/{id}` - Détails d'une offre

### Candidats
- `GET /api/candidats` - Liste des candidats
- `GET /api/candidats/{id}` - Profil candidat

### Entreprises
- `GET /api/entreprises` - Liste des entreprises
- `GET /api/entreprises/{id}` - Détails entreprise

## 🗄️ Stratégie d'Héritage

**JOINED Inheritance** utilisée pour :
- Candidat → Etudiant, Alumni
- Offre → Stage, Alternance, ProjetFinEtudes

Cette stratégie permet les requêtes polymorphes et maintient l'intégrité des données.

## 💾 Dépendances Principales

- **Spring Boot 3.1.5**
- **Spring Data JPA** - ORM & Database Access
- **Hibernate** - JPA Implementation
- **Thymeleaf** - Template Engine
- **MySQL Connector/J** - Database Driver
- **Lombok** - Code Generation
- **Jakarta Persistence API** - JPA Annotations

## 📝 Notes Importantes

✅ **Préservé du Code Original:**
- Toute la logique métier des services
- Tous les attributs et validations des modèles
- Toutes les relations et comportements entités
- Méthodes `getInfosPrincipales()`

❌ **Éliminé:**
- JavaFX (remplacé par Spring Web)
- Fichiers FXML (remplacés par Thymeleaf)
- FileManager & DataManager (remplacés par Repositories JPA)
- Main.java JavaFX Entry Point

## 🔐 Sécurité

- Les mots de passe sont stockés en base (envisager le hachage en production)
- Validation des entrées au niveau de la base de données
- Injection de dépendances pour le testing

## 📈 Améliorations Futures

- [ ] Ajouter Spring Security pour l'authentification
- [ ] Implémenter le hachage des mots de passe (BCrypt)
- [ ] Ajouter les tests unitaires (JUnit 5)
- [ ] API REST endpoints complètes
- [ ] Validation des formulaires côté client
- [ ] Pagination pour les listes
- [ ] Système de notification

## 👨‍💻 Auteur

**Mariem-Zitouna** - HecRecruit Spring Boot Migration

## 📄 Licence

MIT License
