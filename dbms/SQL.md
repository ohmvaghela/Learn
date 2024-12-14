# Basic commands
- Status/Stop/Start

        sudo service mysql status/stop/start



`User with full privilages`

- root user - 'User1'
- root password - 'User1_password'

- Creating user 

        CREATE USER 'User1'@'localhost' IDENTIFIED BY 'User1_password'

- Granting full privilages to User1

        GRANT ALL PRIVILAGES ON *.* TO 'User1'@'localhost' WITH GRANT OPTION;
        FLUSH PRIVILEGES;

- Login to user in MySql

        mysql -u User1 -p

        