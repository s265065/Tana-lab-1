package com.company;

import java.io.*;
import java.net.*;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;

public class Server {
    private DatagramSocket datagramSocket;
    private int port;
    private ArrayList<Pig> arrayList;
    private Command command;
    private static DataBaseConnection db;

    public ArrayList<Pig> getArrayList() {
        return arrayList;
    }


    public Server(int port) throws IOException {
        this.port = port; //указываем порт на котором сокет будет слушать
        datagramSocket = new DatagramSocket(port);
        System.out.println("Сервер создан ");//Уведомляем пользователя
        System.out.println("Адрес сервера: " + InetAddress.getLocalHost());//Выводим адресmai
        System.out.println("Порт сервера: " + port); //Выводим порт

        arrayList = new ArrayList<>();
        db = new DataBaseConnection();//Подключаемся к бд
        initTables();
        //db.addToDB(new Bear("Vinni"));
        db.loadPersons(arrayList);//Загружаем из бд в коллекцию объекты

    }

    private static void initTables() {
        System.out.println("Проверка таблиц...");
            try {
                Statement statement = db.getConnection().createStatement();
                statement.execute("create table if not exists pigs " +
                        "(id serial primary key not null, name text, creationdate timestamp, color text, x double , y double , arcons text, owner text)"
                );
                statement.execute("create table if not exists users (" +
                        "id serial primary key not null, username text, hash text)"
                );

            } catch (SQLException e) {
                System.out.println("Не получилось создать таблицы: " + e.toString());
            }
        }
    private static void autoCreateTable(String name, String structure) {
        try {
            DatabaseMetaData metaData = db.getConnection().getMetaData();
            if (
                    !metaData.getTables(
                            null,
                            null,
                            name,
                            new String[]{"TABLE"}
                    ).next()
            ) {
                db.getConnection().createStatement().execute("create table if not exists " +  name +" (" + structure + ")");
                System.out.println("Создана таблица " + name);
            }
        } catch (SQLException e) {
            System.out.println("Не получилось создать таблицу " + name + ": " + e.getMessage());
        }
    }

    private void listening() throws IOException {

        CommandManager commandManager;//Объект, который будет руководить обработкой команд


        while (true) {

            byte[] bytes = new byte[1024]; //Создаём байтовый массив для пакетов
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);//Пакет, который приходит
            datagramSocket.receive(datagramPacket);//Принимаем пакет

            InetAddress inetAddress = datagramPacket.getAddress();//Узнаём адрес, который потом будем передавать в CommandManager
            int outPort = datagramPacket.getPort();//Узнаём порт, который потом будем передавать в CommandManager


            try (ByteArrayInputStream bais = new ByteArrayInputStream(datagramPacket.getData());
                 ObjectInputStream ois = new ObjectInputStream(bais);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {


                command = (Command) ois.readObject(); //Сохраняем команду

                System.out.println("Клиент ввёл команду: " + command.getCommand()); //Узнаём команду
                System.out.println("Данные: " + command.getData()); //Узнаём данные


                System.out.println("Ник пользователя: " + command.getUsername());
                System.out.println("Пароль пользователя: " + command.getPassword());


                //Объект, который будет руководить обработкой команд
                commandManager = new CommandManager(arrayList, command, datagramSocket, outPort, inetAddress, this, db);
                shootDown();
                commandManager.start();

                //   Response response = new Response(handler.handleCommand(command, storage, db));


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }


    }

    public void shootDown() { //Экстренно выходим         //todo переделать возможно
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Меня застрелили, но всё сохранено!");
        }));
    }

    public static void main(String[] args) throws IOException {


        try {

            //int input_port = Integer.parseInt(args[0]); //сохраняем в переменную номер порта
            Server server = new Server(5432); //создали сервер с портом, который указали при запуске


            server.listening(); //принимаем команды от пользователей и создаём потоки, которые взаимодейсвуют с ними


        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Введите только номер порта");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Введите только номер порта");
        } catch (BindException e) {
            System.out.println("Нет доступа к порту, возможно он занят");
        }

    } //мейн сервера

}