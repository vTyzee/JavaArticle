# Articles Application

Это простое Spring Boot веб-приложение для работы со статьями, тегами, комментариями и пользователями. В приложении реализованы следующие функции:
- Просмотр списка статей
- Поиск статей по заголовку, содержимому и имени автора
- Просмотр отдельной статьи с комментариями
- Добавление комментариев (только для залогиненных пользователей)
- Регистрация и авторизация пользователей
- Администрирование (например, просмотр списка всех пользователей)

## Что понадобится

- **Java JDK 17** (или новее) – [скачайте с Oracle/OpenJDK](https://adoptium.net/)
- **Maven** – для сборки проекта (Maven обычно включается в IDE вроде IntelliJ IDEA, Eclipse, или можно установить отдельно)
- **XAMPP** (или другой сервер MySQL) – для работы с базой данных MySQL/MariaDB  
  [Скачать XAMPP](https://www.apachefriends.org/ru/index.html)

## Как запустить приложение

### 1. Склонируйте или загрузите проект

Сохраните исходный код проекта в удобное для вас место на диске.

### 2. Настройте базу данных

Если вы используете XAMPP:
1. Запустите XAMPP Control Panel и включите модуль **MySQL**.
2. Зайдите в phpMyAdmin (обычно по адресу [http://localhost/phpmyadmin](http://localhost/phpmyadmin)) и создайте новую базу данных, например, `articles_db`.

### 3. Настройка файла свойств

В проекте в папке `src/main/resources` откройте файл `application.properties` (или создайте его) и настройте подключение к базе данных. Например:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/articles_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate настройки (создание схемы автоматически)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Для Thymeleaf Extras Spring Security (если нужно)
spring.thymeleaf.cache=false
