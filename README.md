# pay_my_buddy

| Tables            | Columns                                                                            |
|-------------------|------------------------------------------------------------------------------------|
| users             | (PK) id INT, username VARCHAR(50), email VARCHAR(255), password VARCHAR(255)       |
| association_users | (PK) id INT, (FK) user_id INT, (FK) associated_user_id INT, date DATETIME          |
| transactions      | (PK) id INT, (FK) user_id_1 INT, (FK) user_id_2 INT, amount DECIMAL, date DATETIME |

Initialisation de la base de données :

```
psql -h localhost -p 5432 -U postgres -f ./resources/create_database.sql
psql -h localhost -p 5432 -U postgres -d pay_my_buddy -f ./resources/data.sql

# Initialisation de la base de données de test
psql -h localhost -p 5432 -U postgres -f ./src/test/resources/create_database_test.sql
psql -h localhost -p 5432 -U postgres -d pay_my_buddy_test -f ./src/test/resources/data-test.sql
psql -h localhost -p 5432 -U postgres -d pay_my_buddy_test -f ./src/main/resources/create_trigger_function_user_order.sql
```

Vérification de l'initialisation

```
psql -h localhost -p 5432 -U postgres -d pay_my_buddy -c "\dt"
```