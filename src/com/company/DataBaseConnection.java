package com.company;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class DataBaseConnection {
    private String url = "jdbc:postgresql://localhost:5432/studs"; //Записываем url, в конце имя DB
    private String name = "s264912";// Имя
    private String pass = "qbi667";// Пароль
    private Connection connection = null;// Соединение
    private CommandManager command;//


    {
        try {
            Class.forName("org.postgresql.Driver"); //Подключаем драйвер
            System.out.println("Драйвер загружен");//Выводим сообщение
            connection = DriverManager.getConnection(url, name, pass);//Устанавливаем соединение
           // System.out.println("Creating table in selected database...");
            //String SQL = "CREATE TABLE pigs ";
            //PreparedStatement statement = connection.prepareStatement(SQL);
            //statement.executeUpdate();
            System.out.println("Соединение успешно установлено! ");//Сообщение о подключении
        } catch (Exception e) {
            e.printStackTrace(); //Выводим стректрейс ошибки
            System.out.println("Не получилось подключиться к DB!"); //Соответствующие сообщение
        }

    }
    Connection getConnection() {
        return this.connection;
    }

    public void loadPersons(ArrayList<Pig> arrayList) { //Загружаем в коллекцию данные из DB

        try {
            PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM pigs;");
            ResultSet result = preStatement.executeQuery();
//            int i = 0;
//            OffsetDateTime time = OffsetDateTime.now(); // Установка времени
//            PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM pigs;"); //Создание запроса
//            ResultSet result = preStatement.executeQuery(); //Результат
//
//            while (result.next()) { //Вытаскиваем все объекты из DB поочерёдности
//                String name = result.getString("name");
//                String color = result.getString("color");
//                /*String name = result.getString("name");
//                int age = result.getInt("age");
//                int health = result.getInt("health");
//                Feels feels = Feels.valueOf(result.getString("feel"));
//                Emotions emotion = Emotions.valueOf(result.getString("emotion"));
//                double x = result.getDouble("x");
//                double y = result.getDouble("y");
//                //массив одежды
//                String date = result.getString("creation_date");
//                if (date != null) {//todo
//                    time = OffsetDateTime.parse(result.getString("creation_date").replace(" ", "T"));
//                }*/
//                Pig new_pig = new Pig(name, color);
//                //добавляем в коллекцию объект
//                arrayList.add(new_pig); //Добавляем в коллекцию
//                System.out.println("В коллекцию из DB добавлен " + new_pig.get_name());
//                System.out.println("Данные о загруженном объекте из DB: \nИмя:" + new_pig.name+"\nВид:"+new_pig.get_color());
//                //todo убрать счётчик
//                i++;
//            }
//            return i; //Возвращаем
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при добавлении Bear");
            //return -1;
        }
    }

    public void addToDB(Pig pig) {
        try {
            addHuman(pig);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при добавлении " + pig.getName() + " в DB");
        }
    }

    public boolean cheakOnPass(String pass, String user) throws SQLException {
        String hashNew = DataBaseConnection.encryptString(pass);
        PreparedStatement preStatement = connection.prepareStatement("SELECT username, hash FROM users;"); //Создание запроса
        ResultSet result = preStatement.executeQuery(); //Результат
        while(result.next()){
            String username = result.getString("username");
            String hash = result.getString("hash");
            if (hashNew.equals(hash) && user.equals(username) ){
                System.out.println("Проверка на логин и пароль прошла успешна!");
                return true;
            }

        }

        System.out.println("Пользователь пытается ломануть бд");
        return false;
    }

    //todo Работает, только придётся расширять под весь объект
    private void addHuman(Pig pig) throws SQLException { //добавление Pig
        PreparedStatement preStatement = connection.prepareStatement("INSERT INTO pigs(name, creationdate, color, x, y, acrons, owner) VALUES (?, ?, ?, ?, ?, ?, ?);"); //Создаём запрос для добавления Mumi
        //Здесь мы устанавливаем пол
        preStatement.setString(1, pig.getName());
        preStatement.setString(2, pig.getDataCreate().toString());
        preStatement.setString(3, pig.getColor());
        preStatement.setDouble(4, pig.getX());
        preStatement.setDouble(5, pig.getY());
        preStatement.setString(6, pig.getAcrons());
        preStatement.setString(7, pig.getOwner());

        preStatement.executeUpdate();//Здесь записываем в таблицу
        System.out.println(pig.getName() + " был добавлен в DB.");

    }

    public boolean removePerson(String username, Pig pig) { //дя команды remove_greater по age
        try {
            PreparedStatement preStatement = connection.prepareStatement(("DELETE FROM mumies WHERE username=? AND name=? AND color=? AND x=? AND y = ?;"));
            preStatement.setString(1, username);
            preStatement.setString(2, pig.getName());
            preStatement.setString(3, pig.getColor());
            preStatement.setDouble(4, pig.getX());
            preStatement.setDouble(5, pig.getY());

            preStatement.executeUpdate();
            System.out.println("Из DB успешно удалён  " + pig.getName());
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при удалении Pig из DB");
            return false;
        }
    }

    public boolean removePersonString(String username, String nameOfMumi) { // remove_greater_key
        try {
            PreparedStatement preStatement = connection.prepareStatement(("DELETE FROM mumies WHERE username=? AND name=?;"));
            preStatement.setString(1, username);
            preStatement.setString(2, nameOfMumi);


            preStatement.executeUpdate();
            System.out.println("Из DB успешно удалён  " + nameOfMumi);
            return true;
        } catch (Exception e) {
            System.out.println("Ошибка при удалении Mumi из DB");
            return false;
        }
    }

    public int executeLogin(String login, String pass) {
        try {
            String hash = DataBaseConnection.encryptString(pass);
            PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM users WHERE username=? and hash=?;");//Достаём из DB юзера по его нику и хеш-паролю
            preStatement.setString(1, login);// Устанавливаем наш логин в инъекцию
            preStatement.setString(2, hash);// Устанавливаем наш хеш-пароль в инъекцию
            ResultSet result = preStatement.executeQuery();//Достаём значение из БД
            if (result.next()) {//Если такой логин есть, то мы залогинились
                return 0;
            } else {//Если нет такого, то проверяем его логин
                PreparedStatement preStatement2 = connection.prepareStatement("SELECT * FROM users WHERE username=?;");//Достаём из DB юзера по его нику
                preStatement2.setString(1, login);// Устанавливаем наш логин в инъекцию
                ResultSet result2 = preStatement2.executeQuery();//Достаём значение из БД
                if (result2.next()) {//Если такой логин есть, то
                    return 2;
                } else {//Нет такого логина
                    return 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при попытки залогиниться");
            return -1;
        }
    }

    public int executeRegister(String login, String pass) {
        try {

            PreparedStatement ifLog = connection.prepareStatement("SELECT * FROM users WHERE username=?;"); //Достаём из юзеров по логину
            ifLog.setString(1, login);// Устанавливаем наш логин в инъекцию

            ResultSet result = ifLog.executeQuery(); //Результат из таблицы юзеров
            if (result.next()) {//если такой аккаунт уже есть, то выходим и сообщаем, что уже зарегистрированы
                return 0;
            }

            String hash = encryptString(pass); //Создание хеша пароля
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, hash) VALUES (?, ?);"); //Сохраняем юзера в ДБ
            statement.setString(1, login); //Сохраняем его логин
            statement.setString(2, hash);//и пароль-хэш
            statement.executeUpdate(); //Обновляем DB
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка во время регистрации");
            return -1;
        }
    }


    public static String getToken() { //Создание паролья todo
        try {
            int leftLimit = 97; // буква 'a'
            int rightLimit = 122; // буква 'z'
            int targetStringLength = 8; //Длина пароля
            Random random = new Random(); // Получаем рандомную строку
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }

            //шифрование случайной строки
            return buffer.toString();

        } catch (Exception e) {
            return null;
        }
    }

    public static String encryptString(String input) { //Шифруем
        try {
            // getInstance() метод, который вызывает алгоритм хеширования, к примеру MD2
            MessageDigest md = MessageDigest.getInstance("MD2");


            //digest() нужен нам для вычисления дайджеста входящей строки
            byte[] messageDigest = md.digest(input.getBytes());

            // Преобразование массива байтов в знаковое представление
            BigInteger no = new BigInteger(1, messageDigest);

            // Преобразование дайджеста сообщения в шестнадцатеричное значение
            StringBuilder hashText = new StringBuilder(no.toString(16));

            //для надёжности хэша
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }

            // возвращаем
            return hashText.toString();
        }

        // При указании неправильных алгоритмов дайджеста сообщений
        catch (NoSuchAlgorithmException e) {
            System.out.println("Ошибка во время генерации hashText'а пароля");
            e.getMessage();
            return null;
        }
    }


}