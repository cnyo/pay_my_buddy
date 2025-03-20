# pay_my_buddy

| Tables            | Columns                                                                         |
|-------------------|---------------------------------------------------------------------------------|
| user              | (PK) id INT, username VARCHAR(50), email VARCHAR(255), password VARCHAR(255)    |
| association_user  | (PK) id INT, (FK) user_id INT, (FK) associated_user_id INT                      |
| transaction       | (PK) id INT, (FK) sender_user_id INT, (FK) receiver_user_id INT, amount DECIMAL |

Initialisation des données
```
psql -h localhost -p 5432 -U postgres -f data.sql
```

Vérification de l'initialisation
```
psql -h localhost -p 5432 -U postgres -d pay_my_buddy -c "\dt"
```



