package com.company;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Pig extends Creature implements ThinkthenSpeak{
    private String color;
    private ZonedDateTime dataCreate;
    private ArrayList<Acorn> acorns = new ArrayList<>();
    private double x;
    private double y;
    private String owner;
    Pig(String name, String color, double x, double y, String owner){
        super(name);
        this.color = color;
        this.dataCreate = ZonedDateTime.now();
        this.x = x;
        this.y = y;
        this.owner = owner;
    }
    public String getOwner(){
        return this.owner;
    }
    public ZonedDateTime getDataCreate(){
        return this.dataCreate;
    }
    public String getName(){
        return this.name;
    }
    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public String getColor(){
        return this.color;
    }
    public String getAcrons(){
        String str = "";
        for(int i = 0; i < this.acorns.size(); i ++) {
            int j = this.acorns.get(i).isGrow();
            str = str + "Желудь №"+i+" и он уже вырос до "+this.acorns.get(i).getHeight()+"; ";
        }
        return str;
    }
    @Override
    public void think(){
        System.out.println("Как же там мои желуди");
        System.out.println("Каждый раз когда я думаю о них, они растут:)");
        for(int i = 0; i < this.acorns.size(); i ++){
            int j = this.acorns.get(i).isGrow();
            switch (j){
                case -1:
                    System.out.println("Желудь №"+i+" еще не показался");
                case 1:
                    System.out.println("Желудь №"+i+" уже показался");
                case 2:
                    System.out.println("Желудь №"+i+" ростом уже "+this.acorns.get(i).getHeight());
            }
            this.acorns.get(i).grow();
        }
    }
    public void PlantNew(){
        Acorn acorn = new Acorn();
        this.acorns.add(acorn);
    }
}
