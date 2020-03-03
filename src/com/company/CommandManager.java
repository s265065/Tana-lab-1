package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CommandManager extends Thread { //Класс для обработки команды и работы с пользователем
    private ArrayList<Pig> arrayList; //Наша коллекция для работы
    // private File fileFromLoad; //Файл, откуда мы берём данные //todo удалить связ с файлом
    private Command command; // Команда команда
    private InetAddress inetAddress; //Адрес клиента
    private int port; //Порт клиента
    private DatagramSocket datagramSocket;//Сокет
    private Date initDate; //дата
    private Server server; //Наш сервер
    private DataBaseConnection db;//Наша БД


    private String username = null;
    private String password = null;


    public ArrayList<Pig> getArrayList() {
        return arrayList;
    } //возвращает коллекцию


    @Override
    public void run() {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();//Создаём поток для отправки байтов
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {//Поток для объектов

            byte[] bytes = runServer(command); //Записываем данные с обработчика команд
            if (!command.getCommand().equals("show")) {
                System.out.println("Данные для отправки:" + new String(bytes));
            }
            Response response = new Response(); //Создём ответ
            response.setResponse(bytes);//Устанавливаем байты
            oos.writeObject(response);//Записываем объект
            oos.flush(); //
            bytes = baos.toByteArray();//
            DatagramPacket datagramPacket1 = new DatagramPacket(bytes, bytes.length, inetAddress, port); //Создаём пакет для отправки клиенту

            System.out.println("Данные отправляются на хост: " + inetAddress); //Выводим данные о клиентском адресе
            System.out.println("Данные отправляются на порт: " + port);//Выводим данные о клиентском порте


            datagramSocket.send(datagramPacket1); //Отправляем пакет пользователю
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //todo удалить связ с файлом
    public CommandManager(ArrayList<Pig> arrayList, Command command, DatagramSocket datagramSocket, int outport, InetAddress inetAddress, Server server, DataBaseConnection db) {
        this.command = command; //Устанавливаем команду от клиента
        this.datagramSocket = datagramSocket; //Устанавливаем сокет
        this.port = outport;//Устанавливаем порт клиента
        this.inetAddress = inetAddress;// Устанавливаем адрес клиента
        initDate = new Date();//создаём новые время
        this.arrayList = arrayList;  //Новая коллекция
        this.server = server;//Устанавливаем наш сервер
        this.db = db;
    }//Правильный конструктор


    public byte[] info() { // всё работает
        //todo на русский
        return ("Type collection is  ConcurrentSkipListMap and contains Mumi objects.\n" + " Time of initialize: " + initDate + "\n" + "contains " + arrayList.size() + " elements now.\n").getBytes(StandardCharsets.UTF_8);
    } //выводим на консоль информацию о коллекции


    public byte[] show() {
        if (!arrayList.isEmpty()) { //Если наша коллекция не пуста:

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();//Создаём стримы для записи в объекты
                 ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
                oos.writeObject(arrayList); //Записываем объект
                oos.flush();//выталкиваем данные
                byte[] data = outputStream.toByteArray(); //записываем данные
                return data; //Возвращаем данные
            } catch (IOException e) {
                e.printStackTrace();
            }
        }// Если всё таки пуста, то выводим соответствующее сообщение
        return "Нет элементов в коллекции".getBytes();


    } //Выводим на консоль элементы коллекции

    public byte[] help() {
        return ("Your commands:\n" +
                "insert {String key} {element}: add new element with this key\n" +
                "show: show elements of collection in string\n" +
                "info: show info about collection\n" +
                "remove_greater {element}: delete all of elements that \"bigger\" than this\n" +
                "remove_greater_key {String key}: delete all of elements that key \"bigger\" than this\n" +
                "remove {String key}: delete element on this key \n" +
                "save: save collection in file\n" +
                "exit: exit from programm and save the collection").getBytes(StandardCharsets.UTF_8);
    }//Выводим пользователю помощь

    /*public byte[] remove(String key) {

        synchronized (mumiConcurrentSkipListMap) {
            if (mumiConcurrentSkipListMap.containsKey(key)) {//если содержит коллекция значение по ключу, то удаляем

                if (mumiConcurrentSkipListMap
                        .values()//значения
                        .removeIf(x ->
                                x.getName().equals(key) &&
                                        x.getOwner().equals(username))) {
                    db.removePersonString(username, key);
                } else return "У вас нет прав на удаление этого объекта!".getBytes();


                //   db.loadPersons(mumiConcurrentSkipListMap);


                return "Объект удалён".getBytes(); //Возвращаем, что элемент удалён

            } else return ("Нет такого элемента").getBytes(); //Иначе отсылаем, что нет такого
        }
    }//удаление элемента по ключу*/


    public byte[] insert(Pig pig) throws FileNotFoundException {
//        synchronized (mumiConcurrentSkipListMap) {
//            if (mumiConcurrentSkipListMap.containsKey(mumi.getName())) {
//                return "Коллекция уже содержит этот элемент!".getBytes();
//            }
//            if (name.equals(mumi.getName())) {//проверяем, сходится ли ключ с именем Mumi
//                mumiConcurrentSkipListMap.put(name, mumi); //
//                db.addToDB(mumi, username);
//
//                return "Элемент сохранён".getBytes();
//
////todo перенести валидацию на клиента
//            } else return "Ключ должен совпадать с именем Mumi!".getBytes();
//        }
        db.addToDB(pig);
        return "ok".getBytes();
    }//добавление объекта с ключом, совпадающим с именем

/*
    public byte[] remove_greater_key(String name) throws FileNotFoundException {
        synchronized (mumiConcurrentSkipListMap) {
            int before = mumiConcurrentSkipListMap.size();


            mumiConcurrentSkipListMap
                    .values()
                    .stream()
                    .filter(x ->
                            (x.getOwner().equals("all") || username.equals(x.getOwner()))) // Фильтр, если клиент=владелец
                    .filter(x -> x.getName().length() > name.length())//Сортируем, имена больше данного
                    .forEach(x -> { //Удаляем такие из бд
                        db.removePerson(username, x);
                    });


            mumiConcurrentSkipListMap.values().removeIf(x -> x.getOwner().equals(username) && (x.getName().length() > name.length()));

            return ("Удалено из коллекции " + (before - mumiConcurrentSkipListMap.size()) + " элементов").getBytes();
        }

    }//удалить


    public byte[] remove_greater(Mumi m) throws FileNotFoundException {

        synchronized (mumiConcurrentSkipListMap) {

            int before = mumiConcurrentSkipListMap.size();


            mumiConcurrentSkipListMap
                    .values()
                    .stream()
                    .filter(x ->
                            (x.getOwner().equals("all")
                                    || username.equals(x.getOwner())))
                    .filter(x -> x.getAge() > m.getAge())
                    .forEach(x -> {
                        db.removePerson(username, x);
                    });
//Может удалять чужие объекты


            mumiConcurrentSkipListMap
                    .values()
                    .removeIf(x -> x.getOwner().equals(username) && (x.getAge() > m.getAge()));


            return ("Удалено " + (before - mumiConcurrentSkipListMap.size()) + " элементов").getBytes();
        }
    }*/
    public Pig createPigFromString(String str){
        String[] tmp = str.split(" ");
        Pig pig = new Pig(tmp[0], tmp[1], Double.parseDouble(tmp[3]), Double.parseDouble(tmp[4]), this.username);
        return pig;
    }

    public byte[] runServer(Command command) throws FileNotFoundException {

        try {
            String commandString = command.getCommand(); //Достаём команду
            String dataCommandString = (String) command.getData();//Достаём данные
            username = command.getUsername();
            password = command.getPassword();//Достаём пароль юзера
            Bear forLoad = null; // инициализация объекта, который будет использоваться для загрузки/удаление и т.д.
            if (commandString.equals("connecting")) {
                return "connected".getBytes();
            } else if (commandString.equals("register")) {
                int resultR = 1;
                db.executeRegister(username, password);//Совершаем попытку зарегистрироваться, по цифре понимаем результат
                if (resultR == 1) { // Зарегистрировались
                    return "Email registration is approved!".getBytes();
                } else if (resultR == 0) {// Аккаунт такой уже есть
                    return "Вы уже регистрировались!".getBytes();
                } else {// Какая-то ошибка
                    return "Регистрация невозможна".getBytes();
                }
            } else if (commandString.equals("login")) {
                int result = db.executeLogin(username, password); //Совершаем попытку залогиниться, по цифре понимаем результат
                if (result == 0) { //Мы залогинились
                    return "Log in".getBytes();
                } else if (result == 1) { // Аккаунта нет, надо зарегистрироваться
                    return "Вам нужно для начала зарегистрироваться!".getBytes();
                } else if (result == 2) {//Такой аккаунт есть, но пароль неправильный
                    return "Неправильный пароль!".getBytes();
                } else {
                    return "Невозможно войти".getBytes();
                }

            }

            if (db.cheakOnPass(password, username)) {
                db.loadPersons(arrayList); //синхронизирую всегда бд и коллекцию
                switch (commandString) {//ищим подходящую команду


                    /*case "remove_greater_key":
                        return remove_greater_key(dataCommandString);


                    case "remove_greater":
                        forLoad = gson.fromJson(dataCommandString, Mumi.class); //переводим аргумент из строки JSON в объект
                        return remove_greater(forLoad); //удаляем по аргументу*/


                    case "add":

                        return insert(createPigFromString(dataCommandString));

                    case "show":

                        return show();

                    /*case "remove":

                        return remove(dataCommandString);*/

                    case "info":
                        return info();


                    /*case "insert"://todo проверку+
                        forLoad = gson.fromJson(dataCommandString, Mumi.class);
                        forLoad.setDateTime(OffsetDateTime.now()); //todo перенести

                        if (!mumiConcurrentSkipListMap.containsValue(forLoad)) {
                            return insert(key, forLoad);

                        } else {
                            return "Элемент не добавлен, потому что уже есть в коллекции!".getBytes();
                        }
*/

                    case "help":
                        return help();


                }
            } else {
                return "Пользователь, не пытайся засрать мне бд))".getBytes();
            }

        } catch (Exception e) {
            System.out.println("АШЫБКА");
            e.printStackTrace();
            return "какая-то ошибка в runserver".getBytes();
        }

        return "aaaa".getBytes();
    }
}
