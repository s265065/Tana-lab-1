package com.company;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) { //Мейн для тестировки
        ArrayList<Bear> arrayList = new ArrayList<>();
        Bear bear = new Bear("Mishe", "brown");
        Bear bear1 = new Bear("Vinni", "yellow");
        Bear bear2 = new Bear("Pyhh", "white");
        Bear bear3 = new Bear("Sun", "brown");
        Bear bear4 = new Bear("Vit", "brown");
        arrayList.add(bear);
        arrayList.add(bear1);
        arrayList.add(bear2);
        arrayList.add(bear3);
        arrayList.add(bear4);

    }
}