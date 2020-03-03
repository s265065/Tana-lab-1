package com.company;
//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";
    private DatagramChannel udpChannel;
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private int port;
    private SocketAddress socketAddress;
    private Command command = new Command("connection");

    private Scanner scanner = new Scanner(System.in); //Сканнер для считывания пароля/почты/ника
    private String mail; //Почта пользователя
    private boolean isAuth = false; //проверка на зарегистрованность/залогиненность
    private java.io.Console console = System.console();//todo
    private String username; //Ник пользователя
    private String password; //Пароль пользователя

    public Client() {

    }

    private Client(String addr, int port) throws IOException {

        this.port = port;
        this.udpSocket = new DatagramSocket();
        this.socketAddress = new InetSocketAddress(addr, port);
        this.udpChannel = DatagramChannel
                .open(); //открываем канал
        helpWithAuth();
    }


    private void testConnection() throws IOException {
        System.out.println("Пробуем связаться с сервером..."); //Выводим сообщение
        Command commandConnecting = new Command(); //Создаём команду
        commandConnecting.setCommand("connecting");//Со значением "connecting"

        ByteBuffer buffer = createByteBuffer(commandConnecting); //Создаём буффер из команды


        udpChannel.socket().setSoTimeout(1000);//Ставим сокету таймер, что через 10 секунд откл

        boolean connected = false;
        String connectString;

        for (int i = 1; i < 11; i++) {
            System.out.println("Попытка №" + i);
            try {
                udpChannel.send(buffer, socketAddress);
            } catch (UnresolvedAddressException e) {
                System.out.println("Невозможно подключиться к серверу");
                System.exit(1);
            }
            //Пытаемся отправить серверу команду
            buffer.clear();//Отчищаем буфер
            try {
                udpChannel.socket().receive(new DatagramPacket(buffer.array(), buffer.array().length));//пытаемся принять пакет
            } catch (SocketTimeoutException e) {
                continue;
            }


            try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());//Создаём каналы для загрузки ответа
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Response response = (Response) ois.readObject();
                connectString = new String(decodeResponse(response));//узнаём ответ


            } catch (IOException | ClassNotFoundException e) {
                connectString = "Not connected";
            }

            if (!(connectString.equals(null) && (connectString.equals("Not connected")))) {
                connected = true;
                break;
            }
        }


        if (connected) {//если подключены, то всё окей
            System.out.println("Удалось подключиться к серверу!");
        } else {//иначе информируем пользователя и выходим
            System.err.println("Сервер в данный момент недоступен...");
            System.exit(1);
        }


    }

    public void helpWithAuth() {
        System.out.println("Команда register - зарегистрироваться\n" +
                "login - залогиниться, если есть уже такой аккаунт");

    } //Помощь с регистрацией пользователя

    public void setIsAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }

    private void shootDown() { //Экстренно выходим
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Пока-пока!");
        }));
    } //todo переименовать и возможно переделать

    public static void main(String[] args) throws IOException {
        try {

            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.shootDown();
            Response response = new Response();
            System.out.println("Клиент запустился на " + InetAddress.getLocalHost());
            System.out.println("Хост: " + args[0]);
            System.out.println("Порт клиента: " + args[1]);
            client.testConnection();
            while (true) {
                //отправляем команды серверу
                client.sendToServer();

            }


        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Введите строковый адрес, а потом номер порта!");
        } catch (NoSuchElementException ex) {
            System.out.println(ANSI_PURPLE + "Оу е, вы нашли пасхалку))");
            System.out.println(ANSI_BLUE + "Блять, да мне похуй на тебя, блять, слушай, какая у тебя там тачка, блять,\n квартиры, срачки там блять, яхты, всё, мне похуй,\n хоть там \"Бэнтли\", хоть блять нахуй \"Майбах\", хоть \"Роллс-Ройс\", хоть \"Бугатти\" блять,\n хоть стометровая яхта, мне на это насрать, понимаешь?\n Сколько ты там, кого ебешь, каких баб, каких значит вот этих самок шикарных или атласных,\n блять в космос ты летишь, мне на это насрать, понимаешь?\n Я, блять, в своем познании настолько преисполнился, что я как будто бы уже сто триллионов миллиардов лет, блять, проживаю на триллионах и триллионах таких же планет,\n как эта Земля, мне этот мир абсолютно понятен, и я здесь ищу только одного, блять, - покоя,\n умиротворения и вот этой гармонии, от слияния с бесконечно вечным,\n от созерцания великого фрактального подобия и от вот этого замечательного всеединства существа,\n бесконечно вечного, куда ни посмотри, хоть вглубь - бесконечно малое, хоть ввысь - бесконечное большое, понимаешь?\n А ты мне опять со своим вот этим блять, иди суетись дальше, это твоё распределение, это твой путь и твой горизонт познания и ощущения твоей природы,\n он несоизмеримо мелок по сравнению с моим, понимаешь?\n Я как будто бы уже давно глубокий старец, бессмертный, ну или там уже почти бессмертный,\n который на этой планете от её самого зарождения, ещё когда только Солнце только-только сформировалось как звезда, и вот это газопылевое облако,\n вот, после взрыва, Солнца, когда оно вспыхнуло, как звезда, начало формировать вот эти коацерваты, планеты, понимаешь,\n я на этой Земле уже как будто почти пять миллиардов лет блять живу и знаю её вдоль и поперёк этот весь мир,\n а ты мне какие-то... мне похуй на твои тачки, на твои блять нахуй яхты, на твои квартиры, там, на твоё благо.\n Я был на этой планете бесконечным множеством, и круче Цезаря, и круче Гитлера, и круче всех великих, понимаешь, был,\n а где-то был конченым говном, ещё хуже, чем здесь.\n Я множество этих состояний чувствую.\n Где-то я был больше подобен растению, где-то я больше был подобен птице, там, червю,\n где-то был просто сгусток камня, это всё есть душа, понимаешь?\n Она имеет грани подобия совершенно многообразные, бесконечное множество.\n Но тебе этого не понять, поэтому ты езжай себе блять, мы в этом мире как бы живем разными ощущениями и разными стремлениями,\n соответственно, разное наше и место, разное и наше распределение.\n Тебе я желаю все самые крутые тачки чтоб были у тебя, и все самые лучше самки чтобы раздвигали ноги перед тобой,\n чтобы раздвигали перед тобой щели, на шиворот-навыворот, блять, перед тобой, как ковёр,\n это самое, раскрывали, растлевали, растлали, и ты их чтобы ебал до посинения, докрасна,\n вон, как Солнце закатное, и чтоб на лучших яхтах, и на самолётах летал, и кончал прямо с иллюминатора,\n и делал всё, что только в голову могло прийти и не прийти, если мало идей, обращайся ко мне,\n я тебе на каждую твою идею предложу сотню триллионов, как всё делать.\n Ну а я всё, я иду как глубокий старец, узревший вечное, прикоснувшийся к Божественному, сам стал богоподобен и устремлен в это бесконечное,\n и который в умиротворении, покое, гармонии, благодати, в этом сокровенном блаженстве пребывает, вовлеченный во всё и во вся, понимаешь,\n вот и всё, в этом наша разница.\n Так что я иду любоваться мирозданием, а ты идёшь преисполняться в ГРАНЯХ каких-то,\n вот и вся разница, понимаешь, ты не зришь это вечное бесконечное, оно тебе не нужно.\n Ну зато ты, так сказать, более активен, как вот этот дятел долбящий, или муравей, который очень активен в своей стезе,\n поэтому давай, наши пути здесь, конечно, имеют грани подобия, потому что всё едино,\n но я-то тебя прекрасно понимаю, а вот ты меня - вряд ли,\n потому что я как бы тебя в себе содержу, всю твою природу, она составляет одну маленькую там песчиночку, от того что есть во мне,\n вот и всё, поэтому давай, ступай, езжай, а я пошел наслаждаться нахуй блять прекрасным осенним закатом на берегу теплой южной реки.\n Всё, пиздуй-бороздуй, и я попиздил, нахуй. ");
        }

    }

    public String[] readAndParseCommand() {
        System.out.println("Введите команду");//todo для оптимизации можно убрать сканнер новый
        Scanner scanner = new Scanner(System.in);//Считывание команды
        String command = "";
        String[] fullСommand;

        do command = command + scanner.nextLine();
        while ((command.length() - command.replaceAll("}", "").length()) < (command.length() - command.replaceAll("\\{", "").length()));

        if (!command.split(" ", 2)[0].equals("insert")) {//если не insert,то:

            command = command.trim(); // удаляем пробелы в начале и в конце

            if (command.contains("\t")) { //если строка содержит табуляцию, то заменяем на пробелы
                command = command.replaceAll("\t", " ");
            }

            fullСommand = command.split(" ", 2); //в массив помещаем 2 троки(название и "аргумент")
            if (fullСommand.length >= 2) {
                if (fullСommand[1].contains(" ")) {//если аргументов является пробел, то заменяем его на ничего, тем самым
                    fullСommand[1] = fullСommand[1].replaceAll(" ", "");//делаем аргумент аргументом
                }
            }

        } else {//если insert
            if (command.split(" ", 3).length < 3) { // если оказывается, что команда
                fullСommand = command.split(" ", 2);
            } else {
                fullСommand = command.split(" ", 3);
                while (fullСommand[1].contains("  ")) {
                    fullСommand[1] = fullСommand[1].replaceAll("  ", "");
                }
                while (fullСommand[2].contains("  ")) {
                    fullСommand[2] = fullСommand[2].replaceAll("  ", "");
                }
            }

        }
        return fullСommand;

    }

    /*public boolean validateInputData(String[] command) { //проверяем введенные пользователем данные
        Bear forLoad;
        if (command[0].equals("remove_greater_key") ||
                command[0].equals("info") ||
                command[0].equals("remove_greater") ||
                command[0].equals("show") ||
                command[0].equals("insert") ||

                command[0].equals("remove") ||
                command[0].equals("exit") ||
                command[0].equals("help") ||
                command[0].equals("login") ||
                command[0].equals("register")) { // если содержит команду, то:


            if (command[0].equals("insert") &&//если insert
                    (command.length > 1) &&//и имеет ключ
                    (command[1].contains("{") ||//то ключ должен быть
                            command[1].contains("}"))) {//без фигурных скобок
                System.out.println("Ключ должен быть без фигурных скобок");
                return false;
            }
            //продолжение


            if ((command.length == 1) &&            //todo Можно просто переделать под специальные команды, идти от обратного
                    !command[0].equals("show") &&
                    !command[0].equals("help") &&
                    !command[0].equals("info") &&
                    !command[0].equals("exit") &&

                    !command[0].equals("login") &&
                    !command[0].equals("register")) {
                System.out.println("Нужен аргумент");
                return false;//передана команда без аргумента, для которой он нужен
            }


            if ((command.length == 2) &&
                    !(command[0].equals("insert")) &&
                    !command[0].equals("show") &&

                    !command[0].equals("info") &&
                    !command[0].equals("exit") &&
                    !command[0].equals("help") &&
                    !command[0].equals("login") &&
                    !command[0].equals("register")) {
                //команда с аргументом и она не insert и не одноаргументная
                /*switch (command[0]) {
                    case "remove_greater"://если команда remove_greater, то
                        try {

                            Gson gson = new Gson();
                            forLoad = gson.fromJson(command[1], Mumi.class);
                            System.out.println(forLoad);
                            //переводим аргумент из строки JSON в объект
                            if (forLoad.getAge() > 0 && //если возраст больше 0          //todo проверить валидацию

                                    (forLoad.getName() != null) &&  //если введено имя

                                    (forLoad.getHealth() > 0) && //если здоровье больше 0
                                    (command[1].contains("\"x\"")) &&
                                    (command[1].contains("\"y\"")) &&


                                    (forLoad.chechOnEnum())) {//если чувства и настроение введены правильно

                            } else {
                                System.out.println("Не все поля команды remove_greater заполнены!");
                                return false;//выводим, что объект не валиден
                            }
                            break;

                        } catch (JsonSyntaxException | NullPointerException ex) {
                            System.out.println("Ошибка в заполнении аргумента команды remove_greater!");
                            return false;
                        }
                }

            }

            if (command[0].equals("insert")) { //если команда insert и длина не меньше 3, то выполняется
                if (command.length < 3) {//если длина меньше 3, то неправильно введено
                    System.out.println("Требуются и ключ и значение");
                    return false;
                } else {
                    try {

                        Gson gson = new Gson();
                        forLoad = gson.fromJson(command[2], Mumi.class);//загружаем из JSON аргумент команды

                        if ((forLoad.getName() != null) &&     //todo проверить валидацию
                                //есть имя
                                (forLoad.getAge() > 0) &&//возраст больше 0
                                (forLoad.getHealth() > 0) &&//есть здоровье
                                (command[2].contains("\"x\"")) &&
                                (command[2].contains("\"y\"")) &&

//todo НЕ ЗАБЫТЬ ПЕРЕНЕСТИ ВАЛИДАЦИЮ ДЛЯ INSERT НА СЕРВЕР, ПОТОМУ ЧТО ТОЛЬКО ТАМ ПРОИСХОДИТ СРАВНЕНИЕ ОБЪЕКТОВ
                                (forLoad.chechOnEnum())) {//есть чувства и эмоции
                            if (forLoad.getName().equals(command[1])) {//если имя совпадает с ключом

                            } else {
                                System.out.println("Ключ должен совпадать с name");
                                return false;//выводим сообщение
                            }

                        } else {
                            System.out.println("Неправильно заданы поля или отсутсвуют!");
                            return false;//выводим сообщение

                        }

                    } catch (JsonSyntaxException ex) {//ловим ошибку и выводим сообщение
                        System.out.println("Неправильно задан объект");
                        return false;
                    } catch (NullPointerException ex) {//ловим ошибку и выводим сообщение
                        System.out.println("Неправильно задан объект");
                        return false;
                    }
                }
            }

            if ((command[0].equals("show") || //проверка на то, что для односложных команд нет
                    //аргумента
                    command[0].equals("info") ||
                    command[0].equals("exit") ||
                    command[0].equals("help") ||
                    command[0].equals("login") ||
                    command[0].equals("register"))
                    && command.length != 1) {
                System.out.println("Эта команда не требует аргумент");
                return false;//выводим сообщение
            }
            return true;
        }

        System.out.println("Такой команды не существует");
        return false;

    }*/

    public Command createCommand(String[] stringFromUser) {
        command = new Command();

        if (stringFromUser[0].equals("exit")) {

            System.exit(1);
        } //todo возможно переделать

        if (true) {
            command.setCommand(stringFromUser[0]);
            command.setUsername(username);
            command.setPassword(password);

            if (stringFromUser[0].equals("register")) {
                inputUsernameAndEmail();

                command.setUsername(username);
                command.setPassword(password);
                System.out.println("Создан запрос");
                System.out.println("Команда: " + stringFromUser[0]);
                System.out.println("Ваш логин " + username);
                return command;

            }

            if (stringFromUser[0].equals("login")) {

                inputLoginAndPass();
                command.setUsername(username);
                command.setPassword(password);
                System.out.println("Создан запрос");
                System.out.println("Команда: " + stringFromUser[0]);
                System.out.println("Ваш логин " + username);
                return command;

            }


            if (isAuth) {

                if (stringFromUser[0].equals("show") ||
                        stringFromUser[0].equals("info") ||

                        stringFromUser[0].equals("help")) {

                    System.out.println("Создан запрос");
                    System.out.println("Команда: " + stringFromUser[0]);
                    return command;


                } else if (stringFromUser[0].equals("remove_greater_key") ||
                        stringFromUser[0].equals("remove_greater") ||
                        stringFromUser[0].equals("remove")) {

                    command.setData(stringFromUser[1]);
                    System.out.println("Создан запрос");
                    System.out.println("Команда: " + stringFromUser[0]);
                    System.out.println("Данные: " + stringFromUser[1]);
                    return command;

                } else if (stringFromUser[0].equals("insert")) {

                    command.setData(stringFromUser[1]);
                    System.out.println("Создан запрос");
                    System.out.println("Команда: " + stringFromUser[0]);
                    System.out.println("Ключ объекта: " + stringFromUser[1]);
                    System.out.println("Данные: " + stringFromUser[2]);
                    return command;
                }
            } else {
                System.out.println("Вы не зарегистрированы/не зашли под своим логином! \n Введите команду register или login!");
            }
        } else System.out.println("Невозможно создать запрос");

        return null;
    }

    private ByteBuffer createByteBuffer(Command command) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(command);
            oos.flush();
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            buffer.put(baos.toByteArray()).flip();

            System.out.println("Буфер успешно создан");
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Буфер не создан!");
        return null;
    }


    private void sendToServer() throws IOException {

        ByteBuffer byteBuffer;
        String[] stringFromUser = readAndParseCommand();
        Command command = createCommand(stringFromUser);
        if (command != null) {
            byteBuffer = createByteBuffer(command);


            udpChannel.send(byteBuffer, socketAddress);
            System.out.println("Буфер успешно отправлен");


            byteBuffer.clear();
            try {

                this.udpChannel.socket().receive(new DatagramPacket(byteBuffer.array(), byteBuffer.array().length));
                System.out.println("Буфер успешно получен");


            } catch (SocketTimeoutException e) {
                System.err.println("Отключение от хоста");
                testConnection();

            }
            try (ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer.array());
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Response response = (Response) ois.readObject();
                String output = new String(decodeResponse(response));

                System.out.println("Данные с сервера: " + output);

                //проверка на
            } catch (IOException e) {

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {

            }
        } else System.out.println("Невозможно отправить пакет");


    }

    private void inputUsernameAndEmail() {

        System.out.println("Для регистрации, пожалуйста, введите свой никнейм без пробелов и табуляций: ");


        username = scanner.nextLine();
        while (username.contains(" ") || username.contains("\t")) {
            System.out.println("Ник не может содержать пробелы и табуляции! :-] ");
            System.out.println("Попробуйте ещё раз!");
            username = scanner.nextLine();
        }

        password = DataBaseConnection.getToken();
        //создание запроса с данными
    }

    private void inputLoginAndPass() {
        System.out.println("Чтобы зайти, пожалуйста, введите свой никнейм без пробелов и табуляций: ");
        username = scanner.nextLine();

        while (username.contains(" ") || username.contains("\t")) {
            System.out.println("Ник не может содержать пробелы и табуляции! :-] ");
            System.out.println("Попробуйте ещё раз!");
            username = scanner.nextLine();
        }


        System.out.println("Введите свой пароль: ");
        password = scanner.nextLine();

        while (password.contains(" ") || password.contains("\t")) {
            System.out.println("Пароль не может содержать пробелы и табуляции! :-] ");
            System.out.println("Попробуйте ещё раз!");
            password = scanner.nextLine();
        }

        //password = DataBaseConnection.encryptString(password); //Шифруем пароль
    }

    private byte[] decodeResponse(Response response) {

//        if (command.getCommand().equals("show")) {
//            try (ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) response.getResponse());
//                 ObjectInputStream ois = new ObjectInputStream(bais)) {
//                ConcurrentSkipListMap<String, Mumi> storage = (ConcurrentSkipListMap<String, Mumi>) ois.readObject();
//                synchronized (storage) {
//
//                    for (ConcurrentSkipListMap.Entry<String, Mumi> pair : storage.entrySet()) {
//                        System.out.println("Ключ: ");
//                        System.out.println(pair.getKey());
//                        System.out.println("Данные с сервера: ");
//                        System.out.println(pair.getValue().toString());
//
//
//                    }
//                }
//            } catch (IOException | ClassNotFoundException e) {
//                return (byte[]) response.getResponse();
//            }
//            //todo просто заготовка, переделать
//        } else if (command.getCommand().equals("Email registration is approved!")) {
//            setIsAuth(isAuth);
//            System.out.println("На ваш email было отправлено письмо с паролем!\nВ следующий раз введите просто \"login\"");
//            return "На ваш email было отправлено письмо с паролем!\nВ следующий раз введите просто \"login\"".getBytes(); // todo переделать
//            //todo
//
//        } else if (command.getCommand().equals("login")) {
//            setIsAuth(true);
//            System.out.println("Вы успешно залогинились!");
//
//            return "Вы успешно залогинились!".getBytes();
//            //todo просмотреть всё тут
//        } else {
//            return (byte[]) response.getResponse();
//        }
//        return "show".getBytes();
        String stringForDecode = new String((byte[]) response.getResponse());
        if (command.getCommand().equals("show")) {
            return decodeIfShow(response);
        }
        if (stringForDecode.equals("Registration is approved")) {
            setIsAuth(true);
            return "В следующий раз введите просто \"login\"".getBytes();
        }

        if (stringForDecode.equals("Log in")) {
            setIsAuth(true);
            return "Вы успешно залогинились!".getBytes();
        }
        return (byte[]) response.getResponse();


    }

    private byte[] decodeIfShow(Response response) {


        try (ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) response.getResponse());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            ArrayList<Bear> storage = (ArrayList<Bear>) ois.readObject();
            synchronized (storage) {

                for (int i = 0; i < storage.size(); i++) {
                    System.out.println("Данные с сервера: ");
                    System.out.println(storage.get(i).toString());


                }
                return "show".getBytes();
            }
        } catch (IOException | ClassNotFoundException e) {
            return (byte[]) response.getResponse();
        }
    }


}