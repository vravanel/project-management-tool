# Project Management Tool 
C'est un projet de gestion de projet collaboratif pour les équipes de développement. Les fonctionnalités sont création de projets, invitation de membres avec des rôles, suivi des tâches, historique des modifications et notifications par email. 

## Sommaire
1. [Lancer le projet avec Docker](#lancer-le-projet-avec-docker)
2. [Variables d'environnement](#variables-denvironnement)
3. [Lancer en développement (sans Docker)](#lancer-en-développement-sans-docker)
4. [Tests et couverture de code](#tests-et-couverture-de-code)
5. [Documentation de l'API](#documentation-de-lapi)

## Lancer le projet avec Docker

1. Copier le `.env.example` en `.env` et modifier les valeurs (facultatif)
2. Démarrer l'application
```bash 
docker compose up -d --build
```
3. Ouvrir l'application : 
- Application frontend : http://localhost:8081
- API : http://localhost:8080
- Swagger : http://localhost:8080/swagger-ui/index.html
- Mailpit: http://localhost:8025

Commandes utiles : 
```bash
docker compose logs -f backend # suivre les logs du backend
docker compose down # arrêter les containers
docker compose down -v # arrêter et supprimer les données de la base
```

## Variables d'environnement
**Avec Docker**, les valeurs par défaut suffisent. Pour les changer, vous pouvez renseigner le fichier `.env` en copiant le `.env.example`

- `DB_USERNAME` = `postgres` 
- `DB_PASSWORD` = `postgres` 
- `FRONTEND_PORT` = `8081` 
- `BACKEND_PORT` = `8080` 
- `MAILPIT_PORT` = `8025`

**Sans Docker** seules `DB_USERNAME` et `DB_PASSWORD` sont obligatoires.

- `DB_USERNAME` = `postgres` 
- `DB_PASSWORD` = `postgres`

Les autres sont falcultatives : 

- `DB_URL` = `jdbc:postgresql://localhost:5432/project_management`
- `MAIL_HOST` = `localhost` 
- `MAIL_PORT` = `1025` 
- `CORS_ALLOWED_ORIGINS` = `http://localhost:4200` 


## Lancer en développement (sans Docker)
1. **Base de données** : créer la base `project_management` et exécuter `data.sql`. Avec Docker, par exemple :
   ```bash
   docker run -d --name pmt-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=project_management -p 5432:5432 postgres:16-alpine
   docker exec -i pmt-db psql -U postgres -d project_management < data.sql
   ```
2. **Serveur de mail** :
   ```bash
   docker run -d --name mailpit -p 1025:1025 -p 8025:8025 axllent/mailpit
   ```
3. **Backend** (depuis la racine) :
   ```bash
   DB_USERNAME=postgres DB_PASSWORD=postgres ./mvnw spring-boot:run
   ```
4. **Frontend** :
   ```bash
   cd frontend
   npm install --legacy-peer-deps
   npm start
   ```
   L'application est alors disponible sur http://localhost:4200. Le fichier `proxy.conf.json` redirige les appels `/api` vers le backend (port 8080).

## Tests et couverture de code
**Backend** 

```bash
./mvnw clean verify
```
Rapport de couverture : `target/site/jacoco/index.html`. Les tests utilisent une base H2 en mémoire : aucune base ni serveur de mail n'est nécessaire.

**Frontend** (dans `frontend/`).

```bash
npm install --legacy-peer-deps
npx ng test --no-watch --coverage
```
Rapport de couverture : `frontend/coverage/`.

## Documentation de l'API

Swagger UI (backend démarré) : http://localhost:8080/swagger-ui/index.html

La description OpenAPI brute est disponible sur http://localhost:8080/v3/api-docs.
