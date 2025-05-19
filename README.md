# pay_my_buddy

| Tables            | Columns                                                                                        |
|-------------------|------------------------------------------------------------------------------------------------|
| users             | (PK) id INT, username VARCHAR(50) (U), email VARCHAR(255) (U), password VARCHAR(255)           |
| association_users | (PK) id INT, (FK) user_id_1 INT, (FK) user_id_2 INT, date DATETIME                             |
| transactions      | (PK) id INT, (FK) sender_user_id INT, (FK) receiver_user_id INT, amount DECIMAL, date DATETIME |

Initialisation de la base de données :

```
psql -h localhost -p 5432 -U BDD_USER -f ./resources/create_database.sql
psql -h localhost -p 5432 -U BDD_USER -d DB_NAME -f ./resources/data.sql
```

Vérification de l'initialisation

```
psql -h localhost -p 5432 -U BDD_USER -d DB_NAME -c "\dt"
```

Deploy

```
 java -jar .\target\alerts-0.0.1-SNAPSHOT.jar
```

Generate surfire report

```
mvn site
mvn surefire-report:report
```